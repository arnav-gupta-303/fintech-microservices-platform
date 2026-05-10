# ================================
# Stage 1 — Build Application
# ================================

FROM maven:3.9-eclipse-temurin-21-alpine AS build

# Set working directory
WORKDIR /app

# Copy pom.xml separately for dependency caching
COPY pom.xml .

# Download dependencies
RUN mvn dependency:go-offline

# Copy application source code
COPY src ./src

# Build Spring Boot application
RUN mvn package -DskipTests

# ================================
# Stage 2 — Runtime Image
# ================================

FROM eclipse-temurin:21-jre-alpine

# Metadata
LABEL maintainer="Ayush Rao Chaudhary"
LABEL service="auth-service"
LABEL project="fintech-payment-system"

# Set working directory
WORKDIR /app

# Copy generated JAR from builder stage
COPY --from=build /app/target/*.jar app.jar

# Expose service port
EXPOSE 8081

# JVM memory optimization for containers
ENV JAVA_OPTS="-Xms128m -Xmx256m"

# Start application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]