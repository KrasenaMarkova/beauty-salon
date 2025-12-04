# Beauty Salon Microservices System

A microservices-based system built with **Spring Boot 3** and **Maven**,
designed to manage a beauty salon business.\
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

## 🧱 Technology Stack

### Backend

-   Java 17+
-   Spring Boot 3
-   Maven
-   Spring Web
-   Spring Data JPA
-   Validation
-   OpenFeign

### Database

-   MySQL

### Testing

-   JUnit 5
-   Spring Boot Test

------------------------------------------------------------------------

## 🗂️ Project Structure

    beauty-salon-system/
    │
    ├── beauty-salon/
    │   ├── appointment
    │   ├── beautytreatment
    │   ├── bootstrap
    │   ├── config
    │   ├── email
    │   ├── employee
    │   └── pom.xml
    │
    └── user-service/
        ├── controller
        ├── service
        ├── entity
        ├── repository
        ├── dto
        └── pom.xml
    
------------------------------------------------------------------------

## 🔗 Inter-Service Communication (Feign)

### Example Feign Client

``` java
@FeignClient(name = "beauty-salon-rest", url = "http://localhost:8081/api/v1/users")
public interface UserServiceClient {
  @GetMapping("/{id}")
  ResponseEntity<UserDto> loadById(@PathVariable("id") UUID id);
}
```

### application.yml (Beauty Salon)

``` yaml
services:
  user:
    url: http://localhost:8081
```

------------------------------------------------------------------------

## ▶️ Running the Project

### 1. Clone repository

``` bash
git clone https://github.com/your/repo.git
cd beauty-salon-system
```

### 2. Start User Service

``` bash
cd user-service
mvn spring-boot:run
```

### 3. Start Beauty Salon Service

``` bash
cd beauty-salon-service
mvn spring-boot:run
```

------------------------------------------------------------------------

## 📘 API Documentation

  Service        URL
  -------------- ---------------------------------------
  Beauty Salon   http://localhost:8080
  User Service   http://localhost:8081

------------------------------------------------------------------------

## 🧪 Testing

``` bash
mvn test
```

------------------------------------------------------------------------

## 🤝 Contributing

1.  Fork the project\
2.  Create a feature branch\
3.  Commit your changes\
4.  Open a pull request

------------------------------------------------------------------------

## 📄 License

MIT License.
