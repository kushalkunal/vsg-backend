# ─── Stage 1: Build ───────────────────────────────────────────────────────────
# $BUILDPLATFORM = host native arch (e.g. linux/amd64).
# Running Maven on the native platform avoids QEMU entirely for the build stage.
# The JAR produced is platform-independent Java bytecode.
FROM --platform=$BUILDPLATFORM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copy pom.xml first for dependency caching
COPY pom.xml .
RUN mvn dependency:go-offline -q

# Copy source and build
COPY src ./src
RUN mvn package -DskipTests -q

# ─── Stage 2: Runtime ─────────────────────────────────────────────────────────
# eclipse-temurin jammy for proper multi-arch manifest support.
# NO RUN commands in this stage — QEMU arm64 emulation cannot exec /bin/sh
# on this host. Use numeric UID 65534 (nobody/nogroup) with COPY --chown
# to achieve non-root execution without any shell invocation.
FROM eclipse-temurin:21-jre-jammy AS runtime

WORKDIR /app

# Copy jar and assign ownership to nobody (65534) in one layer — no shell needed
COPY --from=build --chown=65534:65534 /app/target/vsg-backend-*.jar app.jar

USER 65534

# Default profile — overridden at runtime by SPRING_PROFILES_ACTIVE env var
ENV SPRING_PROFILES_ACTIVE=prod

EXPOSE 8080

ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-Dspring.profiles.active=prod", \
  "-jar", "app.jar"]
