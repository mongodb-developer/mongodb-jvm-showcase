package com.devrel;

import dev.langchain4j.model.chat.ChatModel;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;

@Path("/chat")
public class ChatResource {

    @Inject
    ChatModel model;

    @GET
    public String chat(@QueryParam("message") String message) {
        return model.chat(message);
    }
}
