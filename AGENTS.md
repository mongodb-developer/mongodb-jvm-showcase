# AGENTS.md

Guidance for coding assistants working in the MongoDB JVM Showcase repository.

## Repository Structure

```text
workshops/

java/
├── java-driver/
├── hibernate/
├── spring/
│   ├── spring-data/
│   └── spring-ai/
├── quarkus/
└── use-cases/
    └── <business-domain>/
        └── <project-name>/

kotlin/

scala/
```

Use the existing structure when adding or moving projects. Do not create new top-level directories unless explicitly requested.

## Project Placement

- `java/java-driver/`: MongoDB Java Driver examples.
- `java/hibernate/`: Hibernate ORM examples.
- `java/spring/spring-data/`: Spring Data MongoDB examples.
- `java/spring/spring-ai/`: Spring AI examples.
- `java/quarkus/`: Quarkus examples.
- `java/use-cases/`: complete applications organized by business problem.
- `workshops/`: workshop and hands-on content.

Framework examples should focus on one primary technology. Complete business applications belong under `use-cases`.

## Working in the Monorepo

- Limit changes to the relevant project whenever possible.
- Do not modify unrelated projects.
- Keep every project independent and self-contained.
- Follow the build tool and conventions already used by the project.
- Prefer the smallest change that correctly implements the task.

## Project Requirements

Every project must include a `README.md` with:

- Purpose
- Prerequisites
- Configuration
- How to run
- MongoDB Atlas requirements, when applicable

## MongoDB Guidelines

Use MongoDB official documentation as the primary technical reference:

https://www.mongodb.com/docs/

Use official MongoDB drivers and integrations when available.

Never commit credentials, API keys, or connection strings containing secrets. Use environment variables and document them in the project README.

## MongoDB Skills

Use the official MongoDB agent skills when a matching skill exists:

https://github.com/mongodb/agent-skills

## Build and Test

Run build and tests from the project directory whenever possible.

Typical commands:

```bash
./mvnw clean verify
```

```bash
./gradlew build
```

Before considering a change complete, ensure the modified project builds and relevant tests pass.

## Documentation

Update the project README when changes introduce new configuration, environment variables, Atlas requirements, external services, or setup steps.
