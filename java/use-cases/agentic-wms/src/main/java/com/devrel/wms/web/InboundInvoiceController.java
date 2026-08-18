package com.devrel.wms.web;

import com.devrel.wms.entity.InboundInvoice;
import com.devrel.wms.entity.Inventory;
import com.devrel.wms.service.InboundInvoiceService;
import com.devrel.wms.service.InventoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/inbound-invoice")
public class InboundInvoiceController {

	private final InboundInvoiceService inboundInvoiceService;

	InboundInvoiceController(InboundInvoiceService inboundInvoiceService) {
		this.inboundInvoiceService = inboundInvoiceService;
	}

	@PostMapping
	public ResponseEntity<InboundInvoice> create(@RequestBody InboundInvoice inboundInvoice) {
		return ResponseEntity.ok(inboundInvoiceService.save(inboundInvoice));
	}
}
