package com.mongodb.service;

import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.search.SearchOperator;
import com.mongodb.client.model.search.SearchOptions;
import com.mongodb.client.model.search.SearchPath;
import com.mongodb.dto.MovieSearchRequest;
import com.mongodb.model.Movie;
import com.mongodb.voyage.VoyageConfigProperties;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.VectorSearchOperation;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.mongodb.client.model.search.FuzzySearchOptions.*;
import static com.mongodb.client.model.search.SearchOperator.*;
import static com.mongodb.client.model.search.SearchScore.*;

@Service
@EnableConfigurationProperties(VoyageConfigProperties.class)
public class MovieService {

	private final MongoTemplate mongoTemplate;
	private final VoyageConfigProperties config;
	private final EmbeddingService embeddingService;

 	MovieService(MongoTemplate mongoTemplate, VoyageConfigProperties config, EmbeddingService embeddingService) {
		this.mongoTemplate = mongoTemplate;
		this.config = config;
		this.embeddingService = embeddingService;
	}

	public List<Movie> searchMovies(MovieSearchRequest req) {
		AggregationOperation rankFusion = context -> new Document("$rankFusion",
				new Document("input",
						new Document("pipelines",
								new Document("searchPipeline", List.of(buildFullTextSearchPipeline(req), new Document("$limit", config.topK())))
										.append("vectorPipeline", List.of(buildVectorSearchPipeline(req)))))
						.append("combination",
								new Document("weights",
										new Document("searchPipeline", 0.)
												.append("vectorPipeline", 0.8)))
						.append("scoreDetails", false));

		Aggregation aggregation = Aggregation.newAggregation(rankFusion);

		return mongoTemplate.aggregate(aggregation, config.vectorCollectionName(), Movie.class).getMappedResults();
	}

	private BsonDocument buildFullTextSearchPipeline(MovieSearchRequest req) {
		var filters = buildFilters(req);
		var searchClauses = buildSearchClauses(req);
		var mustNot = buildMustNot(req);
		var compound = compound();

		if (!filters.isEmpty()) {
			compound = compound.filter(filters);
		}
		if (!mustNot.isEmpty()) {
			compound = compound.mustNot(mustNot);
		}

		return Aggregates.search(
				compound.should(searchClauses),
				SearchOptions.searchOptions().index("fulltextsearch")
		).toBsonDocument();
	}

	private List<SearchOperator> buildFilters(MovieSearchRequest req) {
		var filters = new ArrayList<SearchOperator>();

		if (req.genres() != null && !req.genres().isEmpty() && !req.excludeGenres()) {
			filters.add(in(SearchPath.fieldPath("genres"), req.genres()));
		}

		if (req.yearFrom() != null) {
			filters.add(numberRange(SearchPath.fieldPath("year")).gte(req.yearFrom()));
		}

		if (req.yearTo() != null) {
			filters.add(numberRange(SearchPath.fieldPath("year")).lte(req.yearTo()));
		}

		if (req.minIMDbRating() != null) {
			filters.add(numberRange(SearchPath.fieldPath("imdb.rating")).gte(req.minIMDbRating()));
		}

		return filters;
	}

	private List<SearchOperator> buildSearchClauses(MovieSearchRequest req) {
		Map<String, Float> fieldConfigs = Map.of(
				"title", 4.0F,
				"plot", 3.0F,
				"fullplot", 2.0F
		);

		return fieldConfigs.entrySet().stream()
				.map(entry -> text(SearchPath.fieldPath(entry.getKey()), req.query())
						.fuzzy(fuzzySearchOptions().maxEdits(1))
						.score(boost(entry.getValue())))
				.collect(Collectors.toList());
	}

	private List<SearchOperator> buildMustNot(MovieSearchRequest req) {
		var mustNot = new ArrayList<SearchOperator>();

		if (req.genres() != null && !req.genres().isEmpty() && req.excludeGenres()) {
			mustNot.add(in(SearchPath.fieldPath("genres"), req.genres()));
		}
		return mustNot;
	}

	private Bson buildVectorSearchPipeline(MovieSearchRequest req) {
		return VectorSearchOperation.search(config.vectorIndexName())
				.path(config.vectorField())
				.vector(embeddingService.embedQuery(req.query()))
				.limit(config.topK())
				.filter(req.toCriteria())
				.numCandidates(config.numCandidates()).toDocument(Aggregation.DEFAULT_CONTEXT);
	}
}
