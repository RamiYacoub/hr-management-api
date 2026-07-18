# HR Management API

A RESTful API built with Spring Boot for managing departments, employees, leave requests, and expense claims.

This project was developed as part of a backend technical assessment and follows a layered architecture with validation, exception handling, authentication, API documentation, and unit testing.

---

## Features

- Department Management
- Employee Management
- Leave Type Management
- Leave Management
- Expense Type Management
- Expense Claim Management
- Search APIs
- Leave Days Calculation
- Expense Totals by Expense Type
- Request Validation
- Global Exception Handling
- HTTP Basic Authentication
- Swagger / OpenAPI Documentation
- Service Layer Unit Tests
- Sample Data Initialization

---

## Technology Stack

- Java 17
- Spring Boot 4.1.0
- Spring Data JPA
- Spring Security
- PostgreSQL
- Hibernate ORM
- OpenAPI (Swagger)
- Maven
- JUnit 5
- Mockito

---

## Development Environment

The project was developed and tested using:

- IntelliJ IDEA
- PostgreSQL 17
- Docker Desktop (used to run the PostgreSQL database)

> **Note:** Docker was used only for the database during development. The application itself is not containerized and can run against any PostgreSQL 17 instance.

---

## Prerequisites

Before running the project, make sure you have:

- Java 17
- PostgreSQL 17
- Git

---

## Database Configuration

Configure the datasource in:

```
src/main/resources/application.yaml
```

Example:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/hr_management
    username: hr_user
    password: hr_password
```

---

## Running the Application

### Clone the repository

```bash
git clone https://github.com/RamiYacoub/hr-management-api.git
```

### Start the application

Windows

```powershell
.\mvnw.cmd spring-boot:run
```

Linux / macOS

```bash
./mvnw spring-boot:run
```

The application will start on:

```
http://localhost:8080
```

---

## Authentication

The API is secured using HTTP Basic Authentication.

Default credentials:

```
Username: admin
Password: admin123
```

---

## API Documentation

Swagger UI is available after starting the application:

```
http://localhost:8080/swagger-ui/index.html
```

---

## Sample Data

Sample reference data is automatically inserted on application startup through:

```
src/main/resources/data.sql
```

The following data is included:

- Departments
- Employees
- Leave Types
- Expense Types

---

## Running Tests

Run all unit tests:

Windows

```powershell
.\mvnw.cmd test
```

Linux / macOS

```bash
./mvnw test
```

The project contains unit tests for the service layer using JUnit 5 and Mockito.

---

## Assumptions

Project assumptions are documented in:

```
ASSUMPTIONS.md
```

---

## Author

**Rami Yacoub**