# ================================
# Stage 1 — Build Application
# ================================

FROM maven:3.9-eclipse-temurin-21-alpine AS build

# Set working directory
WORKDIR /app

# Copy pom.xml first for Docker dependency caching
COPY pom.xml .

# Download Maven dependencies
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
LABEL service="user-service"
LABEL project="fintech-payment-system"

# Set working directory
WORKDIR /app

# Copy generated JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose User Service port
EXPOSE 8082

# JVM memory optimization for containers
ENV JAVA_OPTS="-Xms128m -Xmx256m"

# Run Spring Boot application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]