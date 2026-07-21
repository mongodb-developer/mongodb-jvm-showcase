package com.mongodb.application.routes

import com.mongodb.domain.ports.FitnessRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.bson.types.ObjectId
import org.koin.ktor.ext.inject

fun Route.fitnessRoutes() {

    val repository by inject<FitnessRepository>()

    route("/fitness") {
        get {
            repository.findAll()?.let { list ->
                call.respond(list.map { it.toResponse() })
            } ?: call.respondText("No records found")
        }

        get("/{id?}") {
            val id = call.parameters["id"]
            if (id.isNullOrEmpty()) {
                return@get call.respondText(
                    text = "Missing id",
                    status = HttpStatusCode.BadRequest
                )
            }

            repository.findById(ObjectId(id))?.let {
                call.respond(it.toResponse())
            } ?: call.respondText("No records found for id $id")
        }

        get("/exerciseType/{type?}") {
            val type = call.parameters["type"]
            type?.takeIf { it.isNotEmpty() }?.let {
                call.respond(repository.findByExerciseType(it).ifEmpty { "No records found for exerciseType $it" })
            } ?: call.respond(HttpStatusCode.BadRequest, "Missing exercise type param")
        }

        post {
            val fitness = call.receive<String>()
            println(fitness)
        }
//        post {
//            val fitness = call.receive<FitnessRequest>()
//            val insertedId = repository.insertOne(fitness.toDomain())
//            call.respond(HttpStatusCode.Created, "Created fitness with id $insertedId")
//
//        }

        delete("/{id?}") {
            val id = call.parameters["id"] ?: return@delete call.respondText(
                text = "Missing fitness id",
                status = HttpStatusCode.BadRequest
            )

            val delete: Long = repository.deleteById(ObjectId(id))

            if (delete == 1L) {
                return@delete call.respondText("Fitness Deleted successfully", status = HttpStatusCode.OK)
            }
            return@delete call.respondText("Fitness not found", status = HttpStatusCode.NotFound)

        }

        patch("/{id?}") {
            val id = call.parameters["id"] ?: return@patch call.respondText(
                text = "Missing fitness id",
                status = HttpStatusCode.BadRequest
            )

            val updated = repository.updateOne(ObjectId(id), call.receive())

            call.respondText(
                text = if (updated == 1L) "Fitness updated successfully" else "Fitness not found",
                status = if (updated == 1L) HttpStatusCode.OK else HttpStatusCode.NotFound
            )
        }
    }
}



