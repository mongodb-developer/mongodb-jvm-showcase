package com.devrel.wms.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "outbound_invoices")
public record OutboundInvoice(
        @Id String id,
		@Indexed(unique = true)
        String number,
        Depositor depositor,
        List<InvoiceItem> items,
        InvoiceStatus status
) {
	public record InvoiceItem(
			String productCode,
			Integer quantity
	) {}

	public enum InvoiceStatus {
		PENDING,
		RECEIVED,
		COMPLETED,
		FAILED
	}
}