package com.mongodb.application.config;

import com.mongodb.domain.model.Customer;
import com.mongodb.domain.model.Transaction;
import com.mongodb.domain.model.Transaction.AccountDetails;
import com.mongodb.domain.model.Transaction.Beneficiary;
import com.mongodb.domain.model.Transaction.Originator;
import com.mongodb.domain.service.CustomerService;
import com.mongodb.domain.service.TransactionService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Configuration
public class DataInitializerConfig {

    @Bean
    public ApplicationRunner initializeData(
            CustomerService customerService,
            TransactionService transactionService
    ) {
        return args -> {

            // 👉 Customers
            List<Customer> customers = List.of(
                    new Customer("João Silva", "joao.silva@email.com", "1001", "1199990001",
                            new Customer.Address("Rua das Flores", "São Paulo")),
                    new Customer("Maria Oliveira", "maria.oliveira@email.com", "1002", "1199990002",
                            new Customer.Address("Avenida Diagonal", "Barcelona")),
                    new Customer("Carlos Souza", "carlos.souza@email.com", "1003", "3199990003",
                            new Customer.Address("Via Roma", "Roma")),
                    new Customer("Ana Costa", "ana.costa@email.com", "1004", "2199990004",
                            new Customer.Address("Broadway", "New York")),
                    new Customer("Pedro Lima", "pedro.lima@email.com", "1005", "7199990005",
                            new Customer.Address("Gran Vía", "Madrid")),
                    new Customer("Juliana Rocha", "juliana.rocha@email.com", "1006", "6199990006",
                            new Customer.Address("Champs-Élysées", "Paris")),
                    new Customer("Fernando Dias", "fernando.dias@email.com", "1007", "3199990007",
                            new Customer.Address("Kreuzbergstrasse", "Berlin")),
                    new Customer("Patrícia Almeida", "patricia.almeida@email.com", "1008", "8199990008",
                            new Customer.Address("Queen Street", "Toronto")),
                    new Customer("Rafael Martins", "rafael.martins@email.com", "1009", "5199990009",
                            new Customer.Address("George Street", "Sydney")),
                    new Customer("Camila Ramos", "camila.ramos@email.com", "1010", "6299990010",
                            new Customer.Address("Damrak", "Amsterdam"))
            );
            int insertedCustomers = customerService.bulkCustomerSample(customers);
            System.out.println("Inserted customers: " + insertedCustomers);

            Originator originator = new Originator("1001", "Ricardo", "Bank A");
            Beneficiary beneficiary = new Beneficiary("1002", "Maria", "Bank B");

            List<Transaction> transactions = List.of(
                    new Transaction(null, "Credit", 1200.00, "USD", "finished", Instant.now().toString(), new AccountDetails(originator, beneficiary), "Salary deposit", LocalDateTime.now()),
                    new Transaction(null, "Debit", 150.00, "USD", "pending", Instant.now().toString(), new AccountDetails(originator, beneficiary), "Groceries",  LocalDateTime.now()),
                    new Transaction(null, "Credit", 300.00, "EUR", "finished",  Instant.now().toString(), new AccountDetails(originator, beneficiary), "Freelance job",  LocalDateTime.now()),
                    new Transaction(null, "Debit", 90.00, "BRL", "error", Instant.now().toString(), new AccountDetails(originator, beneficiary), "Failed payment",  LocalDateTime.now()),
                    new Transaction(null, "Debit", 450.00, "USD", "pending", Instant.now().toString(), new AccountDetails(originator, beneficiary), "Online shopping",  LocalDateTime.now()),
                    new Transaction(null, "Credit", 2000.00, "BRL", "finished", Instant.now().toString(), new AccountDetails(originator, beneficiary), "Bonus",  LocalDateTime.now()),
                    new Transaction(null, "Debit", 50.00, "EUR", "error", Instant.now().toString(), new AccountDetails(originator, beneficiary), "Card error",  LocalDateTime.now()),
                    new Transaction(null, "Credit", 600.00, "USD", "pending", Instant.now().toString(), new AccountDetails(originator, beneficiary), "Client payment", LocalDateTime.now()),
                    new Transaction(null, "Debit", 300.00, "EUR", "finished", Instant.now().toString(), new AccountDetails(originator, beneficiary), "Utility bill", LocalDateTime.now()),
                    new Transaction(null, "Debit", 110.00, "BRL", "error",Instant.now().toString(), new AccountDetails(originator, beneficiary), "Incorrect data", LocalDateTime.now())
            );
            transactions.forEach(transactionService::save);
            System.out.println("Inserted transactions: " + transactions.size());
        };
    }
}
