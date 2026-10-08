package com.devrel;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import jakarta.enterprise.context.ApplicationScoped;

@RegisterAiService
@ApplicationScoped
public interface Assistant {

    @SystemMessage("""
            You are a Java mentor.
            Help developers with Java, the JVM, and frameworks like Quarkus.
            Adapt your answers to the developer's experience level, Java version, and stack mentioned in the conversation.
            Keep answers short and practical, with small code examples when useful.
            """)
    String chat(@MemoryId String sessionId, @UserMessage String message);
}
