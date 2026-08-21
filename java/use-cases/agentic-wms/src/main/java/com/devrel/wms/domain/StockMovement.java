package com.devrel.wms.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "stock_movements")
@CompoundIndex(name = "product_date", def = "{'productCode': 1, 'date': -1}")
@CompoundIndex(name = "invoice_date", def = "{'invoiceNumber': 1, 'date': -1}")
@CompoundIndex(name = "depositor_date", def = "{'depositor.id': 1, 'date': -1}")
@CompoundIndex(name = "date_desc", def = "{'date': -1}")
public record StockMovement(
        @Id String id,
        LocalDateTime date,
        String productCode,
        DepositorRef depositor,
        Integer quantity,
        String invoiceNumber,
        MovementType type
) {
	public enum MovementType {
		INBOUND,
		OUTBOUND
	}
}