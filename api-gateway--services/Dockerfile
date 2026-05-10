# ================================
# Stage 1 — Build Application
# ================================

FROM maven:3.9-eclipse-temurin-21-alpine AS build

# Set working directory
WORKDIR /app

# Copy only pom.xml first for dependency caching
COPY pom.xml .

# Download dependencies separately for better Docker layer caching
RUN mvn dependency:go-offline

# Copy source code
COPY src ./src

# Build application
# clean removed for faster Docker builds
RUN mvn package -DskipTests

# ================================
# Stage 2 — Runtime Image
# ================================

FROM eclipse-temurin:21-jre-alpine

# Set metadata
LABEL maintainer="Ayush Rao Chaudhary"
LABEL service="api-gateway"
LABEL project="fintech-payment-system"

# Set working directory
WORKDIR /app

# Copy packaged jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose API Gateway port
EXPOSE 8080

# JVM optimizations for containers
ENV JAVA_OPTS="-Xms128m -Xmx256m"

# Run application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]