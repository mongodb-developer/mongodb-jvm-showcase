# syntax=docker/dockerfile:1

ARG BUILD_IMAGE=maven:3.9-eclipse-temurin-25
ARG RUNTIME_IMAGE=eclipse-temurin:25.0.5.1_2-jre-noble

FROM ${BUILD_IMAGE} AS build

WORKDIR /workspace

COPY pom.xml .
COPY src src

RUN mvn -B -DskipTests package

FROM ${RUNTIME_IMAGE}

USER root

# Update OS CA certificates
RUN apt-get update \
    && apt-get install -y --only-upgrade ca-certificates \
    && rm -rf /var/lib/apt/lists/*

# eMudhra CA mitigation
RUN keytool -list -cacerts -storepass changeit > /tmp/cacerts.txt \
    && sed -nE 's/^(.*), [A-Za-z]{3} [0-9]{1,2}, [0-9]{4}, trustedCertEntry,.*$/\1/p' /tmp/cacerts.txt \
      | { grep -Ei 'emsign|aaa_certificate|comodoaaa' || true; } \
      | while IFS= read -r a; do \
          echo "Removing: $a"; \
          keytool -delete -cacerts -storepass changeit -alias "$a" || exit 1; \
        done

# Verify certificate removal
RUN keytool -list -cacerts -storepass changeit > /tmp/cacerts.txt \
    && if grep -Ei 'emsign|aaa_certificate|comodoaaa' /tmp/cacerts.txt; then \
         exit 1; \
       fi \
    && rm /tmp/cacerts.txt

WORKDIR /app

COPY --from=build /workspace/target/*.jar app.jar

EXPOSE 8080

USER 1001

ENTRYPOINT ["sh", "-c", "exec java -jar /app/app.jar --server.port=${PORT:-8080}"]