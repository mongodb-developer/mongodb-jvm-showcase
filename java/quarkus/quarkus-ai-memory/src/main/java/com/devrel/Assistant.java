package com.devrel;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.UserMessage;
import jakarta.enterprise.context.ApplicationScoped;
import io.quarkiverse.langchain4j.RegisterAiService;

@RegisterAiService
@ApplicationScoped
public interface Assistant {

    String chat(@MemoryId String sessionId, @UserMessage String message);
}
