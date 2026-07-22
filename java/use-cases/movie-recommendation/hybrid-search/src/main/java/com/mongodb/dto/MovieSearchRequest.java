package com.mongodb.dto;

import org.springframework.data.mongodb.core.query.Criteria;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public record MovieSearchRequest(
		String query,
		Integer yearFrom,
		Integer yearTo,
		List<String> genres,
		Double minIMDbRating,
		boolean excludeGenres
) {

	public Criteria toCriteria() {
		final List<Criteria> parts = new ArrayList<>(3);

		List<String> g = cleanedGenres();
		if (!g.isEmpty()) {
			parts.add(excludeGenres
					? Criteria.where("genres").nin(g)
					: Criteria.where("genres").in(g));
		}

		YearBounds yb = normalizedYearBounds();
		if (yb.from != null || yb.to != null) {
			Criteria y = Criteria.where("year");
			if (yb.from != null) y = y.gte(yb.from);
			if (yb.to   != null) y = y.lte(yb.to);
			parts.add(y);
		}

		if (minIMDbRating != null) {
			parts.add(Criteria.where("imdb.rating").gte(minIMDbRating));
		}

		if (parts.isEmpty()) return new Criteria();
		if (parts.size() == 1) return parts.getFirst();
		return new Criteria().andOperator(parts.toArray(Criteria[]::new));
	}

	public List<String> cleanedGenres() {
		return Optional.ofNullable(genres).orElseGet(List::of).stream()
				.filter(Objects::nonNull)
				.map(String::trim)
				.filter(s -> !s.isEmpty())
				.distinct()
				.toList();
	}

	private YearBounds normalizedYearBounds() {
		Integer f = yearFrom, t = yearTo;
		if (f != null && t != null && f > t) {
			int tmp = f; f = t; t = tmp;
		}
		assert f != null;
		assert t != null;
		return new YearBounds(f, t);
	}

	private record YearBounds(
			Integer from,
			Integer to
	){}
}
