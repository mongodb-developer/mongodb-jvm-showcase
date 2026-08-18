package com.example.policyAgent.service;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class EmailToolService {

	public record Contact(String name, String email, String role) {}

	public record SentEmail(String messageId, String recipient, String status) {}

	@Tool(description = "Get the manager of the current employee")
	public Contact getManager() {
		return new Contact("Helena Duarte", "helena.duarte@company.com", "Engineering Manager");
	}

	@Tool(description = "Get the team members of the current employee")
	public List<Contact> getTeamMembers() {
		return List.of(
				new Contact("Ricardo Mello", "ricardo.mello@company.com", "Software Engineer"),
				new Contact("Carla Nunes", "carla.nunes@company.com", "Software Engineer"),
				new Contact("Diego Ramos", "diego.ramos@company.com", "QA Engineer")
		);
	}

	@Tool(description = "Send an email to a recipient")
	public SentEmail sendEmail(
			@ToolParam(description = "Email address of the recipient") String recipient,
			@ToolParam(description = "Subject of the email") String subject,
			@ToolParam(description = "Body of the email") String body
	) {
		return new SentEmail(UUID.randomUUID().toString(), recipient, "SENT");
	}
}