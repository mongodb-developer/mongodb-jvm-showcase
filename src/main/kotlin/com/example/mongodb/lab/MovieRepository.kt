package com.example.mongodb.lab

import com.mongodb.client.model.Accumulators
import com.mongodb.client.model.Aggregates
import com.mongodb.client.model.Sorts
import io.quarkus.mongodb.panache.kotlin.PanacheMongoRepository
import jakarta.enterprise.context.ApplicationScoped
import org.bson.Document

@ApplicationScoped
class MovieRepository : PanacheMongoRepository<Movie> {

    /*
    * [
  {
    $unwind:
      {
        path: "$cast"
      }
  },
  {
    $group:
      {
        _id: "$cast",
        totalMovies: {
          $sum: 1
        }
      }
  },
  {
    $sort:
      {
        totalMovies: -1
      }
  },
  {
    $limit:
      10
  }
]*/

    fun findTopFrequentActors(): List<Document> =
        mongoCollection().withDocumentClass(Document::class.java)
            .aggregate(
            listOf(
                Aggregates.unwind("\$cast"),
                Aggregates.group(
                    "\$cast", Accumulators.sum("totalMovies", 1)
                ),
                Aggregates.sort(Sorts.descending("totalMovies")),
                Aggregates.limit(3)
            )
        ).toList()
}