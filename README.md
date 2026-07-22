# MongoDB JVM Showcase

A collection of example projects showing how to use MongoDB with JVM languages and frameworks.

Each project is independent and self-contained, with its own source code, documentation, and build configuration.

## Repository Structure

Projects are organized by language (**Java** and **Kotlin**), and inside each language they follow the same structure, grouped by driver or framework:

- **`java-driver` / `kotlin-driver`** – Examples using the official MongoDB drivers directly (CRUD, aggregations, Atlas Search, encryption, etc.).
- **`spring`** – Spring-based projects, split by technology:
  - **`spring-data`** – Spring Data MongoDB.
  - **`spring-ai`** – Spring AI (RAG, vector search, etc.).
  - **`langchain4j`** – LangChain4j integrations.
- **`quarkus`** – Quarkus projects.
- **`ktor`** – Ktor projects (Kotlin).
- **`use-cases`** – Complete, runnable applications solving real-world scenarios (e.g. fraud detection, movie recommendation).

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
└── kotlin/
    ├── kotlin-driver/
    ├── ktor/
    ├── quarkus/
    └── use-cases/
```

## Contributing

This project is a work in progress and we'd love to improve it. Contributions, new examples, and suggestions to make this README and the projects better are welcome!
