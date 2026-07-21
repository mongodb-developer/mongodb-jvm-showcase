package com.mongodb.resources;

import com.mongodb.domain.model.CustomerOriginatorDetail;
import com.mongodb.domain.model.Transaction;
import org.springframework.data.mongodb.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends MongoRepository<Transaction, String> {

    @ReadPreference("nearest")
    List<Transaction> findByTransactionType(String type);

    List<Transaction> findByAmountGreaterThan(double amount);
    void deleteByTransactionType(String type);
    List<Transaction> findByTransactionTypeAndCurrency(String type, String currency);

    @Query(
            value= "{ 'status' : ?0 }",
            fields="{ 'transactionType': 1, 'status':  1, 'createdAt':  1, 'accountDetails' : 1, 'amount' : 1}",
            sort = "{ createdAt:  -1}"
    )
    List<Transaction> findByStatus(String status);

    @Query("{ '_id' : ?0 }")
    @Update("{ '$set' : { 'status' : ?1 } }")
    void updateStatus(String id, String status);

    @Aggregation(pipeline = {
            "{ '$match': { 'transactionType': ?0 } }",
            "{ '$group': { '_id': '$transactionType', 'amount': { '$sum': '$amount' } } }",
            "{ '$project': { 'amount': 1 } }"
    })
    List<Transaction> getTotalAmountByTransactionType(String transactionType);

    @Aggregation(pipeline = {
            "{ '$match': { 'status': 'error' } }",
            "{ '$project': { '_id': 1, 'amount': 1, 'status': 1, 'description': 1, 'createdAt': 1} }",
            "{ '$out': 'transactions_with_error' }"
    })
    void exportErrorTransactions();

    @Aggregation(pipeline = {
            "{ '$lookup': { " +
                    "'from': 'customer', " +
                    "'localField': 'accountDetails.originator.accountNumber', " +
                    "'foreignField': 'accountNumber', " +
                    "'as': 'originatorCustomerDetails' } }",
            "{ '$project': { " +
                    "'amount': 1, " +
                    "'status': 1, " +
                    "'accountDetails': 1, " +
                    "'originatorCustomerDetails': 1 } }"
    })
    List<CustomerOriginatorDetail> findTransactionsWithCustomerDetails();
}



