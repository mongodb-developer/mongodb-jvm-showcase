# MongoDB JVM Showcase

A collection of example projects showing how to use MongoDB with JVM languages and frameworks.

Each project is independent and self-contained, with its own source code, documentation, and build configuration.

## Repository Structure

Projects are organized by language (**Java** and **Kotlin**), and inside each language they follow the same structure, grouped by driver or framework:

- **`java-driver` / `kotlin-driver`** – Examples using the official MongoDB drivers directly (CRUD, aggregations, Atlas Search, encryption, etc.).
- **`spring`** – Spring-based projects, including Spring Data, Spring AI, and LangChain4j integrations.
- **`quarkus`** – Quarkus projects.
- **`ktor`** – Ktor projects (Kotlin).

```
.
├── java/
│   ├── java-driver/
│   ├── spring/
│   └── quarkus/
└── kotlin/
    ├── kotlin-driver/
    ├── ktor/
    └── quarkus/
```

## Contributing

This project is a work in progress and we'd love to improve it. Contributions, new examples, and suggestions to make this README and the projects better are welcome!
