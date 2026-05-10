# ================================
# Stage 1 — Build Application
# ================================

FROM maven:3.9-eclipse-temurin-21-alpine AS build

# Set working directory
WORKDIR /app

# Copy Maven configuration first for Docker cache optimization
COPY pom.xml .

# Download dependencies
RUN mvn dependency:go-offline

# Copy source code
COPY src ./src

# Build application
RUN mvn package -DskipTests

# ================================
# Stage 2 — Runtime Image
# ================================

FROM eclipse-temurin:21-jre-alpine

# Metadata
LABEL maintainer="Ayush Rao Chaudhary"
LABEL service="ledger-service"
LABEL project="fintech-payment-system"

# Set working directory
WORKDIR /app

# Copy built JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose Ledger Service port
EXPOSE 8085

# JVM container optimization
ENV JAVA_OPTS="-Xms128m -Xmx256m"

# Start Spring Boot application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]