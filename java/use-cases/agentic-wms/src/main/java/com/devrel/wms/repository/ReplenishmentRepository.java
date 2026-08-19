package com.devrel.wms.repository;

import com.devrel.wms.entity.Replenishment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReplenishmentRepository extends MongoRepository<Replenishment, String> {
}
