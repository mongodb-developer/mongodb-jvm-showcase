# syntax=docker/dockerfile:1

ARG BUILD_IMAGE=maven:3.9-eclipse-temurin-25
ARG RUNTIME_IMAGE=amazoncorretto:25-al2023-headless

FROM ${BUILD_IMAGE} AS build

WORKDIR /workspace

COPY pom.xml .
COPY src src

RUN mvn -B -DskipTests package

FROM ${RUNTIME_IMAGE}

USER root

RUN dnf update -y --security --releasever=latest \
    && dnf clean all

WORKDIR /app

COPY --from=build /workspace/target/*.jar app.jar

EXPOSE 8080

USER 1001

ENTRYPOINT ["sh", "-c", "exec java -jar /app/app.jar --server.port=${PORT:-8080}"]