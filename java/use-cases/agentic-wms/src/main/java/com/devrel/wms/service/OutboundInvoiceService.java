package com.devrel.wms.service;

import com.devrel.wms.domain.OutboundInvoice;
import com.devrel.wms.domain.StockMovement;
import com.devrel.wms.event.OutboundInvoiceCompleted;
import com.devrel.wms.exception.ConflictException;
import com.devrel.wms.exception.NotFoundException;
import com.devrel.wms.repository.OutboundInvoiceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OutboundInvoiceService {

	private final Logger logger = LoggerFactory.getLogger(OutboundInvoiceService.class);
	private final OutboundInvoiceRepository outboundInvoiceRepository;
	private final InventoryService inventoryService;
	private final StockMovementService stockMovementService;
	private final ApplicationEventPublisher eventPublisher;

	OutboundInvoiceService(
			OutboundInvoiceRepository outboundInvoiceRepository,
			InventoryService inventoryService,
			StockMovementService stockMovementService,
			ApplicationEventPublisher eventPublisher
	) {
		this.outboundInvoiceRepository = outboundInvoiceRepository;
		this.inventoryService = inventoryService;
		this.stockMovementService = stockMovementService;
		this.eventPublisher = eventPublisher;
	}

	public OutboundInvoice save(OutboundInvoice outboundInvoice) {
		OutboundInvoice save = outboundInvoiceRepository.save(
				changeOutboundStatus(outboundInvoice, OutboundInvoice.InvoiceStatus.PENDING));

		logger.info("outbound Invoice with Id {} saved", save.id());

		return save;
	}

	public List<OutboundInvoice> findAll() {
		return outboundInvoiceRepository.findAll();
	}

	private OutboundInvoice changeOutboundStatus(
			OutboundInvoice outboundInvoice,
			OutboundInvoice.InvoiceStatus status
	) {
		return new OutboundInvoice(
				outboundInvoice.id(),
				outboundInvoice.number(),
				outboundInvoice.depositor(),
				outboundInvoice.items(),
				status
		);
	}

	public void check(String number) {
		OutboundInvoice invoice = getOutbound(number);

		if (invoice.items().isEmpty()) {
			throw new IllegalArgumentException("Invoice is empty: " + number);
		}

		invoice.items().forEach(item -> {
			if (item.quantity() <= 0) throw new IllegalArgumentException("Invalid quantity: " + item.quantity());
		});

		outboundInvoiceRepository.save(changeOutboundStatus(invoice, OutboundInvoice.InvoiceStatus.RECEIVED));
	}

	@Transactional
	public void execute(String number) {
		OutboundInvoice outbound = getOutbound(number);

		if (outbound.status() != OutboundInvoice.InvoiceStatus.RECEIVED) {
			throw new ConflictException("Invoice is not received: " + number);
		}

		outbound.items().forEach(item -> {
			inventoryService.add(item.productCode(), outbound.depositor(), -item.quantity());

			stockMovementService.register(
					item.productCode(),
					outbound.depositor(),
					item.quantity(),
					outbound.number(),
					StockMovement.MovementType.OUTBOUND
			);
		});

		outboundInvoiceRepository
				.save(changeOutboundStatus(outbound, OutboundInvoice.InvoiceStatus.COMPLETED));

		eventPublisher.publishEvent(new OutboundInvoiceCompleted(number));

		logger.info("Invoice {} completed", number);
	}

	private OutboundInvoice getOutbound(String number) {
		return outboundInvoiceRepository.findByNumber(number)
				.orElseThrow(() -> new NotFoundException("Invoice not found: " + number));
	}

}