# 🔐 Spring Boot JWT Authentication API
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
