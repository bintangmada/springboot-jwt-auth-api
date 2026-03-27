# 🔐 Spring Boot JWT Authentication API

[![Java CI with Maven](https://github.com/bintangmada/springboot-jwt-auth-api/actions/workflows/ci.yml/badge.svg)](https://github.com/bintangmada/springboot-jwt-auth-api/actions/workflows/ci.yml)

Production-Ready Authentication & Authorization System  
Built with Spring Security, JWT, Refresh Token, RBAC, and Soft Delete Strategy

---

# 🚀 Overview

This project is a secure REST API for authentication and authorization using:

- JWT Access Token
- Refresh Token with HttpOnly Cookie
- Role-Based Access Control (RBAC)
- Permission-Based Authorization
- Soft Delete with Audit Tracking
- Global Exception Handling
- Layered Architecture

Designed to reflect real-world backend architecture used in enterprise applications.

---

# ✨ Features

✅ User Registration & Login  
✅ JWT Access Token Authentication  
✅ Refresh Token Rotation Strategy  
✅ HttpOnly Secure Cookie Storage  
✅ Role Management  
✅ Permission Management  
✅ Assign Role to User  
✅ Assign Permission to Role  
✅ Assign Direct Permission to User  
✅ Soft Delete with Audit Trail  
✅ Global Error Handling  
✅ Production-ready Security Flow

---

# 🧠 Authentication Flow

## 🔐 Login Process

1. User submits email and password
2. Spring Security authenticates credentials
3. Server generates:
    - Access Token (JWT)
    - Refresh Token (stored in database)
4. Refresh token is sent via HttpOnly Cookie
5. Access token is returned in response body
6. Client stores access token in memory

---

## 🔁 Refresh Token Process

1. Client calls refresh endpoint
2. Server extracts refresh token from cookie
3. Token is validated in database
4. If valid → new access token is generated

---

## 🚪 Logout Process

1. Server deletes refresh token from database
2. Server clears refresh token cookie

---

# 🛡️ Authorization Model (RBAC)

## 👤 User
A user can have:
- Multiple roles
- Direct permissions

## 🧩 Role
A role contains multiple permissions.

## 🔐 Permission
Permission controls access to API endpoints.

---

## 🔗 Final User Authorities

Final permissions are calculated from:

Role Permissions
+ Direct User Permissions  
= Effective Authorities

Permissions are loaded during authentication in CustomUserDetailsService.

---

# 🏗️ Project Structure

```
com.bintang.jwt.auth
│
├── config
├── controller
├── dto
├── entity
├── exception
├── repository
├── security
│   ├── jwt
│   ├── oauth2
│   └── user
├── service
└── util
```

---

# 🔑 Token Strategy

## Access Token
- Short lifetime
- Used for API authorization
- Sent via Authorization header

Example:
```
Authorization: Bearer <access_token>
```

## Refresh Token
- Long lifetime
- Stored in database
- Sent via HttpOnly Cookie
- Not accessible via JavaScript

---

# 🍪 Cookie Security

Refresh token cookie configuration:

- HttpOnly = true
- Secure = true (enable in HTTPS)
- SameSite = Strict
- Path = /api/auth

Purpose:
- Prevent XSS attacks
- Prevent token theft
- Restrict token access to backend only

---

# 🧬 Database Model

Main tables:

```
users
roles
permissions

user_roles
role_permissions
user_permissions
refresh_tokens
```

User does NOT need direct permissions at registration.  
Permissions are automatically inherited from assigned roles.

---

# 🧹 Soft Delete Strategy

All entities include:

- is_deleted
- deleted_at
- deleted_by
- status

Entities are automatically filtered using:

```java
@Where(clause = "is_deleted = false")
```

Benefits:
- Data recovery capability
- Audit history tracking
- Compliance friendly design

---

# ⚠️ Global Error Handling

Custom exceptions:

- ResourceNotFoundException
- ConflictException
- BadRequestException
- UnauthorizedException

Standard error response:

```json
{
  "timestamp": "2025-01-01T00:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Error message",
  "path": "/api/example"
}
```

---

# 🧾 API Endpoints

## Authentication

### Register
POST /api/auth/register

### Login
POST /api/auth/login

Response:
```json
{
  "accessToken": "jwt-token"
}
```

Refresh token is automatically stored in cookie.

---

### Refresh Access Token
POST /api/auth/refresh-token

---

### Logout
POST /api/auth/logout

---

# ⚙️ Technology Stack

- Java 17+
- Spring Boot
- Spring Security
- Spring Data JPA
- JWT
- Hibernate
- Lombok
- MySQL / PostgreSQL

---

# ▶️ Running the Project

## Option A: Using Docker (Recommended for quick testing)

You don't need to install Java or PostgreSQL locally to test this API.

1. Install Docker & Docker Compose
2. Run the following command in the project root:
```bash
docker-compose up -d --build
```
3. The API will be available at `http://localhost:8080`

> **Note:** The database will be automatically seeded with a default Admin account on startup:
> - **Email:** `admin@example.com`
> - **Password:** `admin123`

---

## Option B: Manual Setup (For Development)

### 1. Clone repository
```bash
git clone <repository-url>
```

### 2. Configure PostgreSQL database in `application.properties`

### 3. Run application
```bash
mvn spring-boot:run
```

---

# 💼 Portfolio Highlights

This project demonstrates:

- Enterprise authentication architecture
- JWT + Refresh Token security strategy
- RBAC implementation
- Secure REST API design
- Spring Security expertise

---

# 👨‍💻 Author

Bintang Mada  
Backend Developer — Java & Spring Boot