package com.example.wms.infrastructure.http;

import com.example.wms.infrastructure.http.response.ErrorResponse;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(DuplicateKeyException.class)
	ResponseEntity<ErrorResponse> handleDuplicateKey(DuplicateKeyException exception) {
		log.warn("Violacao de indice unico", exception);
		return ResponseEntity.status(HttpStatus.CONFLICT)
				.body(ErrorResponse.of(
						HttpStatus.CONFLICT.value(),
						"DUPLICATE_KEY",
						"A record with this unique value already exists.."
				));
	}

	@ExceptionHandler(Exception.class)
	ResponseEntity<ErrorResponse> handleUnexpected(Exception exception) {
		log.error("Unexpected Error", exception);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(ErrorResponse.of(
						HttpStatus.INTERNAL_SERVER_ERROR.value(),
						exception.getClass().getSimpleName(),
						exception.getMessage()
				));
	}
}
