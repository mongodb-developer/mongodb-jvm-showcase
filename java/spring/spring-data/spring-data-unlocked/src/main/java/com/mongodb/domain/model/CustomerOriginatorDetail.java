package com.mongodb.domain.model;

import java.util.List;

public record CustomerOriginatorDetail(
        double amount,
        String status,
        Transaction.AccountDetails accountDetails,
        List<OriginatorCustomerDetails> originatorCustomerDetails
) {
    private record OriginatorCustomerDetails(
            String name,
            String accountNumber,
            String phone,
            Address address) {

        private record Address(String city, String street) {}
    }
}
