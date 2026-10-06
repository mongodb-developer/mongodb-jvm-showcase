package com.devrel;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;

@Path("/chat")
public class ChatResource {

    @Inject
    Assistant assistant;

    @GET
    public String chat(@QueryParam("sessionId") String sessionId, @QueryParam("message") String message) {
        return assistant.chat(sessionId, message);
    }
}
