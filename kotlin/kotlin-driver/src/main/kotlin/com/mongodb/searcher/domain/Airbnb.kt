package com.mongodb.searcher.domain

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.codecs.pojo.annotations.BsonProperty
import org.bson.types.Decimal128

data class Airbnb(
    @BsonId val id: String,
    val name: String,
    val summary: String,
    val price: Decimal128,
    @BsonProperty("number_of_reviews")
    val numbersOfReviews: Int,
    val street: String
)