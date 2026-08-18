package com.devrel.wms.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "inventory")
public record Inventory(
        @Id String id,
        @Indexed(unique = true) String productCode,
        int quantity
) {}