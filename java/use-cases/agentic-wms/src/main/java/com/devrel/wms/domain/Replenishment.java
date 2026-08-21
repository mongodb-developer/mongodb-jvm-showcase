package com.devrel.wms.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "replenishments")
public record Replenishment(
        @Id String id,
        DepositorRef depositor,
        List<ReplenishmentItem> items,
        String message,
        Status status,
        Notification notification
) {
    public record Notification(
            String to,
            List<String> cc,
            String subject,
            String body,
            LocalDateTime sentAt
    ) {}

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