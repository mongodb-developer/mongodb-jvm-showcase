package com.mongodb.domain.ports

import com.mongodb.domain.entity.Exercises
interface ExercisesRepository {
    suspend fun findSimilarExercises(embedding: List<Double>): List<Exercises>
}