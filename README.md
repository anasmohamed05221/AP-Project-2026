![StayEase](src/main/resources/static/images/logo.png)

> A modern hotel discovery and booking platform. Search hotels, explore rooms, and manage your stays with ease.

---

## Overview

StayEase is a full-stack web application built with Spring Boot MVC and Thymeleaf. Users can register, search hotels by city, browse rooms, make reservations, and leave reviews. Built as a university team project by 6 members, covering real-world concepts: Spring Security authentication, JPA/Hibernate ORM, layered architecture (Controller, Service, Repository, Model), and relational databases.

---

## Features

- User registration and login with BCrypt password hashing via Spring Security
- Search hotels by city with dynamic results
- Hotel detail pages with room listings and average review rating
- Room detail pages with full description, pricing, and availability status
- Booking system with date validation and overlap detection
- User dashboard with booking history and cancellation
- Hotel reviews with rating system (one review per user per hotel)

---

## Tech Stack

| Layer    | Technology                      |
|----------|---------------------------------|
| Frontend | Thymeleaf, HTML5, CSS3          |
| Backend  | Spring Boot MVC                 |
| Security | Spring Security (session-based) |
| ORM      | Spring Data JPA / Hibernate     |
| Database | PostgreSQL                      |
| Font     | Plus Jakarta Sans               |

---

## Project Structure

```
src/main/
├── java/com/sb/stayeaseap/
│   ├── config/          # Security configuration
│   ├── controller/      # HTTP layer, one controller per feature
│   ├── service/         # Business logic layer
│   ├── repository/      # Database access layer (JPA interfaces)
│   └── model/           # JPA entities mapped to DB tables
└── resources/
    ├── templates/        # Thymeleaf HTML templates
    ├── static/css/       # One CSS file per member
    ├── application.properties
    └── data.sql          # Sample seed data
```

---

## Team

| Name            | Responsibility                        |
|-----------------|---------------------------------------|
| Anas Mohamed    | Authentication, security and hotel/room pages |
| Mohamed Gamil   | Homepage and hotel search             |
| Mohsen Mohamed  | Booking flow                          |
| Yassin Abdullah | User dashboard                        |
| Ahmed Tarig     | Reviews and about page                |

---

## Getting Started

1. Clone the repo
2. Create the database in PostgreSQL: `CREATE DATABASE stayease_db;`
3. Update credentials in `src/main/resources/application.properties`
4. Run the app: `.\mvnw.cmd spring-boot:run`
5. Open `http://localhost:8080`

---

<p align="center">Built with care by the StayEase team</p>