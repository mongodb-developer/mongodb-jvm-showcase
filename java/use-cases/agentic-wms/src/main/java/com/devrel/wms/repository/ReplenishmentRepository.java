package com.devrel.wms.repository;

import com.devrel.wms.domain.Replenishment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReplenishmentRepository extends MongoRepository<Replenishment, String> {
	List<Replenishment> findByDepositorIdAndStatus(String depositorId, Replenishment.Status status);

	Optional<Replenishment> findFirstByStatusAndNotificationIsNullOrderByIdDesc(Replenishment.Status status);
}
