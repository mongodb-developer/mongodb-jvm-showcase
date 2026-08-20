package com.devrel.wms.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "replenishments")
public record Replenishment(
        @Id String id,
        Depositor depositor,
        List<ReplenishmentItem> items,
        String message,
        Status status
) {
    public enum Status {
        PENDING,
        APPROVED,
        REJECTED,
        COMPLETED
    }

    public record ReplenishmentItem(
            String productCode,
            Integer quantity
    ) {}
}