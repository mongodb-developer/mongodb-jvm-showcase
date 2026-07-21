package com.mongodb.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "transactions")
@CompoundIndex(name = "type_currency", def = "{'transactionType': 1, 'currency': 1}")
@Data
@AllArgsConstructor
public class Transaction {
    @Id
    private String id;
    private String transactionType;
    private Double amount;
    private String currency;
    private String status;
    private String description;
    private AccountDetails accountDetails;

    @Indexed(unique = true)
    private String authCode;

    @Indexed(
            name = "PartialIndex",
            expireAfterSeconds = 50,
            partialFilter = "{ 'status': { $in: ['SUCCESS', 'COMPLETED'] } }"
    )
    private LocalDateTime createdAt;



    public record AccountDetails(Originator originator, Beneficiary beneficiary) {}
    public record Originator(String accountNumber, String name, String bank) {}
    public record Beneficiary(String accountNumber, String name, String bank) {}
}

