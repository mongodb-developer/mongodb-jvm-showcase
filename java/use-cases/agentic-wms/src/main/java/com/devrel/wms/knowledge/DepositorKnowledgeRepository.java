package com.devrel.wms.knowledge;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class DepositorKnowledgeRepository {

	private static final String CONTENT_FIELD = "content";
	private static final String METADATA_FIELD = "metadata";
	private static final Set<String> RESERVED_METADATA = Set.of("depositorId", "type", "key");

	private final MongoTemplate mongoTemplate;
	private final String collectionName;

	DepositorKnowledgeRepository(
			MongoTemplate mongoTemplate,
			@Value("${spring.ai.vectorstore.mongodb.collection-name}") String collectionName) {
		this.mongoTemplate = mongoTemplate;
		this.collectionName = collectionName;
	}

	public List<DepositorKnowledgeEntry> findAll() {
		return read(new Query());
	}

	public List<DepositorKnowledgeEntry> findByDepositorId(String depositorId) {
		return read(Query.query(Criteria.where(METADATA_FIELD + ".depositorId").is(depositorId)));
	}

	private List<DepositorKnowledgeEntry> read(Query query) {
		query.fields().include(CONTENT_FIELD).include(METADATA_FIELD);

		return mongoTemplate.find(query, org.bson.Document.class, collectionName)
				.stream()
				.map(this::toEntry)
				.sorted(Comparator.comparing(DepositorKnowledgeEntry::depositorId,
								Comparator.nullsLast(Comparator.naturalOrder()))
						.thenComparing(DepositorKnowledgeEntry::key,
								Comparator.nullsLast(Comparator.naturalOrder())))
				.toList();
	}

	private DepositorKnowledgeEntry toEntry(org.bson.Document document) {
		org.bson.Document metadata = document.get(METADATA_FIELD, org.bson.Document.class);

		if (metadata == null) {
			metadata = new org.bson.Document();
		}

		Map<String, Object> attributes = new LinkedHashMap<>();

		metadata.forEach((name, value) -> {
			if (!RESERVED_METADATA.contains(name)) {
				attributes.put(name, value);
			}
		});

		return new DepositorKnowledgeEntry(
				metadata.getString("depositorId"),
				metadata.getString("key"),
				toType(metadata.getString("type")),
				document.getString(CONTENT_FIELD),
				attributes
		);
	}

	private KnowledgeType toType(String value) {
		return value == null ? KnowledgeType.GENERAL : KnowledgeType.valueOf(value);
	}
}