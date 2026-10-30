# syntax=docker/dockerfile:1

ARG BUILD_IMAGE=maven:3.9-eclipse-temurin-25
ARG RUNTIME_IMAGE=eclipse-temurin:25.0.5_8-jre-noble

FROM ${BUILD_IMAGE} AS build

WORKDIR /workspace

COPY pom.xml .
COPY src src

RUN mvn -B -DskipTests package

FROM ${RUNTIME_IMAGE}

USER root

# Atualiza CA certificates do SO
RUN apt-get update \
    && apt-get install -y --only-upgrade ca-certificates \
    && rm -rf /var/lib/apt/lists/*

RUN keytool -list -cacerts -storepass changeit 2>/dev/null \
      | sed -nE 's/^(.*), [A-Za-z]{3} [0-9]{1,2}, [0-9]{4}, trustedCertEntry,.*$/\1/p' \
      | { grep -Ei 'emsign|aaa_certificate|comodoaaa' || true; } \
      | while IFS= read -r a; do \
          echo "Removendo: $a"; \
          keytool -delete -cacerts -storepass changeit -alias "$a"; \
        done

# Falha o build se sobrou alguma root afetada
RUN ! keytool -list -cacerts -storepass changeit | grep -Ei 'emsign|aaa_certificate|comodoaaa'

WORKDIR /app

COPY --from=build /workspace/target/*.jar app.jar

EXPOSE 8080

USER 1001

ENTRYPOINT ["sh", "-c", "exec java -jar /app/app.jar --server.port=${PORT:-8080}"]