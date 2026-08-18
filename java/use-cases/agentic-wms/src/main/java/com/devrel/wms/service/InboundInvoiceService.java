package com.devrel.wms.service;

import com.devrel.wms.entity.InboundInvoice;
import com.devrel.wms.exception.ConflictException;
import com.devrel.wms.exception.NotFoundException;
import com.devrel.wms.repository.InboundInvoiceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class InboundInvoiceService {

	private final Logger logger = LoggerFactory.getLogger(InboundInvoiceService.class);
	private final InboundInvoiceRepository inboundInvoiceRepository;
	private final InventoryService inventoryService;

	InboundInvoiceService(
			InboundInvoiceRepository inboundInvoiceRepository,
			InventoryService inventoryService) {
		this.inboundInvoiceRepository = inboundInvoiceRepository;
		this.inventoryService = inventoryService;
	}

	public InboundInvoice save(InboundInvoice inboundInvoice) {
		InboundInvoice save = inboundInvoiceRepository.save(
				changeInboundStatus(inboundInvoice, InboundInvoice.InvoiceStatus.PENDING));

		logger.info("inbound Invoice with Id {} saved", save.id());

		return save;
	}

	private InboundInvoice changeInboundStatus(
			InboundInvoice inboundInvoice,
			InboundInvoice.InvoiceStatus status
	) {
		return new InboundInvoice(
				inboundInvoice.id(),
				inboundInvoice.number(),
				inboundInvoice.items(),
				status
		);
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

	public void complete(String number) {

		InboundInvoice inbound = getInbound(number);

		if (inbound.status() != InboundInvoice.InvoiceStatus.RECEIVED) {
			throw new ConflictException("Invoice is not received: " + number);
		}

		inbound.items().forEach(item -> inventoryService.add(item.productCode(), item.quantity()));

		inboundInvoiceRepository
				.save(changeInboundStatus(inbound, InboundInvoice.InvoiceStatus.COMPLETED));
	}

	private InboundInvoice getInbound(String number) {
		return inboundInvoiceRepository.findByNumber(number)
				.orElseThrow(() -> new NotFoundException("Invoice not found: " + number));
	}


}
