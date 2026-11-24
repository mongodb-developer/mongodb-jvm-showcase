package com.mongodb.devrel;

import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

	private final OrderService service;

	public OrderController(OrderService service) {
		this.service = service;
	}

	@PostMapping
	public Order create(@RequestBody Order order) {
		return service.create(order);
	}

	@GetMapping
	public List<Order> findByCustomerName(@RequestParam String customerName) {
		return service.findByCustomerName(customerName);
	}

	@GetMapping("/search")
	public List<Order> search(@RequestParam String customerName) {
		return service.search(customerName);
	}

	@GetMapping("/revenue")
	public List<Revenue> revenue() {
		return service.revenueByCustomer();
	}
}
