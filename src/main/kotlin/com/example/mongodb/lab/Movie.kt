package com.example.mongodb.lab

import io.quarkus.mongodb.panache.common.MongoEntity

@MongoEntity(collection = "movies")
class Movie {
    lateinit var title: String
    var plot: String? = null
    var cast: List<String>? = null
}