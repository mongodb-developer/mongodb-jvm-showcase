package com.devrel;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class PersistentChatMemoryProvider implements ChatMemoryProvider {

    @Inject
    ChatSessions sessions;

    @Override
    public MessageWindowChatMemory get(Object memoryId) {
        return MessageWindowChatMemory.builder()
                .id(memoryId)
                .maxMessages(20)
                .chatMemoryStore(new ChatMemoryStore() {
                    @Override
                    public List<ChatMessage> getMessages(Object id) {
                        return sessions.getMessages(id);
                    }

                    @Override
                    public void updateMessages(Object id, List<ChatMessage> messages) {
                        sessions.updateMessages(id, messages);
                    }

                    @Override
                    public void deleteMessages(Object id) {
                    }
                })
                .build();
    }
}
