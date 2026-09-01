package com.mongodb.domain;

import com.mongodb.hibernate.annotations.ObjectIdGenerator;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.bson.types.ObjectId;

@Entity
@Table(name = "order_items")
public class OrderItem {
	@Id
	@ObjectIdGenerator
	private ObjectId id;
	private String product;
	private Integer quantity;
	private Double price;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "orderId", nullable = false)
	private Order order;

	public OrderItem(String product, int quantity, double price) {
		this.product = product;
		this.quantity = quantity;
		this.price = price;
	}

	public OrderItem() {
	}

	@Override
	public String toString() {
		return "OrderItem{id=%s, product='%s', quantity='%s', price='%s'}"
				.formatted(id, product, quantity, price);
	}

	void setOrder(Order order) {
		this.order = order;
	}

}
