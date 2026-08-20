package com.devrel.wms.web;

import com.devrel.wms.domain.OutboundInvoice;
import com.devrel.wms.service.OutboundInvoiceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/outbound-invoice")
public class OutboundInvoiceController {

	private final OutboundInvoiceService outboundInvoiceService;

	OutboundInvoiceController(OutboundInvoiceService outboundInvoiceService) {
		this.outboundInvoiceService = outboundInvoiceService;
	}

	@PostMapping
	public ResponseEntity<OutboundInvoice> create(@RequestBody OutboundInvoice outboundInvoice) {
		OutboundInvoice created = outboundInvoiceService.save(outboundInvoice);

		return ResponseEntity
				.created(URI.create("/outbound-invoice/" + created.number()))
				.body(created);
	}

	@GetMapping
	public ResponseEntity<List<OutboundInvoice>> findAll() {
		return ResponseEntity.ok(outboundInvoiceService.findAll());
	}

	@PostMapping("/{number}/check")
	public ResponseEntity<Void> check(@PathVariable String number) {
		outboundInvoiceService.check(number);

		return ResponseEntity.noContent().build();
	}

	@PostMapping("/{number}/execute")
	public ResponseEntity<Void> execute(@PathVariable String number) {
		outboundInvoiceService.execute(number);

		return ResponseEntity.noContent().build();
	}
}