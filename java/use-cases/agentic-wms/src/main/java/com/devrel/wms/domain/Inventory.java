package com.devrel.wms.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "inventory")
@CompoundIndex(name = "product_depositor_unique", def = "{'productCode': 1, 'depositor.id': 1}", unique = true)
public record Inventory(
        @Id String id,
        String productCode,
        Depositor depositor,
        int quantity
) {}
