package com.mongodb.devrel;

import jakarta.persistence.EntityManager;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

	private final EntityManager entityManager;

	public OrderService(EntityManager entityManager) {
		this.entityManager = entityManager;
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
