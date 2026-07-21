package com.mongodb.application.request

data class SentenceRequest(
    val input: String
)
{
    fun convertResponse(body: String) =
        body
            .replace("[", "")
            .replace("]", "")
            .split(",")
            .map { it.trim().toDouble() }
}