package com.mongodb.domain;

import com.mongodb.hibernate.annotations.ObjectIdGenerator;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import org.bson.types.ObjectId;
import org.hibernate.annotations.Struct;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

	@Id
	@ObjectIdGenerator
	private ObjectId id;
	private String customer;
	private String number;
	private boolean paid;
	private Address address;

	@Version
	private long version;

	@OneToMany(
			mappedBy = "order",
			cascade = CascadeType.ALL
	)
	private List<OrderItem> items = new ArrayList<>();

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public void setPaid(boolean paid) {
		this.paid = paid;
	}

	public void addItem(OrderItem item) {
		items.add(item);
		item.setOrder(this);
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	@Override
	public String toString() {
		return "Order{id=%s, customer='%s', number='%s', items='%s' paid='%s' address='%s'}"
				.formatted(id, customer, number, items, paid, address);
	}

	public boolean getPaid() {
		return paid;
	}

	@Embeddable
	@Struct(name = "Address")
	public record Address(String city, String zipcode) {}

}
