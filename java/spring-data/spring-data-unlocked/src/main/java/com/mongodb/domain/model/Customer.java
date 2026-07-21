package com.mongodb.domain.model;

public record Customer(
        String name,
        String email,
        String accountNumber,
        String phone,
        Address address
) {
    public record Address(
            String street,
            String city
    ) {}
}




