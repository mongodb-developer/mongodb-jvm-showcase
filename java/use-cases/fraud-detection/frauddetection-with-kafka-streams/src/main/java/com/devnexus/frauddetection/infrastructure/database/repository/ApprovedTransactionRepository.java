package com.devnexus.frauddetection.infrastructure.database.repository;

import com.devnexus.frauddetection.infrastructure.database.document.ApprovedTransaction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApprovedTransactionRepository extends MongoRepository<ApprovedTransaction, String> {
}