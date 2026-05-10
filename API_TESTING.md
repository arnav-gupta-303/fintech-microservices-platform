# Fintech Payment System API Testing Guide

This document provides complete API testing instructions for the Fintech Payment System microservices platform. It covers authentication, wallet operations, payment processing, ledger verification, fraud validation, Docker commands, Postman setup, testing flow, and failure scenarios.

---

# Table of Contents

* [Base URL](#base-url)
* [Authentication](#authentication)
* [Auth Service APIs](#auth-service-apis)

  * [Signup](#signup)
  * [Login](#login)
* [User Service APIs](#user-service-apis)

  * [Create or Update Profile](#create-or-update-profile)
  * [Get Profile](#get-profile)
* [Wallet Service APIs](#wallet-service-apis)

  * [Create Wallet](#create-wallet)
  * [Get Wallet](#get-wallet)
  * [Top Up Wallet](#top-up-wallet)
  * [Debit Wallet](#debit-wallet)
* [Payment Service APIs](#payment-service-apis)

  * [Send Payment](#send-payment)
  * [Get Payment History](#get-payment-history)
* [Ledger Service APIs](#ledger-service-apis)

  * [Transaction History](#transaction-history)
* [Fraud Service APIs](#fraud-service-apis)

  * [Fraud Check](#fraud-check)
* [Recommended API Testing Order](#recommended-api-testing-order)
* [Payment Processing Workflow](#payment-processing-workflow)
* [Fraud Detection Rules](#fraud-detection-rules)
* [Idempotency Testing](#idempotency-testing)
* [Concurrency Testing](#concurrency-testing)
* [Failure Testing](#failure-testing)
* [Postman Environment Setup](#postman-environment-setup)
* [Docker Commands](#docker-commands)
* [Logs and Debugging](#logs-and-debugging)
* [Expected HTTP Status Codes](#expected-http-status-codes)
* [Security Testing](#security-testing)
* [Production Testing Recommendations](#production-testing-recommendations)

---

# Base URL

```text id="17q1z5"
http://localhost:8080
```

All requests should be sent through the API Gateway.

---

# Authentication

All protected APIs require a JWT token.

Add the following header:

```http id="x7o5i8"
Authorization: Bearer <token>
```

You must first:

1. Signup
2. Login
3. Copy JWT token
4. Use token in protected APIs

---

# Auth Service APIs

The Auth Service manages:

* User registration
* User login
* JWT token generation
* Authentication validation

---

# Signup

## Endpoint

```http id="w9t3lv"
POST /api/auth/signup
```

---

## Headers

```http id="jlwm8n"
Content-Type: application/json
```

---

## Request Body

```json id="4c3r6d"
{
  "email": "user@gmail.com",
  "password": "123456",
  "fullName": "John Doe"
}
```

---

## Expected Response

```json id="4gh8d2"
{
  "message": "User registered successfully"
}
```

---

## Test Scenarios

| Scenario        | Expected Result |
| --------------- | --------------- |
| Valid signup    | User created    |
| Duplicate email | 409 Conflict    |
| Invalid email   | 400 Bad Request |
| Weak password   | 400 Bad Request |

---

# Login

## Endpoint

```http id="h1tjlwm"
POST /api/auth/login
```

---

## Headers

```http id="s6uxaq"
Content-Type: application/json
```

---

## Request Body

```json id="upxj3w"
{
  "email": "user@gmail.com",
  "password": "123456"
}
```

---

## Expected Response

```json id="n6tz3d"
{
  "token": "eyJhbGc..."
}
```

Copy the token for all protected requests.

---

## Test Scenarios

| Scenario          | Expected Result    |
| ----------------- | ------------------ |
| Valid credentials | JWT token returned |
| Wrong password    | 401 Unauthorized   |
| Unknown email     | 404 Not Found      |
| Empty fields      | 400 Bad Request    |

---

# User Service APIs

The User Service manages:

* User profile
* Contact details
* KYC information

---

# Create or Update Profile

## Endpoint

```http id="4eyfw7"
POST /api/users/profile
```

---

## Headers

```http id="ykk7gw"
Authorization: Bearer <token>
```

---

## Request Body

```json id="yfzjlwm"
{
  "fullName": "John Doe",
  "phone": "9999999999",
  "address": "New Delhi, India"
}
```

---

## Expected Response

```json id="3xmgk8"
{
  "message": "Profile updated successfully"
}
```

---

# Get Profile

## Endpoint

```http id="13n8l2"
GET /api/users/profile
```

---

## Headers

```http id="j7q6dz"
Authorization: Bearer <token>
```

---

## Expected Response

```json id="hr0n5o"
{
  "fullName": "John Doe",
  "phone": "9999999999",
  "address": "New Delhi, India"
}
```

---

# Wallet Service APIs

The Wallet Service handles:

* Wallet creation
* Balance management
* Credits and debits
* Concurrency safety

---

# Create Wallet

## Endpoint

```http id="n0v1m9"
POST /api/wallet/create
```

---

## Headers

```http id="5nmwnq"
Authorization: Bearer <token>
```

---

## Expected Response

```json id="z7ij7w"
{
  "message": "Wallet created successfully"
}
```

---

# Get Wallet

## Endpoint

```http id="xv9hbo"
GET /api/wallet
```

---

## Headers

```http id="o3hzph"
Authorization: Bearer <token>
```

---

## Expected Response

```json id="rkw7dd"
{
  "balance": 1000.00
}
```

---

# Top Up Wallet

## Endpoint

```http id="g8r4gx"
POST /api/wallet/topup
```

---

## Headers

```http id="yr7fdr"
Authorization: Bearer <token>
```

---

## Request Body

```json id="a5sklm"
{
  "amount": 1000.00
}
```

---

## Expected Response

```json id="6n4yq8"
{
  "message": "Wallet topped up successfully"
}
```

---

# Debit Wallet

## Endpoint

```http id="j0e0kl"
POST /api/wallet/debit
```

---

## Headers

```http id="5m4jlwm"
Authorization: Bearer <token>
```

---

## Request Body

```json id="u4y4ko"
{
  "amount": 100.00,
  "idempotencyKey": "debit-001"
}
```

---

## Expected Response

```json id="9kylxw"
{
  "message": "Wallet debited successfully"
}
```

---

## Test Scenarios

| Scenario                  | Expected Result |
| ------------------------- | --------------- |
| Valid debit               | Success         |
| Duplicate idempotency key | Cached response |
| Insufficient balance      | 400 Bad Request |
| Negative amount           | 400 Bad Request |

---

# Payment Service APIs

The Payment Service orchestrates:

* Payment flow
* Fraud validation
* Wallet debit/credit
* Ledger synchronization
* Kafka events

---

# Send Payment

## Endpoint

```http id="n9mjlwm"
POST /api/payments/send
```

---

## Headers

```http id="jlwmw8"
Authorization: Bearer <token>
```

---

## Request Body

```json id="w7jyz9"
{
  "receiverEmail": "receiver@gmail.com",
  "amount": 100.00,
  "idempotencyKey": "pay-001"
}
```

---

## Expected Response

```json id="3laz6j"
{
  "message": "Payment successful"
}
```

---

## Internal Flow Triggered

* Fraud validation
* Sender wallet debit
* Receiver wallet credit
* Ledger entry creation
* Kafka event publishing
* Notification processing

---

## Test Scenarios

| Scenario           | Expected Result |
| ------------------ | --------------- |
| Valid transfer     | Success         |
| Insufficient funds | Failure         |
| Fraud detected     | Rejected        |
| Duplicate request  | Cached response |
| Receiver not found | 404 Not Found   |

---

# Get Payment History

## Endpoint

```http id="c5jokh"
GET /api/payments/history
```

---

## Headers

```http id="jlwm3r"
Authorization: Bearer <token>
```

---

## Expected Response

```json id="u4dy5q"
[
  {
    "amount": 100.00,
    "status": "SUCCESS"
  }
]
```

---

# Ledger Service APIs

The Ledger Service stores immutable transaction records.

The ledger is append-only.

No update or delete operations are allowed.

---

# Transaction History

## Endpoint

```http id="e4jjlwm"
GET /api/ledger/history
```

---

## Headers

```http id="r3njlwm"
Authorization: Bearer <token>
```

---

## Expected Response

```json id="r5jlwm"
[
  {
    "type": "DEBIT",
    "amount": 100.00
  },
  {
    "type": "CREDIT",
    "amount": 100.00
  }
]
```

---

# Fraud Service APIs

The Fraud Service performs:

* Rule-based transaction validation
* Duplicate reference detection
* Transaction rate limiting

Usually this service is called internally by the Payment Service.

---

# Fraud Check

## Endpoint

```http id="z6jlwm"
POST /api/fraud/check
```

---

## Request Body

```json id="4jlwm7"
{
  "email": "user@gmail.com",
  "amount": 100.00,
  "referenceId": "pay-001"
}
```

---

## Expected Response

```json id="jlwm9v"
{
  "safe": true
}
```

---

# Recommended API Testing Order

Follow this order for successful end-to-end testing.

---

## Step 1 — Signup

```text id="jlwm1d"
POST /api/auth/signup
```

---

## Step 2 — Login

```text id="7jlwmk"
POST /api/auth/login
```

Copy JWT token.

---

## Step 3 — Create Profile

```text id="4jlwm8"
POST /api/users/profile
```

---

## Step 4 — Create Wallet

```text id="1jlwm0"
POST /api/wallet/create
```

---

## Step 5 — Top Up Wallet

```text id="2jlwm5"
POST /api/wallet/topup
```

Add balance before payment testing.

---

## Step 6 — Send Payment

```text id="7jlwm4"
POST /api/payments/send
```

This triggers:

* Fraud validation
* Wallet debit
* Wallet credit
* Ledger entries
* Kafka events
* Notifications

---

## Step 7 — Check Ledger

```text id="3jlwm2"
GET /api/ledger/history
```

---

## Step 8 — Check Payment History

```text id="8jlwm1"
GET /api/payments/history
```

---

# Payment Processing Workflow

```text id="8r1x6v"
Client
   │
   ▼
API Gateway
   │
   ▼
Payment Service
   │
   ├── Fraud Service
   ├── Wallet Service
   ├── Ledger Service
   └── Kafka Producer
            │
            ▼
Notification Service
```

---

# Fraud Detection Rules

Transactions fail if:

* Amount exceeds configured threshold
* Duplicate referenceId detected
* More than allowed requests per minute
* Suspicious transaction pattern found

---

# Idempotency Testing

Idempotency prevents duplicate payment execution.

---

## Example Request

```json id="jlwm6u"
{
  "amount": 100.00,
  "idempotencyKey": "pay-001"
}
```

---

## Expected Behavior

Sending the same request multiple times:

* Does not debit balance twice
* Returns cached response
* Maintains transaction consistency

---

# Concurrency Testing

The Wallet Service uses pessimistic locking.

Purpose:

* Prevent double spending
* Avoid race conditions
* Maintain balance consistency

---

## Example Scenario

Wallet balance = 1000

Two concurrent requests:

* Request A → debit 800
* Request B → debit 800

Expected:

* Only one succeeds
* One fails due to insufficient balance

---

# Failure Testing

Test system resilience by simulating:

* Kafka unavailable
* Fraud service failure
* Database shutdown
* Notification service downtime

Expected:

* No money corruption
* Retry handling
* Safe rollback
* Compensating transactions

---

# Postman Environment Setup

## Add Environment Variables

| Variable | Value                                          |
| -------- | ---------------------------------------------- |
| baseUrl  | [http://localhost:8080](http://localhost:8080) |
| token    | JWT token from login                           |

---

# Authorization Header

```http id="jlwm2k"
Authorization: Bearer {{token}}
```

---

# Docker Commands

## Start Services

```bash id="jlwm7q"
docker compose up --build
```

---

## Run in Detached Mode

```bash id="4jlwm1"
docker compose up -d --build
```

---

## Stop Containers

```bash id="7jlwm9"
docker compose down
```

---

## Restart Services

```bash id="1jlwm8"
docker compose restart
```

---

## View Running Containers

```bash id="9jlwm2"
docker ps
```

---

# Logs and Debugging

## View All Logs

```bash id="3jlwm6"
docker compose logs -f
```

---

## View Specific Service Logs

```bash id="5jlwm3"
docker compose logs -f payment-service
```

---

## Inspect Kafka Logs

```bash id="6jlwm5"
docker compose logs -f kafka
```

---

# Expected HTTP Status Codes

| Status Code | Meaning               |
| ----------- | --------------------- |
| 200         | Success               |
| 201         | Resource Created      |
| 400         | Invalid Request       |
| 401         | Unauthorized          |
| 403         | Forbidden             |
| 404         | Resource Not Found    |
| 409         | Duplicate Request     |
| 500         | Internal Server Error |

---

# Security Testing

Test these scenarios:

| Test                    | Expected Result  |
| ----------------------- | ---------------- |
| Invalid JWT             | 401 Unauthorized |
| Expired JWT             | Access denied    |
| Missing token           | Unauthorized     |
| Invalid endpoint access | Forbidden        |
| Gateway bypass attempt  | Blocked          |

---

# Production Testing Recommendations

For production-grade validation, add:

* k6 load testing
* JMeter stress testing
* Chaos testing
* Kafka retry testing
* Distributed tracing
* Prometheus monitoring
* Grafana dashboards
* OpenTelemetry tracing

Recommended production focus areas:

* Transaction consistency
* Wallet safety
* Ledger synchronization
* Retry handling
* Failure recovery
* Kafka reliability
* Concurrent transaction safety
