package com.example.policyAgent.repository;

import com.example.policyAgent.model.SummaryConversation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SummaryConversationRepository extends MongoRepository<SummaryConversation, String> {

	List<SummaryConversation> findByConversationIdOrderBySequenceAsc(String conversationId);

	Optional<SummaryConversation> findFirstByConversationIdOrderBySequenceDesc(String conversationId);

	void deleteByConversationId(String conversationId);
}
