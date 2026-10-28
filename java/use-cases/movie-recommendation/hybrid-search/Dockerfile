# syntax=docker/dockerfile:1

ARG BUILD_IMAGE=maven:3.9-eclipse-temurin-25
ARG RUNTIME_IMAGE=eclipse-temurin:25-jre

FROM ${BUILD_IMAGE} AS build

WORKDIR /workspace

COPY pom.xml .
COPY src src

RUN mvn -B -DskipTests package

FROM ${RUNTIME_IMAGE}

USER root

RUN apt-get update \
    && apt-get install -y --only-upgrade ca-certificates \
    && rm -rf /var/lib/apt/lists/*

RUN for a in \
      "emsigneccrootcag3 [jdk]" \
      "comodoaaaca [jdk]" \
      "emsignrootcag1 [jdk]" \
      "emsignrootcag2 [jdk]"; do \
      if keytool -list -cacerts -storepass changeit -alias "$a" >/dev/null 2>&1; then \
        keytool -delete -cacerts -storepass changeit -alias "$a"; \
      fi; \
    done

RUN ! keytool -list -cacerts -storepass changeit | grep -Ei 'emsigneccrootcag3|comodoaaaca'

WORKDIR /app

COPY --from=build /workspace/target/*.jar app.jar

EXPOSE 8080

USER 1001

ENTRYPOINT ["sh", "-c", "exec java -jar /app/app.jar --server.port=${PORT:-8080}"]