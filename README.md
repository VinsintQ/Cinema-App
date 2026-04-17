# Cinema App

A RESTful backend API for a cinema booking system. Users can browse movies, view showtimes, and reserve seats. Admins can manage movies, halls, and showtimes. The system handles concurrent seat bookings safely using pessimistic and optimistic locking.

---

## Technologies Used

- **Java 17**
- **Spring Boot 3.2.5** — REST API framework
- **Spring Security + JWT** — Authentication and authorization
- **Spring Data JPA / Hibernate** — ORM and database access
- **PostgreSQL** — Relational database
- **Cloudinary** — Movie poster image uploads
- **JavaMailSender + Thymeleaf** — Email verification and password reset
- **Lombok** — Boilerplate reduction
- **Maven** — Dependency management

---

## General Approach

The project follows a layered architecture — controllers handle HTTP routing, services contain business logic, and repositories manage data access. Each domain (User, Movie, Hall, Showtime, Booking) has its own controller, service, and repository.

Security is stateless using JWT tokens. On registration, users receive a verification email before they can log in. Role-based access control separates public endpoints from user-only and admin-only operations. Password reset is handled via a tokenized email link that opens a Thymeleaf form page.

Seat booking uses a two-layer concurrency control strategy: pessimistic locking at the database level (SELECT FOR UPDATE) prevents race conditions when two users attempt to book the same seat simultaneously, and optimistic locking via a `@Version` field on the `Seat` entity acts as a safety net against stale reads.




## Installation & Setup

### Prerequisites

- Java 17+
- Maven 3.8+
- PostgreSQL running locally
- A Cloudinary account
- A Gmail account with an app password for SMTP

### 1. Clone the repository

```bash
git clone https://github.com/your-username/cinema-app.git
cd cinema-app
```

### 2. Configure `application.properties`

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/cinema
spring.datasource.username=your_pg_username
spring.datasource.password=your_pg_password

cloudinary.cloud-name=your_cloud_name
cloudinary.api-key=your_api_key
cloudinary.api-secret=your_api_secret

spring.mail.username=your_gmail@gmail.com
spring.mail.password=your_app_password
```

### 3. Seed the roles table

```sql
INSERT INTO roles (name) VALUES ('ROLE_USER');
INSERT INTO roles (name) VALUES ('ROLE_ADMIN');
```

### 4. Run the application

```bash
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080`.

---

## API Overview

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/auth/users/register` | Public | Register a new user |
| POST | `/auth/users/login` | Public | Login and receive JWT |
| GET | `/auth/users/register/verify?token=` | Public | Verify email |
| GET | `/auth/users/resetPassword` | Public | Request password reset |
| GET | `/api/movies` | Public | List all movies |
| GET | `/api/showtimes/{id}/seats` | Public | Seat availability for a showtime |
| POST | `/api/bookings` | User | Book a seat |
| PUT | `/api/bookings/{id}/cancel` | User | Cancel a booking |
| POST | `/api/movies` | Admin | Add a movie |
| POST | `/api/halls` | Admin | Create a hall |
| POST | `/api/showtimes` | Admin | Schedule a showtime |
