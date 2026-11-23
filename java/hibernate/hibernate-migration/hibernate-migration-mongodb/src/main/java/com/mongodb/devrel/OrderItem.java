package com.mongodb.devrel;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "order_items")
public class OrderItem {

	@Id
	@UuidGenerator
	private UUID id;

	private String product;
	private int quantity;
	private BigDecimal unitPrice;

	public UUID getId() { return id; }
	public String getProduct() { return product; }
	public void setProduct(String product) { this.product = product; }
	public int getQuantity() { return quantity; }
	public void setQuantity(int quantity) { this.quantity = quantity; }
	public BigDecimal getUnitPrice() { return unitPrice; }
	public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
}
