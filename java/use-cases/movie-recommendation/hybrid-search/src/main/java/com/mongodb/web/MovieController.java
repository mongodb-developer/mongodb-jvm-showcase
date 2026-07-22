package com.mongodb.web;

import com.mongodb.dto.MovieSearchRequest;
import com.mongodb.model.Movie;
import com.mongodb.service.MovieService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/movies")
public class MovieController {

	private final MovieService movieService;

	public MovieController(MovieService movieService) {
		this.movieService = movieService;
	}

	@PostMapping("/search")
	public ResponseEntity<List<Movie>> searchMovies(@RequestBody MovieSearchRequest req) {
		return ResponseEntity.ok(movieService.searchMovies(req));
	}
}
