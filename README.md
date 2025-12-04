# Beauty Salon Microservices System

A microservices-based system built with **Spring Boot 3** and **Maven**,
This is a Spring Boot application for managing a beauty salon. It provides functionalities for both clients and administrators, including appointment scheduling, user management, and beauty treatment management. The frontend is implemented using Thymeleaf templates.
The system contains two services:

### ✔ **Beauty Salon Service (Main Service)**

Handles treatments, appointments, and salon logic.

### ✔ **User Service**

Manages users, authentication, and roles.

The services communicate using **OpenFeign**.

------------------------------------------------------------------------

## 📌 Features

### Beauty Salon Service

-   Manage treatments\
-   Create and schedule appointments\
-   Assign employees\
-   Communicate with User Service via Feign\
-   Handle exceptions and validations

### User Service

-   User registration & login\
-   Role-based access\
-   CRUD operations on users\
-   Exposes APIs consumed by the main service

------------------------------------------------------------------------

## Tech Stack

### Backend
- Java 17
- Spring Boot
- Spring MVC
- Spring Security
- Spring Data JPA
- REST API for user-related functionalities

### Frontend
- Thymeleaf
- HTML / CSS / JavaScript

### Database
- MySQL (production)
- H2 (for testing)

### Testing
- JUnit 5
- Mockito
- Spring Boot Test / WebMvcTest

### Build Tool
- Maven

### Version Control
- Git / GitHub
    
------------------------------------------------------------------------

## Features

### Client Features
- User registration and login
- View, book, edit, or cancel appointments
- View past appointments
- Browse available beauty treatments

### Admin Features
- Manage users and appointments
- Edit beauty treatments
- Role-based access (ADMIN vs USER)
- Mark appointments as completed

------------------------------------------------------------------------

## Functionalities

- Secure login and role-based authentication
- Appointment CRUD operations
- REST API for user management
- Flash messages for actions in frontend
- Sorting and filtering of appointments
- Input validation and error handling

---

## Integrations

- MySQL database for persistent storage
- Spring Security for authentication and authorization
- REST API endpoints for user operations
- 
------------------------------------------------------------------------

## How to Run

1. Clone the repository:
   ```bash
   git clone https://https://github.com/KrasenaMarkova/beauty-salon
------------------------------------------------------------------------

## Navigate to the project directory:

cd beauty-salon

## Configure application.properties with your database credentials.

## Build and run the application:

mvn spring-boot:run

## Open your browser and go to:
http://localhost:8080

------------------------------------------------------------------------

## 🧪 Testing

``` 
mvn test
```

------------------------------------------------------------------------

## Project Structure

src/main/java – Main application code

src/main/resources/templates – Thymeleaf templates

src/main/resources/static – CSS, JS, images

src/test/java – Unit and integration tests

------------------------------------------------------------------------

## 📄 License

MIT License.
