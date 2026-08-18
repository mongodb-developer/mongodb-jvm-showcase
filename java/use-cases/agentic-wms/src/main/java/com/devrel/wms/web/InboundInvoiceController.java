package com.devrel.wms.web;

import com.devrel.wms.entity.InboundInvoice;
import com.devrel.wms.service.InboundInvoiceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/inbound-invoice")
public class InboundInvoiceController {

	private final InboundInvoiceService inboundInvoiceService;

	InboundInvoiceController(InboundInvoiceService inboundInvoiceService) {
		this.inboundInvoiceService = inboundInvoiceService;
	}

	@PostMapping
	public ResponseEntity<InboundInvoice> create(@RequestBody InboundInvoice inboundInvoice) {
		InboundInvoice created = inboundInvoiceService.save(inboundInvoice);

		return ResponseEntity
				.created(URI.create("/inbound-invoice/" + created.number()))
				.body(created);
	}

	@PostMapping("/{number}/check")
	public ResponseEntity<Void> check(@PathVariable String number) {
		inboundInvoiceService.check(number);

		return ResponseEntity.noContent().build();
	}

	@PostMapping("/{number}/complete")
	public ResponseEntity<Void> complete(@PathVariable String number) {
		inboundInvoiceService.complete(number);

		return ResponseEntity.noContent().build();
	}
}