package com.devrel.wms.repository;

import com.devrel.wms.domain.Replenishment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReplenishmentRepository extends MongoRepository<Replenishment, String> {
}
