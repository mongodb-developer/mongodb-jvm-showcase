package com.mongodb.application.response
data class ExercisesResponse(
    val exerciseNumber: Int,
    val bodyPart: String,
    val type: String,
    val description: String,
    val title: String
)