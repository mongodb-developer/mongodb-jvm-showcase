package com.mongodb.infrastructure

import com.mongodb.MongoException
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

    override suspend fun insertMany(exercises: List<Exercises>): Int {
        if (exercises.isEmpty()) return 0
        try {
            val result = mongoDatabase.getCollection<Exercises>(EXERCISES_COLLECTION).insertMany(exercises)
            return result.insertedIds.size
        } catch (e: MongoException) {
            System.err.println("Unable to insert exercises due to an error: $e")
        }

        return 0
    }
}

