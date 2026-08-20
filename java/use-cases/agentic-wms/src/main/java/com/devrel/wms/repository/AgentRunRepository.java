package com.devrel.wms.repository;

import com.devrel.wms.domain.AgentRun;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgentRunRepository extends MongoRepository<AgentRun, String> {
	List<AgentRun> findAllByOrderByStartedAtDesc();
}
