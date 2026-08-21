package com.devrel.wms.service;

import com.devrel.wms.domain.InboundInvoice;
import com.devrel.wms.domain.StockMovement;
import com.devrel.wms.exception.ConflictException;
import com.devrel.wms.exception.NotFoundException;
import com.devrel.wms.repository.InboundInvoiceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InboundInvoiceService {

	private final Logger logger = LoggerFactory.getLogger(InboundInvoiceService.class);
	private final InboundInvoiceRepository inboundInvoiceRepository;
	private final InventoryService inventoryService;
	private final StockMovementService stockMovementService;
	private final DepositorService depositorService;

	InboundInvoiceService(
			InboundInvoiceRepository inboundInvoiceRepository,
			InventoryService inventoryService,
			StockMovementService stockMovementService,
			DepositorService depositorService) {
		this.inboundInvoiceRepository = inboundInvoiceRepository;
		this.inventoryService = inventoryService;
		this.stockMovementService = stockMovementService;
		this.depositorService = depositorService;
	}

	public InboundInvoice save(InboundInvoice inboundInvoice) {
		InboundInvoice withDepositor = new InboundInvoice(
				inboundInvoice.id(),
				inboundInvoice.number(),
				depositorService.toRef(inboundInvoice.depositor()),
				inboundInvoice.items(),
				inboundInvoice.status()
		);

		InboundInvoice save = inboundInvoiceRepository.save(
				changeInboundStatus(withDepositor, InboundInvoice.InvoiceStatus.PENDING));

		logger.info("inbound Invoice with Id {} saved", save.id());

		return save;
	}

	public List<InboundInvoice> findAll() {
		return inboundInvoiceRepository.findAll();
	}

	private InboundInvoice changeInboundStatus(
			InboundInvoice inboundInvoice,
			InboundInvoice.InvoiceStatus status
	) {
		return new InboundInvoice(
				inboundInvoice.id(),
				inboundInvoice.number(),
				inboundInvoice.depositor(),
				inboundInvoice.items(),
				status
		);
	}

	public InboundInvoice update(String number, InboundInvoice inboundInvoice) {
		InboundInvoice current = getInbound(number);

		if (current.status() != InboundInvoice.InvoiceStatus.PENDING) {
			throw new ConflictException("Only a pending invoice can be edited: " + number);
		}

		if (inboundInvoice.items() == null || inboundInvoice.items().isEmpty()) {
			throw new IllegalArgumentException("Invoice must have at least one item: " + number);
		}

		inboundInvoice.items().forEach(item -> {
			if (item.quantity() == null || item.quantity() <= 0) {
				throw new IllegalArgumentException("Invalid quantity: " + item.quantity());
			}
		});

		InboundInvoice updated = inboundInvoiceRepository.save(new InboundInvoice(
				current.id(),
				current.number(),
				inboundInvoice.depositor() == null ? current.depositor() : inboundInvoice.depositor(),
				inboundInvoice.items(),
				InboundInvoice.InvoiceStatus.PENDING
		));

		logger.info("Inbound invoice {} updated", number);

		return updated;
	}

	public void check(String number) {
		InboundInvoice invoice = getInbound(number);

		if (invoice.items().isEmpty()) {
			throw new IllegalArgumentException("Invoice is empty: " + number);
		}

		invoice.items().forEach(item -> {
			if (item.quantity() <= 0) throw new IllegalArgumentException("Invalid quantity: " + item.quantity());
		});

		inboundInvoiceRepository.save(changeInboundStatus(invoice, InboundInvoice.InvoiceStatus.RECEIVED));
	}

	@Transactional
	public void complete(String number) {

		InboundInvoice inbound = getInbound(number);

		if (inbound.status() != InboundInvoice.InvoiceStatus.RECEIVED) {
			throw new ConflictException("Invoice is not received: " + number);
		}

		inbound.items().forEach(item -> {
			inventoryService.add(item.productCode(), inbound.depositor(), item.quantity());

			stockMovementService.register(
					item.productCode(),
					inbound.depositor(),
					item.quantity(),
					inbound.number(),
					StockMovement.MovementType.INBOUND
			);
		});

		inboundInvoiceRepository
				.save(changeInboundStatus(inbound, InboundInvoice.InvoiceStatus.COMPLETED));

		logger.info("Invoice {} completed", number);
	}

	private InboundInvoice getInbound(String number) {
		return inboundInvoiceRepository.findByNumber(number)
				.orElseThrow(() -> new NotFoundException("Invoice not found: " + number));
	}


}
