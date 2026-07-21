package com.mongodb.searcher.resources

import com.mongodb.client.model.Aggregates
import com.mongodb.client.model.Projections
import com.mongodb.client.model.search.FuzzySearchOptions
import com.mongodb.client.model.search.SearchOperator
import com.mongodb.client.model.search.SearchOptions
import com.mongodb.client.model.search.SearchPath
import com.mongodb.kotlin.client.MongoDatabase
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository

@Repository
class AirbnbRepository(
    private val mongoDatabase: MongoDatabase
) {

    companion object {
        private val logger = LoggerFactory.getLogger(AirbnbRepository::class.java)
        private const val COLLECTION = "listingsAndReviews"
    }

    fun find(query: String, minNumberReviews: Int): List<AirbnbEntity> {
        val collection = mongoDatabase.getCollection<AirbnbEntity>(COLLECTION)

        return try {
            collection.aggregate(
                listOf(
                    createSearchStage(query, minNumberReviews),
                    createLimitStage(),
                    createProjectionStage()
                )
            ).toList()
        } catch (e: Exception) {
            logger.error("An exception occurred when trying to aggregate the collection: ${e.message}")
            emptyList()
        }
    }

    private fun createSearchStage(query: String, minNumberReviews: Int) =
        Aggregates.search(
            SearchOperator.compound().filter(
                listOf(
                    SearchOperator.numberRange(SearchPath.fieldPath("number_of_reviews"))
                        .gte(minNumberReviews),
                    SearchOperator.text(SearchPath.fieldPath(AirbnbEntity::summary.name), query)
                        .fuzzy(FuzzySearchOptions.fuzzySearchOptions().maxEdits(2))
                )
            ),

            SearchOptions.searchOptions().index("searchPlaces")
        )

    private fun createLimitStage() =
        Aggregates.limit(5)

    private fun createProjectionStage() =
        Aggregates.project(
            Projections.fields(
                Projections.include(
                    listOf(
                        AirbnbEntity::name.name,
                        AirbnbEntity::id.name,
                        AirbnbEntity::summary.name,
                        AirbnbEntity::price.name,
                        "number_of_reviews",
                        AirbnbEntity::address.name
                    )
                )
            )
        )

}

