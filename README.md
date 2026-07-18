<div align="center">

# JWT Redis Authentication Service

A secure RESTful authentication service built with **Spring Boot**, **Spring Security**, **JWT**, **Redis**, and **PostgreSQL**.

The project provides user registration, login, role-based authorization, secure logout, and JWT token invalidation using a Redis-backed token blacklist.

</div>

---

## Features

* User registration
* User authentication with JWT
* Password encryption with Spring Security
* Role-based authorization
* Stateless authentication
* JWT validation for protected endpoints
* Secure logout mechanism
* Token blacklist management with Redis
* Automatic blacklist expiration based on JWT expiration time
* PostgreSQL database integration
* Global exception handling
* Swagger/OpenAPI documentation
* Docker Compose support for infrastructure services

---

## Tech Stack

| Technology        | Purpose                          |
| ----------------- | -------------------------------- |
| Java 17           | Programming language             |
| Spring Boot 3     | Application framework            |
| Spring Security   | Authentication and authorization |
| JWT               | Stateless access tokens          |
| Redis             | Invalidated token blacklist      |
| PostgreSQL        | Persistent user storage          |
| Spring Data JPA   | Database access                  |
| Hibernate         | ORM implementation               |
| Maven             | Dependency management            |
| Docker Compose    | Redis and PostgreSQL containers  |
| Swagger / OpenAPI | API documentation                |
| Lombok            | Boilerplate code reduction       |

---

## Authentication Flow

### Login

1. The client sends the user's credentials to the login endpoint.
2. Spring Security validates the email and password.
3. The application generates a signed JWT.
4. The JWT is returned to the client.
5. The client sends the token with protected requests:

```http
Authorization: Bearer <your-jwt-token>
```

### Protected Request

1. The JWT authentication filter extracts the token from the request.
2. The application verifies the token signature and expiration date.
3. Redis is checked to determine whether the token has been blacklisted.
4. When the token is valid and not blacklisted, the request is authenticated.

### Logout

1. The authenticated user calls the logout endpoint.
2. The current JWT is added to Redis.
3. The Redis entry is stored only until the original JWT expiration time.
4. Any subsequent request using the same token is rejected.

This approach preserves stateless JWT authentication while still allowing tokens to be invalidated before they expire.

---

## Project Structure

```text
src/main/java/com/redis/redissessionmanagement
├── config
│   ├── ApplicationConfig
│   ├── OpenApiConfig
│   └── SecurityConfig
├── controller
│   └── AuthController
├── dto
│   ├── request
│   └── response
├── entity
├── enumarate
├── exception
├── repository
├── security
│   ├── JwtAuthenticationFilter
│   └── JwtService
├── service
│   ├── impl
│   ├── AuthService
│   └── TokenCacheService
└── RedisSessionManagementApplication
```

---

## API Endpoints

Base path:

```text
/api/auth
```

| Method | Endpoint             | Authentication | Description                              |
| ------ | -------------------- | -------------: | ---------------------------------------- |
| POST   | `/api/auth/register` |             No | Creates a new user                       |
| POST   | `/api/auth/login`    |             No | Authenticates the user and returns a JWT |
| POST   | `/api/auth/logout`   |   Bearer Token | Invalidates the current JWT              |

---

## Example Requests

### Register

```http
POST /api/auth/register
Content-Type: application/json
```

```json
{
  "name": "Elif Akyol",
  "email": "elif@example.com",
  "password": "StrongPassword123"
}
```

### Login

```http
POST /api/auth/login
Content-Type: application/json
```

```json
{
  "email": "elif@example.com",
  "password": "StrongPassword123"
}
```

Example response:

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

### Logout

```http
POST /api/auth/logout
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

After logout, the token is stored in Redis and cannot be used again.

---

## Prerequisites

Make sure the following tools are installed:

* Java 17 or later
* Maven
* Docker
* Docker Compose

---

## Installation

Clone the repository:

```bash
git clone https://github.com/Elifakyol1020/Jwt-Redis-Auth-Service.git
cd Jwt-Redis-Auth-Service
```

Start PostgreSQL and Redis:

```bash
docker compose up -d
```

Run the Spring Boot application:

### macOS / Linux

```bash
./mvnw spring-boot:run
```

### Windows

```bash
mvnw.cmd spring-boot:run
```

The application will be available at:

```text
http://localhost:8080
```

---

## Configuration

The application configuration is located in:

```text
src/main/resources/application.yml
```

The following properties should be configured according to your environment:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/your_database
    username: your_username
    password: your_password

  data:
    redis:
      host: localhost
      port: 6379

jwt:
  secret: your-secure-jwt-secret
  expiration: 86400000
```

For production environments, sensitive values should be supplied through environment variables instead of being committed to the repository.

Example:

```yaml
spring:
  datasource:
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}

jwt:
  secret: ${JWT_SECRET}
```

---

## Swagger Documentation

After starting the application, Swagger UI can be accessed at:

```text
http://localhost:8080/swagger-ui/index.html
```

The OpenAPI specification is available at:

```text
http://localhost:8080/v3/api-docs
```

To test protected endpoints through Swagger:

1. Call the login endpoint.
2. Copy the generated JWT.
3. Click the **Authorize** button.
4. Enter the token using the following format:

```text
Bearer your-jwt-token
```

---

## Redis Token Blacklist

When a user logs out, the active JWT is stored in Redis:

```text
JWT token → blacklisted
```

The Redis entry uses a TTL equal to the token's remaining lifetime.

This ensures that:

* Logged-out tokens cannot be reused.
* Expired blacklist records are removed automatically.
* Redis does not permanently store old tokens.
* JWT authentication remains scalable and mostly stateless.

---

## Author

**Elif Akyol**

* GitHub: [@Elifakyol1020](https://github.com/Elifakyol1020)

---

<div align="center">

Developed with Spring Boot, JWT, PostgreSQL and Redis.

</div>
