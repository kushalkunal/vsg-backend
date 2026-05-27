# ─── Stage 1: Build ───────────────────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copy pom.xml first for dependency caching
COPY pom.xml .
RUN mvn dependency:go-offline -q

# Copy source and build
COPY src ./src
RUN mvn package -DskipTests -q

# ─── Stage 2: Runtime ─────────────────────────────────────────────────────────
# Use Debian-slim (jammy) instead of Alpine: native multi-arch support avoids
# the "exec /bin/sh: input/output error" QEMU failure on arm64 Alpine images.
FROM eclipse-temurin:21-jre-jammy AS runtime

WORKDIR /app

# Non-root user for security
RUN groupadd -r vsg && useradd -r -g vsg -s /sbin/nologin -d /app vsg
USER vsg

COPY --from=build /app/target/vsg-backend-*.jar app.jar

# Default profile — overridden at runtime by SPRING_PROFILES_ACTIVE env var
ENV SPRING_PROFILES_ACTIVE=prod

EXPOSE 8080

ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-Dspring.profiles.active=prod", \
  "-jar", "app.jar"]
