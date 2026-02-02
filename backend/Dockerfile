# =========================
# Stage 1: Build
# =========================
FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /app

# Copy pom first for dependency caching
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

# Copy source and build
COPY src ./src
RUN mvn -B clean package -DskipTests

# =========================
# Stage 2: Runtime
# =========================
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy the built jar from builder
COPY --from=builder /app/target/*.jar app.jar

# JVM options can be overridden via env
ENV JAVA_OPTS=""

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
