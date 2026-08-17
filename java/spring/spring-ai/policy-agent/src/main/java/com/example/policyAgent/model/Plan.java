package com.example.policyAgent.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "plan")
public class Plan {

	@Id
	private String id;
	private String context;
	private Status status;
	private List<Task> tasks;

	public Plan(String context, Status status, List<Task> tasks) {
		this.context = context;
		this.status = status;
		this.tasks = tasks;
	}

	public record Task(String objective, Status status) {}

	public enum Status {
		CREATED, RUNNING, COMPLETED
	}

	public String getId() {
		return id;
	}

	public List<Task> getTasks() {
		return tasks;
	}
}

