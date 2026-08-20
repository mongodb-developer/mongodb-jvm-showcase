package com.devrel.wms.repository;

import com.devrel.wms.domain.AgentRun;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgentRunRepository extends MongoRepository<AgentRun, String> {
}
