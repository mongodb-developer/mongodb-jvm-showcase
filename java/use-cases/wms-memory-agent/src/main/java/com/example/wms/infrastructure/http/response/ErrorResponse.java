package com.example.wms.infrastructure.http.response;

import java.time.Instant;

public record ErrorResponse(
		int status,
		String error,
		String message,
		Instant timestamp
) {
	public static ErrorResponse of(int status, String error, String message) {
		return new ErrorResponse(status, error, message, Instant.now());
	}
}
