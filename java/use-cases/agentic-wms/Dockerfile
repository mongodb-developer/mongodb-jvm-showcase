
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

# Update the OS certificate bundle
RUN apt-get update \
    && apt-get install -y --only-upgrade ca-certificates \
    && rm -rf /var/lib/apt/lists/*

# Oracle eMudhra security mitigation for Temurin
RUN keytool -delete -cacerts -storepass changeit \
      -alias "cn_emsign_ecc_root_ca__g3,o_emudhra_technologies_limited,ou_emsign_pki,c_in [jdk]" \
    && keytool -delete -cacerts -storepass changeit \
      -alias "cn_emsign_root_ca__g1,o_emudhra_technologies_limited,ou_emsign_pki,c_in [jdk]"

# Verify that the affected roots are no longer trusted by Java
RUN ! keytool -list -cacerts -storepass changeit \
    | grep -Ei 'cn_emsign_ecc_root_ca__g3|cn_emsign_root_ca__g1|cn_aaa_certificate_services'

WORKDIR /app

COPY --from=build /workspace/target/*.jar app.jar

EXPOSE 8080

USER 1001

ENTRYPOINT ["sh", "-c", "exec java -jar /app/app.jar --server.port=${PORT:-8080}"]
