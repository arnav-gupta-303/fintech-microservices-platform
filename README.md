# Fintech Payment System

A production-grade microservices-based fintech payment platform built using Java, Spring Boot, Kafka, PostgreSQL, Docker, and JWT authentication. The system is designed with distributed system principles including idempotency, transaction safety, fault tolerance, event-driven communication, and scalable service isolation.

---

# Table of Contents

* [Overview](#overview)
* [System Architecture](#system-architecture)
* [Core Services](#core-services)
* [Technology Stack](#technology-stack)
* [Key Features](#key-features)
* [Project Structure](#project-structure)
* [System Workflow](#system-workflow)
* [Payment Flow](#payment-flow)
* [Microservices Communication](#microservices-communication)
* [Security Architecture](#security-architecture)
* [Database Design](#database-design)
* [Transaction Safety](#transaction-safety)
* [Idempotency Handling](#idempotency-handling)
* [Concurrency Control](#concurrency-control)
* [Failure Recovery](#failure-recovery)
* [Kafka Event Architecture](#kafka-event-architecture)
* [Dockerized Deployment](#dockerized-deployment)
* [Local Development Setup](#local-development-setup)
* [Running with Docker Compose](#running-with-docker-compose)
* [EC2 Deployment Guide](#ec2-deployment-guide)
* [Testing Strategy](#testing-strategy)
* [API Documentation](#api-documentation)
* [Environment Variables](#environment-variables)
* [Logging and Monitoring](#logging-and-monitoring)
* [Future Improvements](#future-improvements)
* [Author](#author)

---

# Overview

This project simulates a real-world fintech payment infrastructure using a distributed microservices architecture. Each service is independently deployable, owns its own database, and communicates using both synchronous REST APIs and asynchronous Kafka events.

The platform focuses heavily on:

* Transaction consistency
* Failure handling
* Retry-safe APIs
* Distributed architecture
* Financial data integrity
* Auditability
* Scalability

This is not a basic CRUD application. The system is designed to model production-level payment processing behavior.

---

# System Architecture

```text
Client Applications
        │
        ▼
API Gateway (8080)
        │
        ├───────────────┐
        ▼               ▼
Auth Service        User Service
        │
        ▼
Payment Service
        │
 ┌──────┼───────────┐
 ▼      ▼           ▼
Wallet  Fraud     Ledger
Service Service   Service
        │
        ▼
Kafka Event Bus
        │
        ▼
Notification Service
```

---

# Core Services

| Service              | Port | Responsibility                                      |
| -------------------- | ---- | --------------------------------------------------- |
| API Gateway          | 8080 | Central routing, JWT validation, request forwarding |
| Auth Service         | 8081 | User authentication, JWT token generation           |
| User Service         | 8082 | User profiles and KYC information                   |
| Wallet Service       | 8083 | Wallet balance management and transaction locking   |
| Payment Service      | 8084 | Payment orchestration and transaction coordination  |
| Ledger Service       | 8085 | Immutable transaction records and audit trail       |
| Fraud Service        | 8086 | Fraud validation and rule-based checks              |
| Notification Service | 8087 | Event-driven email and notification processing      |

---

# Technology Stack

## Backend

* Java 17
* Spring Boot
* Spring Security
* Spring Cloud Gateway
* Spring Data JPA
* Spring Kafka
* OpenFeign

## Database

* PostgreSQL
* Separate database per microservice

## Messaging

* Apache Kafka
* Zookeeper

## Infrastructure

* Docker
* Docker Compose
* NGINX
* Ubuntu EC2

## Authentication

* JWT Authentication
* Role-based authorization

## Testing

* JUnit 5
* Mockito
* Postman
* k6 Load Testing

---

# Key Features

* JWT Authentication and Authorization
* API Gateway Routing
* Event-Driven Kafka Architecture
* Distributed Microservices
* Wallet Balance Management
* Secure Payment Transfers
* Fraud Detection
* Immutable Ledger System
* Idempotent Payment APIs
* Pessimistic Database Locking
* Compensating Transactions
* Retry Handling
* Distributed Transaction Coordination
* Dockerized Deployment
* Production-Oriented Architecture

---

# Project Structure

```text
Fintech-Payment-Microservices-Platform-Java/
│
├── api-gateway-services/
├── auth-services/
├── user-services/
├── wallet-services/
├── payment-services/
├── ledger-services/
├── fraud-service/
├── notification-services/
├── fintech-frontend/
│
├── docker-compose.yml
├── pom.xml
├── README.md
└── API_TESTING.md
```

---

# System Workflow

1. User authenticates using Auth Service.
2. JWT token is generated.
3. Client sends requests through API Gateway.
4. Gateway validates JWT token.
5. Payment Service orchestrates transaction flow.
6. Fraud Service validates payment safety.
7. Wallet Service debits and credits balances.
8. Ledger Service stores immutable transaction records.
9. Kafka publishes payment events.
10. Notification Service consumes events and sends alerts.

---

# Payment Flow

```text
POST /api/payments/send
        │
        ▼
Validate JWT Token
        │
        ▼
Fraud Detection
        │
        ▼
Validate Sender Wallet
        │
        ▼
Acquire Database Lock
        │
        ▼
Debit Sender Balance
        │
        ▼
Create Debit Ledger Entry
        │
        ▼
Credit Receiver Balance
        │
        ▼
Create Credit Ledger Entry
        │
        ▼
Publish Kafka Event
        │
        ▼
Notification Service Consumes Event
        │
        ▼
Send Email/SMS Notification
```

---

# Microservices Communication

## Synchronous Communication

Used for:

* Authentication validation
* Fraud verification
* Wallet operations

Technology:

* REST APIs
* OpenFeign Clients

---

## Asynchronous Communication

Used for:

* Notifications
* Payment events
* Audit logging

Technology:

* Apache Kafka

Topics:

* payment-events
* notification-events

---

# Security Architecture

The system uses JWT-based authentication.

## Authentication Flow

```text
User Login
    │
    ▼
Auth Service
    │
    ▼
JWT Token Generated
    │
    ▼
Client Stores Token
    │
    ▼
Requests Pass Through Gateway
    │
    ▼
Gateway Validates JWT
```

## Security Features

* JWT Authentication
* Stateless Sessions
* Gateway-Level Authorization
* Protected APIs
* Secure Token Validation
* Unauthorized Request Blocking

---

# Database Design

Each microservice owns its own database.

| Service         | Database   |
| --------------- | ---------- |
| Auth Service    | auth_db    |
| User Service    | user_db    |
| Wallet Service  | wallet_db  |
| Payment Service | payment_db |
| Ledger Service  | ledger_db  |
| Fraud Service   | fraud_db   |

This prevents:

* Tight coupling
* Shared schema corruption
* Cross-service dependency issues

---

# Transaction Safety

The platform prioritizes financial consistency.

Key mechanisms:

* Atomic transactions
* Rollback handling
* Database locking
* Retry-safe operations
* Compensating transactions

The system ensures:

* No double spending
* No duplicate deductions
* Consistent ledger entries
* Safe concurrent transactions

---

# Idempotency Handling

Idempotency prevents duplicate payment execution.

## Example

If the same payment request is sent multiple times:

```http
Idempotency-Key: abc123
```

The system:

* Detects duplicate requests
* Returns cached response
* Prevents multiple deductions

---

# Concurrency Control

The Wallet Service uses pessimistic locking.

Purpose:

* Prevent simultaneous balance modification
* Avoid race conditions
* Maintain wallet consistency

Example:

* Two requests attempt to spend the same balance
* Only one transaction succeeds

---

# Failure Recovery

The system handles distributed failures gracefully.

Supported recovery scenarios:

* Service retry handling
* Kafka retry processing
* Database rollback
* Compensating refunds
* Partial transaction recovery

Examples:

* Fraud Service unavailable
* Notification Service down
* Kafka temporarily unavailable

The payment system remains stable without data corruption.

---

# Kafka Event Architecture

Kafka enables asynchronous communication between services.

## Event Flow

```text
Payment Service
      │
      ▼
Kafka Producer
      │
      ▼
payment-events topic
      │
      ▼
Notification Service Consumer
```

## Benefits

* Loose coupling
* Better scalability
* Event replay support
* Independent service execution
* Improved fault tolerance

---

# Dockerized Deployment

Every service is containerized using Docker.

## Docker Features

* Multi-stage builds
* Lightweight containers
* Service isolation
* Easy deployment
* Environment portability

---

# Local Development Setup

## Clone Repository

```bash
git clone <repository-url>
cd Fintech-Payment-Microservices-Platform-Java
```

---

## Build All Services

```bash
mvn clean package -DskipTests
```

---

# Running with Docker Compose

## Build and Start Containers

```bash
docker compose up -d --build
```

---

## Stop Containers

```bash
docker compose down
```

---

## Restart Services

```bash
docker compose restart
```

---

## View Running Containers

```bash
docker ps
```

---

## View Logs

```bash
docker compose logs -f
```

---

# EC2 Deployment Guide

## Launch EC2 Instance

Recommended:

* Ubuntu 22.04
* t3.medium or higher

Open Ports:

* 22
* 80
* 443
* 8080

---

## Install Docker

```bash
sudo apt update -y
sudo apt install docker.io docker-compose-v2 -y
```

---

## Start Docker

```bash
sudo systemctl enable docker
sudo systemctl start docker
```

---

## Clone Project

```bash
git clone <repository-url>
cd Fintech-Payment-Microservices-Platform-Java
```

---

## Deploy Application

```bash
sudo docker compose up -d --build
```

---

# Testing Strategy

## Unit Testing

Tools:

* JUnit 5
* Mockito

Covers:

* Business logic
* Fraud checks
* Wallet operations
* JWT validation

---

## Integration Testing

Covers:

* Database operations
* Kafka communication
* Service interaction

---

## API Testing

Tools:

* Postman
* Swagger/OpenAPI

Covers:

* Authentication
* Payments
* Wallet APIs
* Transaction flows

---

## Load Testing

Tool:

* k6

Covers:

* Concurrent payments
* Race conditions
* Wallet consistency

---

# API Documentation

API testing collection and request samples are available in:

```text
API_TESTING.md
```

Main APIs:

| API            | Method |
| -------------- | ------ |
| /auth/signup   | POST   |
| /auth/login    | POST   |
| /wallet/credit | POST   |
| /wallet/debit  | POST   |
| /payments/send | POST   |

---

# Environment Variables

Example configuration:

```env
SPRING_DATASOURCE_URL=
SPRING_DATASOURCE_USERNAME=
SPRING_DATASOURCE_PASSWORD=

JWT_SECRET=

KAFKA_BOOTSTRAP_SERVERS=

SERVER_PORT=
```

---

# Logging and Monitoring

Recommended production additions:

* Prometheus
* Grafana
* OpenTelemetry
* Zipkin
* ELK Stack

Monitoring goals:

* Transaction tracing
* Error visibility
* Kafka lag tracking
* Service health metrics

---

# Future Improvements

* Kubernetes Deployment
* CI/CD Pipelines
* Redis Caching
* Rate Limiting
* Distributed Tracing
* Multi-Currency Support
* Payment Scheduling
* Advanced Fraud Detection
* Machine Learning Fraud Analysis
* Service Discovery
* Circuit Breakers
* Horizontal Scaling

---

# Author

Built as a scalable fintech microservices backend architecture project focused on distributed systems, transaction consistency, fault tolerance, and production-grade payment processing practices.
