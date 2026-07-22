package com.devnexus.frauddetection.infrastructure.database.repository;

import com.devnexus.frauddetection.infrastructure.database.document.SuspiciousTransaction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SuspiciousTransactionRepository extends MongoRepository<SuspiciousTransaction, String> {
}
