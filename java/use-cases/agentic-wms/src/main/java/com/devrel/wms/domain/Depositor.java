package com.devrel.wms.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "depositors")
public record Depositor(
		@Id String id,
		String name,
		String email
) {}
