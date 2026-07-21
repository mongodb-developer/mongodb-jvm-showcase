package com.mongodb.application.routes

import com.google.gson.Gson
import com.mongodb.application.request.ExerciseSeed
import com.mongodb.application.request.SentenceRequest
import com.mongodb.application.request.toDomain
import com.mongodb.domain.ports.ExercisesRepository
import com.mongodb.huggingFaceApiToken
import com.mongodb.huggingFaceApiUrl
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.exercisesRoutes() {
    val repository by inject<ExercisesRepository>()

    route("/exercises/processRequest") {
        post {
            val sentence = call.receive<SentenceRequest>()

            val response = requestSentenceTransform(sentence.input, call.huggingFaceApiUrl(), call.huggingFaceApiToken())

            if (response.status.isSuccess()) {
                val embedding = sentence.convertResponse(response.body())
                val similarDocuments = repository.findSimilarExercises(embedding)

                call.respond(HttpStatusCode.Accepted, similarDocuments.map { it.toResponse() })
            }
        }
    }

    route("/exercises/seed") {
        post {
            val json = object {}.javaClass.getResourceAsStream("/exercises.json")?.bufferedReader()?.use { it.readText() }
                ?: return@post call.respond(
                    HttpStatusCode.InternalServerError,
                    "exercises.json not found on the classpath"
                )

            val seeds = Gson().fromJson(json, Array<ExerciseSeed>::class.java).toList()
            val huggingFaceURL = call.huggingFaceApiUrl()
            val huggingFaceToken = call.huggingFaceApiToken()

            val exercises = seeds.map { seed ->
                val response = requestSentenceTransform(seed.description, huggingFaceURL, huggingFaceToken)
                if (!response.status.isSuccess()) {
                    return@post call.respond(
                        HttpStatusCode.BadGateway,
                        "Failed to generate embedding for '${seed.title}': ${response.status}"
                    )
                }
                val embedding = SentenceRequest(seed.description).convertResponse(response.body())
                seed.toDomain(embedding)
            }

            val inserted = repository.insertMany(exercises)
            call.respond(HttpStatusCode.Created, "Seeded $inserted exercises")
        }
    }
}
suspend fun requestSentenceTransform(input: String, huggingFaceURL: String, token: String): HttpResponse {
    return HttpClient(CIO).use { client ->
        client.post(huggingFaceURL) {
            header(HttpHeaders.Authorization, "Bearer $token")
            val body = Gson().toJson(mapOf("inputs" to input))
            setBody(TextContent(body, ContentType.Application.Json))
        }
    }
}