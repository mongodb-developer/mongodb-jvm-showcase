package com.mongodb.devrel;

import jakarta.persistence.EntityManager;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

	private final OrderRepository repository;
	private final EntityManager entityManager;

	public OrderService(OrderRepository repository, EntityManager entityManager) {
		this.repository = repository;
		this.entityManager = entityManager;
	}

	public Order create(Order order) {
		return repository.save(order);
	}

	public List<Order> findByCustomerName(String customerName) {
		return repository.findByCustomerName(customerName);
	}

	public List<Order> search(String customerName) {
		return repository.searchByCustomerName(customerName);
	}

	public List<Revenue> revenueByCustomer() {
		return entityManager.createQuery("""
				select o.customerName, sum(i.quantity * i.unitPrice)
				from Order o
				join o.items i
				group by o.customerName
				""", Revenue.class).getResultList();
	}
}
