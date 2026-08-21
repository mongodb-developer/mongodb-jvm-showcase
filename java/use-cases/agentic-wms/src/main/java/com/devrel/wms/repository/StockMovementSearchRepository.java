package com.devrel.wms.repository;

import com.devrel.wms.domain.StockMovement;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class StockMovementSearchRepository {

	private final MongoTemplate mongoTemplate;

	StockMovementSearchRepository(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	public List<StockMovement> search(String productCode, String invoiceNumber, String depositorId) {
		List<Criteria> filters = new ArrayList<>();

		if (hasText(productCode)) {
			filters.add(Criteria.where("productCode").is(productCode.trim()));
		}

		if (hasText(invoiceNumber)) {
			filters.add(Criteria.where("invoiceNumber").is(invoiceNumber.trim()));
		}

		if (hasText(depositorId)) {
			filters.add(Criteria.where("depositor.id").is(depositorId.trim()));
		}

		Query query = new Query().with(Sort.by(Sort.Direction.DESC, "date"));

		if (!filters.isEmpty()) {
			query.addCriteria(new Criteria().andOperator(filters));
		}

		return mongoTemplate.find(query, StockMovement.class);
	}

	private boolean hasText(String value) {
		return value != null && !value.isBlank();
	}
}
