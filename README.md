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

### Tools

- Java 17+
- Maven 
- PostgreSQL 
- A Cloudinary
- A Gmail for SMTP

`



## API Overview

### User Controller — `/auth/users`

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/auth/users/register` | Public | Register a new user |
| POST | `/auth/users/login` | Public | Login and receive JWT |
| GET | `/auth/users/register/verify?token=` | Public | Verify email address |
| GET | `/auth/users/resetPassword` | Public | Request password reset email |
| GET | `/auth/users/reset-password?token=` | Public | Open password reset form |
| POST | `/auth/users/resetPassword?token=` | Public | Submit new password from form |
| PUT | `/auth/users/change-password` | User | Change current password |
| PUT | `/auth/users/{userId}/soft-delete` | Admin | Deactivate a user account |
| PUT | `/auth/users/{userId}/promote` | Admin | Promote user to ADMIN role |

---

### Movie Controller — `/api/movies`

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/api/movies` | Public | Get all movies |
| GET | `/api/movies/{id}` | Public | Get movie by ID |
| GET | `/api/movies/search?title=` | Public | Search movies by title |
| GET | `/api/movies/genre/{genre}` | Public | Get movies by genre |
| POST | `/api/movies` | Admin | Create a movie (multipart: `movie` + `poster`) |
| PUT | `/api/movies/{id}` | Admin | Update a movie (multipart: `movie` + `poster`) |
| DELETE | `/api/movies/{id}` | Admin | Delete a movie |

---

### Hall Controller — `/api/halls`

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/api/halls` | Public | Get all halls |
| GET | `/api/halls/{id}` | Public | Get hall by ID |
| GET | `/api/halls/{id}/seats` | Public | Get all seats in a hall (no availability) |
| POST | `/api/halls` | Admin | Create a hall (auto-generates seats) |
| PUT | `/api/halls/{id}` | Admin | Update a hall (regenerates seats if size changed) |
| DELETE | `/api/halls/{id}` | Admin | Delete a hall and its seats |

---

### Showtime Controller — `/api/showtimes`

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/api/showtimes` | Public | Get all showtimes |
| GET | `/api/showtimes/{id}` | Public | Get showtime by ID |
| GET | `/api/showtimes/{id}/seats` | Public | Get seats with availability for a showtime |
| GET | `/api/showtimes/movie/{movieId}` | Public | Get showtimes for a movie |
| GET | `/api/showtimes/hall/{hallId}` | Public | Get showtimes for a hall |
| POST | `/api/showtimes` | Admin | Create a showtime (validates no hall overlap) |
| PUT | `/api/showtimes/{id}` | Admin | Update a showtime |
| DELETE | `/api/showtimes/{id}` | Admin | Delete a showtime |

---

### Booking Controller — `/api/bookings`

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/api/bookings/my` | User | Get current user's bookings |
| GET | `/api/bookings/{id}` | User | Get booking by ID |
| POST | `/api/bookings` | User | Book a seat for a showtime |
| PUT | `/api/bookings/{id}/cancel` | User (owner) | Cancel a booking |
| GET | `/api/bookings/showtime/{showtimeId}` | Admin | Get all bookings for a showtime |

