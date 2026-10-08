package com.devrel;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import io.quarkiverse.langchain4j.memorystore.MongoDBChatMemoryStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@ApplicationScoped
public class ChatSessions {

    public record Session(String id, String title, Date updatedAt) {
    }

    public record Message(String role, String text) {
    }

    private static final int TITLE_LENGTH = 60;

    @Inject
    MongoDBChatMemoryStore store;

    @Inject
    MongoClient mongoClient;

    private MongoCollection<Document> collection() {
        return mongoClient.getDatabase("langchain4j").getCollection("chat_memory");
    }

    public List<ChatMessage> getMessages(Object id) {
        return store.getMessages(id);
    }

    public void updateMessages(Object id, List<ChatMessage> messages) {
        String sessionId = id.toString();
        Document existing = collection().find(Filters.eq("_id", sessionId))
                .projection(Projections.include("title")).first();
        String title = existing != null && existing.getString("title") != null
                ? existing.getString("title")
                : titleFrom(messages);
        store.updateMessages(id, messages);
        collection().updateOne(Filters.eq("_id", sessionId),
                Updates.combine(Updates.set("title", title), Updates.set("updatedAt", new Date())));
    }

    public List<Session> list() {
        List<Session> sessions = new ArrayList<>();
        collection().find()
                .projection(Projections.include("title", "updatedAt"))
                .sort(Sorts.descending("updatedAt"))
                .forEach(doc -> sessions.add(new Session(
                        doc.get("_id").toString(),
                        Objects.requireNonNullElse(doc.getString("title"), doc.get("_id").toString()),
                        doc.getDate("updatedAt"))));
        return sessions;
    }

    public List<Message> history(String id) {
        List<Message> history = new ArrayList<>();
        for (ChatMessage message : store.getMessages(id)) {
            if (message instanceof UserMessage user && user.hasSingleText()) {
                history.add(new Message("user", user.singleText()));
            } else if (message instanceof AiMessage ai && ai.text() != null) {
                history.add(new Message("ai", ai.text()));
            }
        }
        return history;
    }

    public void delete(String id) {
        store.deleteMessages(id);
    }

    private String titleFrom(List<ChatMessage> messages) {
        return messages.stream()
                .filter(m -> m instanceof UserMessage u && u.hasSingleText())
                .map(m -> ((UserMessage) m).singleText().strip())
                .findFirst()
                .map(t -> t.length() > TITLE_LENGTH ? t.substring(0, TITLE_LENGTH) + "..." : t)
                .orElse("New conversation");
    }
}
