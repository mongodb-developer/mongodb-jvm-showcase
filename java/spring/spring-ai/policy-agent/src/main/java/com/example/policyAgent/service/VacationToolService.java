package com.example.policyAgent.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class VacationToolService {

	private static final int AVAILABLE_DAYS = 20;

	private final Logger loggerFactory = LoggerFactory.getLogger(VacationToolService.class);
	public record VacationBalance(int availableDays, LocalDate lastVacationEndDate) {}

	public record VacationAvailability(boolean available, int availableDays, String reason) {}

	public record VacationRequest(String requestId, LocalDate startDate, LocalDate endDate, String status) {}

	@Tool(description = "Get the vacation day balance of the current employee")
	public VacationBalance getVacationBalance() {
		return new VacationBalance(AVAILABLE_DAYS, LocalDate.of(2025, 11, 14));
	}

	@Tool(description = "Check whether the employee can take vacation on a given start date for a number of days")
	public VacationAvailability checkVacationAvailability(
			@ToolParam(description = "First day of the vacation, in ISO format") LocalDate startDate,
			@ToolParam(description = "Number of vacation days requested") int days
	) {

		loggerFactory.info("Checking Tool vacation availability for {} days starting on {}", days, startDate);

		if (days > AVAILABLE_DAYS) {
			return new VacationAvailability(false, AVAILABLE_DAYS,
					"The employee has only " + AVAILABLE_DAYS + " vacation days available");
		}
		if (startDate.getDayOfWeek() == DayOfWeek.SATURDAY || startDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
			return new VacationAvailability(false, AVAILABLE_DAYS,
					"Vacation cannot start on a weekend");
		}
		return new VacationAvailability(true, AVAILABLE_DAYS, "The requested period is available");
	}

	@Tool(description = "Submit a vacation request for approval")
	public VacationRequest submitVacationRequest(
			@ToolParam(description = "First day of the vacation, in ISO format") LocalDate startDate,
			@ToolParam(description = "Last day of the vacation, in ISO format") LocalDate endDate
	) {
		loggerFactory.info("Submitting Tool vacation Request with start date {} and end date {}", startDate, endDate);

		return new VacationRequest(
				UUID.randomUUID().toString(),
				startDate,
				endDate,
				"PENDING_APPROVAL"
		);
	}
}
