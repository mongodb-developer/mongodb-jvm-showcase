package com.example.policyAgent.repository;

import com.example.policyAgent.model.Plan;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlanRepository extends MongoRepository<Plan, String> {

	Optional<Plan> findFirstByConversationIdAndStatus(String conversationId, Plan.Status status);
}
