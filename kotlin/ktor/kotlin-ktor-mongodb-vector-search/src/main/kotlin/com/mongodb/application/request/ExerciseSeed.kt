package com.mongodb.application.request

import com.mongodb.domain.entity.Exercises
import org.bson.types.ObjectId

/**
 * Raw exercise record as stored in `exercises.json` (without the vector embedding).
 * Used only to seed the database.
 */
data class ExerciseSeed(
    val exerciseNumber: Int,
    val title: String,
    val description: String,
    val type: String,
    val bodyPart: String,
    val equipment: String,
    val level: String,
    val rating: Double,
    val ratingDesc: String
)

fun ExerciseSeed.toDomain(descEmbedding: List<Double>): Exercises = Exercises(
    id = ObjectId(),
    exerciseNumber = exerciseNumber,
    title = title,
    description = description,
    type = type,
    bodyPart = bodyPart,
    equipment = equipment,
    level = level,
    rating = rating,
    ratingDesc = ratingDesc,
    descEmbedding = descEmbedding
)
