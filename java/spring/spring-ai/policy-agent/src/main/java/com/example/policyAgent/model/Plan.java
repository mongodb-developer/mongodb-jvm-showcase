package com.example.policyAgent.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "plan")
public class Plan {

	@Id
	private String id;
	private String context;
	@JsonIgnore
	private Status status;
	private List<Task> tasks;

	@PersistenceCreator
	public Plan() {
	}

	public Plan(String context, List<Task> tasks) {
		this.context = context;
		this.tasks = tasks;
	}

	public static class Task {

		private String objective;
		@JsonIgnore
		private Status status;
		@JsonIgnore
		private String result;

		public Task() {
		}

		public Task(String objective) {
			this.objective = objective;
		}

		public String getObjective() {
			return objective;
		}

		public Status getStatus() {
			return status;
		}

		public void setStatus(Status status) {
			this.status = status;
		}

		public String getResult() {
			return result;
		}

		public void setResult(String result) {
			this.result = result;
		}
	}

	public enum Status {
		CREATED, RUNNING, COMPLETED, FAILED, SKIPPED
	}

	public String getId() {
		return id;
	}

	public String getContext() {
		return context;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public List<Task> getTasks() {
		return tasks;
	}
}