# Contributing

First off, thank you for taking the time to contribute! 

This repository is a collection of small, self-contained example projects showing how to use MongoDB with JVM languages and frameworks. Contributions of all kinds are welcome — new examples, bug fixes, improvements, and documentation.

## Before contributing

- Browse the existing projects to get a feel for the structure and style.
- For small fixes (typos, docs, minor bugs), feel free to open a pull request directly.
- For anything larger — a new example project or a significant change — please [open an issue](../../issues) first so we can discuss the idea before you invest time in it.

## Repository structure

Projects are organized first by language (`java/`, `kotlin/`), then grouped by driver or framework:

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

When adding a new project, place it under the folder that matches its language and technology:

- Driver-only examples go under `java-driver/` or `kotlin-driver/`.
- Framework examples go under the matching framework folder (`spring/`, `quarkus/`, `ktor/`).
- Complete, real-world applications go under `use-cases/`.

If your example uses a framework that doesn't have a folder yet, create one following the same naming convention.

## Contribution guidelines

- Keep examples **small and focused** — each project should demonstrate one idea clearly.
- Aim for **production-inspired** code: sensible structure, error handling, and configuration, without unnecessary complexity.
- Prefer clarity over cleverness. These projects are meant to be read and learned from.
- Match the style and conventions of the surrounding code and existing projects.

## Project requirements

Every project **must include a `README.md`** with:

- A short description of what the example demonstrates.
- **Setup instructions** (prerequisites, dependencies, environment variables, connection strings).
- **Usage instructions** (how to build and run the project).

Projects should be independent and self-contained, with their own build configuration and dependencies.

## Pull request process

1. Fork the repository and create a branch for your change.
2. Make your changes, keeping commits focused and descriptive.
3. Ensure your project builds and runs, and that its README is complete.
4. Open a pull request describing what you changed and why. Link any related issue.
5. Respond to review feedback — we'll work with you to get it merged.

## Need help?

If you have questions or get stuck, [open an issue](../../issues) and we'll be happy to help. 
