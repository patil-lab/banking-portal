# Banking Portal

Welcome to the Banking Portal, a secure and modern banking application built with Spring Boot, Spring Security, Spring Kafka, MySQL, Docker, Java 17, and more.

## Table of Contents

- [Features](#features)
- [Entities](#entities)
- [APIs](#apis)
- [Unit Tests](#unit-tests)
- [Local Setup](#local-setup)
- [Docker Compose](#docker-compose)
- [Kubernetes Deployment](#kubernetes-deployment)
- [CircleCI Pipeline](#circleci-pipeline)

## Features

- User Registration and Authentication
- PIN Management (Create, Update)
- Account Operations (Deposit, Withdrawal, Transfer)
- User Details Retrieval
- Transaction History
- ...

## Entities

- User
- Account
- ExchangeRate
- Transaction

## APIs

### Authentication

- `POST /api/register`: Register a new user.
- `POST /api/login`: Authenticate and generate a token.

### PIN Management

- `POST /api/pin/create`: Create a new PIN.
- `PUT /api/pin/update`: Update an existing PIN.

### Account Operations

- `POST /api/deposit`: Cash deposit to an account.
- `POST /api/withdrawal`: Cash withdrawal from an account.
- `POST /api/transfer`: Fund transfer between accounts.

### User Details

- `GET /api/user/details`: Retrieve user details.

### Transaction History

- `GET /api/transactions`: Get all transactions.

## Unit Tests

The project includes comprehensive unit tests written using JUnit 5 to ensure the reliability of the application.

## Local Setup

1. Clone the repository: `git clone https://github.com/yourusername/banking-portal.git`
2. Navigate to the project directory: `cd banking-portal`
3. Run the application: `./mvnw spring-boot:run`

## Docker Compose

Use the provided `docker-compose.yml` file to run the application locally using Docker. Execute the following command:

```bash
docker-compose up

## Kubernetes Deployment
kubernetes_manifest.yaml


## CircleCI Pipeline

https://app.circleci.com/pipelines/circleci/DVjy7FwFwMjZx7RGBVbTxc/7HNZsV94t7bhZPR4hW761L/3/workflows/e4da9726-f0f1-41f7-9371-e260771cf02a/jobs/2 



