package com.mongodb.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Encrypted;
import org.springframework.data.mongodb.core.mapping.Queryable;
import org.springframework.data.mongodb.core.mapping.RangeEncrypted;

@Document(collection = "employees")
public record Employee(
        @Id
        String id,

        String name,

        @Encrypted
        String pin,

        @Queryable(queryType = "equality")
        @Encrypted
        int ssn,

        @RangeEncrypted(
                contentionFactor = 0L,
                rangeOptions = "{\"min\": 0, \"max\": 150}"
        )
        Integer age,

        @RangeEncrypted(contentionFactor = 0L,
                rangeOptions = "{\"min\": {\"$numberDouble\": \"1500\"}, \"max\": {\"$numberDouble\": \"100000\"}, \"precision\": 2 }")
        double salary
) {}
