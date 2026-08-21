package com.devrel.wms.web;

import com.devrel.wms.exception.ConflictException;
import com.devrel.wms.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Pattern DUP_KEY = Pattern.compile("dup key: \\{ (.+?): (.+?) }");

	private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(NotFoundException.class)
	public ProblemDetail handleNotFound(NotFoundException exception) {
		return problemDetail(HttpStatus.NOT_FOUND, "Resource not found", exception.getMessage());
	}

	@ExceptionHandler(ConflictException.class)
	public ProblemDetail handleConflict(ConflictException exception) {
		return problemDetail(HttpStatus.CONFLICT, "Conflicting state", exception.getMessage());
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ProblemDetail handleIllegalArgument(IllegalArgumentException exception) {
		return problemDetail(HttpStatus.BAD_REQUEST, "Invalid request", exception.getMessage());
	}

	@ExceptionHandler(DuplicateKeyException.class)
	public ProblemDetail handleDuplicatedKey(DuplicateKeyException exception) {
		logger.warn("Duplicate key rejected: {}", exception.getMostSpecificCause().getMessage());

		return problemDetail(HttpStatus.CONFLICT, "Duplicated key", duplicatedKeyDetail(exception));
	}

	@ExceptionHandler(Exception.class)
	public ProblemDetail handleUnexpected(Exception exception) {
		logger.error("Unexpected error", exception);

		return problemDetail(
				HttpStatus.INTERNAL_SERVER_ERROR,
				"Unexpected error",
				exception.getMessage()
		);
	}

	private String duplicatedKeyDetail(DuplicateKeyException exception) {
		String message = exception.getMostSpecificCause().getMessage();

		if (message == null) {
			return "A record with the same unique value already exists";
		}

		Matcher matcher = DUP_KEY.matcher(message);

		if (!matcher.find()) {
			return "A record with the same unique value already exists";
		}

		return "A record with " + matcher.group(1) + " " + matcher.group(2).trim() + " already exists";
	}

	private ProblemDetail problemDetail(HttpStatus status, String title, String detail) {
		ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
		problemDetail.setTitle(title);

		return problemDetail;
	}
}