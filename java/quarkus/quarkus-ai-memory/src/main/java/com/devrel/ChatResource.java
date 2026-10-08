package com.devrel;

import jakarta.inject.Inject;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;

import java.util.List;

@Path("/chat")
public class ChatResource {

    @Inject
    Assistant assistant;

    @Inject
    ChatSessions sessions;

    @GET
    public String chat(@QueryParam("sessionId") String sessionId, @QueryParam("message") String message) {
        return assistant.chat(sessionId, message);
    }

    @GET
    @Path("/sessions")
    public List<ChatSessions.Session> list() {
        return sessions.list();
    }

    @GET
    @Path("/sessions/{id}")
    public List<ChatSessions.Message> history(@PathParam("id") String id) {
        return sessions.history(id);
    }

    @DELETE
    @Path("/sessions/{id}")
    public void delete(@PathParam("id") String id) {
        sessions.delete(id);
    }
}
