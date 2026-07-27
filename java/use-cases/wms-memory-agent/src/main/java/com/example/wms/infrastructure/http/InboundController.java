package com.example.wms.infrastructure.http;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inbound")
public class InboundController {

	@GetMapping
	public String inbound() {
		return "inbound";
	}
}
