package com.example.wms.domain.model;

public record Product(
		String sku,
		String name,
		String description,
		Boolean active
){};