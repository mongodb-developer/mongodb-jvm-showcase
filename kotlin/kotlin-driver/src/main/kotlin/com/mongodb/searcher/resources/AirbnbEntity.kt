package com.mongodb.searcher.resources

import com.mongodb.searcher.domain.Airbnb
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.codecs.pojo.annotations.BsonProperty
import org.bson.types.Decimal128

data class AirbnbEntity(
    @BsonId val id: String,
    val name: String,
    val summary: String,
    val price: Decimal128,
    @BsonProperty("number_of_reviews")
    val numbersOfReviews: Int,
    val address: Address
) {
    data class Address(
        val street: String,
        val country: String,
        @BsonProperty("country_code")
        val countryCode: String
    )

    fun toDomain(): Airbnb {
        return Airbnb(
            id = id,
            name = name,
            summary = summary,
            price = price,
            numbersOfReviews = numbersOfReviews,
            street = address.street
        )
    }
}

