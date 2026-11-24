package com.mongodb.devrel;

import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

	private final OrderRepository repository;
	private final OrderService service;

	public OrderController(OrderRepository repository, OrderService service) {
		this.repository = repository;
		this.service = service;
	}

	@PostMapping
	public Order create(@RequestBody Order order) {
		return repository.save(order);
	}

	@GetMapping
	public List<Order> findByCustomerName(@RequestParam String customerName) {
		return repository.findByCustomerName(customerName);
	}

	@GetMapping("/search")
	public List<Order> search(@RequestParam String customerName) {
		return repository.searchByCustomerName(customerName);
	}

	@GetMapping("/revenue")
	public List<Revenue> revenue() {
		return service.revenueByCustomer();
	}
}
