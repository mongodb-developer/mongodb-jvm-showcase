package com.devrel.wms.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "stock_movements")
public record StockMovement(
        @Id String id,
        LocalDateTime date,
        @Indexed String productCode,
        Integer quantity,
        String invoiceNumber,
        MovementType type
) {
	public enum MovementType {
		INBOUND,
		OUTBOUND
	}
}