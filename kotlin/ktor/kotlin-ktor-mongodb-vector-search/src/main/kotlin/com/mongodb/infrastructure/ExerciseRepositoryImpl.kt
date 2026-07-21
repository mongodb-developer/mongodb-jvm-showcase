package com.mongodb.infrastructure

import com.mongodb.domain.entity.Exercises
import com.mongodb.domain.ports.ExercisesRepository
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.flow.toList
import org.bson.Document

class ExercisesRepositoryImpl(
    private val mongoDatabase: MongoDatabase
) : ExercisesRepository {

    companion object {
        const val EXERCISES_COLLECTION = "exercises"
    }

   override suspend fun findSimilarExercises(embedding: List<Double>): List<Exercises> {
        val result =
            mongoDatabase.getCollection<Exercises>(EXERCISES_COLLECTION).withDocumentClass<Exercises>().aggregate(
                listOf(
                    Document(
                        "\$vectorSearch",
                        Document("queryVector", embedding)
                            .append("path", "descEmbedding")
                            .append("numCandidates", 3L)
                            .append("index", "vector_index")
                            .append("limit", 3L)
                    )
                )
            )

        return result.toList()
    }
}

