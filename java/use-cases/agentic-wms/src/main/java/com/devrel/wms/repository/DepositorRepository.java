package com.devrel.wms.repository;

import com.devrel.wms.domain.Depositor;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepositorRepository extends MongoRepository<Depositor, String> {
	Optional<Depositor> findByCode(String code);
}
