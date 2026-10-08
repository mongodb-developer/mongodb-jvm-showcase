# quarkus-ai-memory

## Purpose

A Java mentor chat built with Quarkus and LangChain4j. Conversation memory is persisted in MongoDB via `quarkus-langchain4j-memory-store-mongodb`, so the mentor remembers your Java version, stack, and experience level per `sessionId`, even after the application restarts.

## Prerequisites

- Java 21
- MongoDB running locally on `localhost:27017` (or a MongoDB Atlas cluster)
- OpenAI API key

## Configuration

| Variable | Description | Default |
|---|---|---|
| `OPENAI_API_KEY` | OpenAI API key | - |
| `MONGODB_URI` | MongoDB connection string | `mongodb://localhost:27017` |

## How to run

```bash
export OPENAI_API_KEY=...
./mvnw quarkus:dev
```

Open http://localhost:8080 for the chat UI. The sidebar lists recent conversations, loads their history from MongoDB, and lets you start a new conversation or delete one.

REST endpoints:

| Method | Path | Description |
|---|---|---|
| `GET` | `/chat?sessionId=&message=` | Send a message |
| `GET` | `/chat/sessions` | List conversations, most recent first |
| `GET` | `/chat/sessions/{id}` | Conversation history |
| `DELETE` | `/chat/sessions/{id}` | Delete a conversation |

Or use curl:

```bash
curl "http://localhost:8080/chat?sessionId=01&message=I%20use%20Java%2021%20and%20Quarkus"
curl "http://localhost:8080/chat?sessionId=01&message=How%20should%20I%20call%20an%20external%20REST%20API?"
```

Messages are stored per `sessionId` in the `langchain4j.chat_memory` collection, keeping the last 20 messages. See `requests.http` for a full flow.

