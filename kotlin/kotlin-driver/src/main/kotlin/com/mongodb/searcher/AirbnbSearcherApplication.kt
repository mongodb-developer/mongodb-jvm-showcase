package com.mongodb.searcher

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class AirbnbSearcherApplication

fun main(args: Array<String>) {
	runApplication<AirbnbSearcherApplication>(*args)
}
