package com.example.mongodb.lab

import jakarta.ws.rs.DefaultValue
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.QueryParam

@Path("/movies")
@Produces("application/json")
class MovieResource(
    val repository: MovieRepository
) {

    @GET
    fun list(
        @QueryParam("page") @DefaultValue("0") page: Int,
        @QueryParam("size") @DefaultValue("20") size: Int,
    ) = repository.findAll()
        .page(page, size)
        .list()


    @GET
    @Path("/top-frequent-actors")
    fun findTopFrequentActors() = repository.findTopFrequentActors()
}