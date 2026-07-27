# MongoDB JVM Showcase

A collection of example projects showing how to use MongoDB with JVM languages and frameworks.

Each project is independent and self-contained, with its own source code, documentation, and build configuration.

## Contents

This repo is organized by language (**Java** and **Kotlin**), and inside each language projects are grouped by driver or framework:

| Folder | Description |
|--------|-------------|
| [`java`](java/) | Java examples: driver usage, Spring (Data, AI, LangChain4j), Quarkus, and complete use cases. |
| [`kotlin`](kotlin/) | Kotlin examples: driver usage, Ktor, Quarkus, and complete use cases. |
| [`workshops`](workshops/README.md) | Self-paced hands-on workshops (Java, Kotlin, etc.). |

Both languages follow the same structure:

| Folder | Description |
|--------|-------------|
| `java-driver` / `kotlin-driver` | Examples using the official MongoDB drivers directly (CRUD, aggregations, MongoDB Search, encryption, etc.). |
| `spring` | Spring-based projects: `spring-data` (Spring Data MongoDB), `spring-ai` (RAG, vector search), and `langchain4j` integrations. |
| `quarkus` | Quarkus projects. |
| `ktor` | Ktor projects (Kotlin). |
| `use-cases` | Complete, runnable applications solving real-world scenarios (e.g. fraud detection, movie recommendation). |

```
.
├── java/
│   ├── java-driver/
│   ├── spring/
│   │   ├── spring-data/
│   │   ├── spring-ai/
│   │   └── langchain4j/
│   ├── quarkus/
│   └── use-cases/
│       ├── fraud-detection/
│       └── movie-recommendation/
└── kotlin/
    ├── kotlin-driver/
    ├── ktor/
    ├── quarkus/
    └── use-cases/
```

