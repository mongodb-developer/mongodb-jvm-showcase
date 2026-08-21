package com.devrel.wms.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "agent_runs")
public record AgentRun(
        @Id String id,
        String trigger,
        String reference,
        Status status,
        String summary,
        LocalDateTime startedAt,
        LocalDateTime completedAt,
        List<AgentTask> tasks
) {

    public enum Status {
        RUNNING,
        COMPLETED,
        FAILED
    }

    public record AgentTask(
            String description,
            TaskStatus status,
            String tool,
            String result,
            LocalDateTime startedAt,
            LocalDateTime completedAt
    ) {}

    public enum TaskStatus {
        PENDING,
        RUNNING,
        COMPLETED,
        FAILED,
        SKIPPED
    }
}