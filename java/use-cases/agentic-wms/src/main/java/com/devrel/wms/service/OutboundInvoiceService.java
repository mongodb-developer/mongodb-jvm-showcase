package com.devrel.wms.service;

import com.devrel.wms.entity.OutboundInvoice;
import com.devrel.wms.entity.StockMovement;
import com.devrel.wms.exception.ConflictException;
import com.devrel.wms.exception.NotFoundException;
import com.devrel.wms.repository.OutboundInvoiceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OutboundInvoiceService {

	private final Logger logger = LoggerFactory.getLogger(OutboundInvoiceService.class);
	private final OutboundInvoiceRepository outboundInvoiceRepository;
	private final InventoryService inventoryService;
	private final StockMovementService stockMovementService;

	private ChatClient chatClient;

	OutboundInvoiceService(
			OutboundInvoiceRepository outboundInvoiceRepository,
			InventoryService inventoryService,
			StockMovementService stockMovementService,
			ChatClient chatClient
	) {
		this.outboundInvoiceRepository = outboundInvoiceRepository;
		this.inventoryService = inventoryService;
		this.stockMovementService = stockMovementService;
		this.chatClient = chatClient;
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
					item.quantity(),
					outbound.number(),
					StockMovement.MovementType.OUTBOUND
			);
		});

		outboundInvoiceRepository
				.save(changeOutboundStatus(outbound, OutboundInvoice.InvoiceStatus.COMPLETED));

		String response = chatClient
				.prompt()
				.system(system -> system
						.param("current_date", LocalDateTime.now()))
				.user("""
                Outbound invoice %s has just been completed.

                Analyze this invoice and determine whether any product requires replenishment.
                Use the available tools autonomously.
                Do not ask the user for additional information.
                If replenishment is necessary, create it.
                After creating a replenishment, notify the depositor using the notification tool.
                If not, take no action.
                """.formatted(number))
				.call()
				.content();

		logger.info("Invoice {} completed. Content: {} ", number, response);
	}

	private OutboundInvoice getOutbound(String number) {
		return outboundInvoiceRepository.findByNumber(number)
				.orElseThrow(() -> new NotFoundException("Invoice not found: " + number));
	}

}