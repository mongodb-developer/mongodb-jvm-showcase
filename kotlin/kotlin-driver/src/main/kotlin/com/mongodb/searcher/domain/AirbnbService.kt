package com.mongodb.searcher.domain

import com.mongodb.searcher.resources.AirbnbRepository
import org.springframework.stereotype.Service

@Service
class AirbnbService(
    private val airbnbRepository: AirbnbRepository
) {

    fun find(query: String, minNumberReviews: Int): List<Airbnb> {
        require(query.isNotEmpty()) { "Query must not be empty" }
        require(minNumberReviews > 0) { "Minimum number of reviews must not be negative" }

       return airbnbRepository.find(query, minNumberReviews).map { it.toDomain() }
    }
}