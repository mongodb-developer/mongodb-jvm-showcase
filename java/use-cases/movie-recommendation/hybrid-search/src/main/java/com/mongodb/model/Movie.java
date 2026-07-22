package com.mongodb.model;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("embedded_movies")
public record Movie(
	String title,
	String year,
	String fullplot,
	String plot,
	String poster,
	Imdb imdb,
	List<String> genres,
	List<String> cast)
{
	record Imdb(Double rating) {}
}

