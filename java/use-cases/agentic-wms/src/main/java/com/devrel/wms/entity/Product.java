package com.devrel.wms.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "products")
public record Product(@Id String id, String name, @Indexed(unique = true) String code) {}