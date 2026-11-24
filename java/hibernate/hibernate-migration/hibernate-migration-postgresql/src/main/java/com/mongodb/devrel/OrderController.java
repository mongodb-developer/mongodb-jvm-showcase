package com.mongodb.devrel;

import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

	private final OrderRepository repository;

	public OrderController(OrderRepository repository) {
		this.repository = repository;
	}

	@PostMapping
	public Order create(@RequestBody Order order) {
		return repository.save(order);
	}

	@GetMapping
	public List<Order> findByCustomerName(@RequestParam String customerName) {
		return repository.findByCustomerName(customerName);
	}
}
