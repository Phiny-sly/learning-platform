# Security Implementation Guide

## Overview
Comprehensive JWT-based authentication and authorization has been implemented across all microservices.

## Components

### 1. Shared Security Module (`services/common`)
- **JwtUtil**: Utility for JWT token validation and extraction
- **JwtAuthenticationFilter**: Spring Security filter for JWT validation
- **SecurityConfig**: Base security configuration

### 2. API Gateway Security
- **JwtAuthenticationGatewayFilter**: Validates JWT tokens at the gateway level
- **GatewaySecurityConfig**: WebFlux security configuration
- All routes (except public endpoints) require valid JWT tokens

### 3. Microservice Security
Each service now has:
- Security configuration extending the common SecurityConfig
- JWT validation filter applied to all requests
- Role-based access control (RBAC) on controllers

## JWT Token Structure
Tokens now include:
- `email`: User email (subject)
- `role`: User role (ADMIN, INSTRUCTOR, STUDENT)
- `userId`: User ID
- `tenantId`: Tenant ID

## Role-Based Access Control

### Roles
- **ADMIN**: Full system access
- **INSTRUCTOR**: Can create/update courses and content
- **STUDENT**: Can view courses and enroll

### Endpoint Protection Examples

#### Course Service
- `POST /api/courses`: Requires INSTRUCTOR or ADMIN
- `GET /api/courses`: Requires authentication
- `DELETE /api/courses/{id}`: Requires ADMIN

#### Content Management
- `POST /api/multimedia/upload`: Requires INSTRUCTOR or ADMIN
- `GET /api/multimedia`: Requires authentication
- `DELETE /api/multimedia/{id}`: Requires INSTRUCTOR or ADMIN

## Configuration

### JWT Secret
Set in `application.properties`:
```properties
jwt.secret=eyJhbGciOiJIUzI1NiIsInR5cCI
```

**IMPORTANT**: Use a strong, randomly generated secret in production!

### Public Endpoints
Only these endpoints are publicly accessible:
- `POST /users/create` - User registration
- `POST /users/login` - User login
- `POST /users/password-reset/**` - Password reset

All other endpoints require authentication.

## Usage

### 1. Login to get JWT token
```bash
POST /users/login
{
  "email": "user@example.com",
  "password": "password"
}
```

Response includes JWT token in response body.

### 2. Use token in requests
```bash
GET /api/courses
Authorization: Bearer <JWT_TOKEN>
```

### 3. Gateway automatically validates tokens
The API Gateway validates all tokens before routing to services.

## Security Features

✅ JWT token validation at API Gateway
✅ JWT token validation at each microservice
✅ Role-based access control (RBAC)
✅ Stateless authentication
✅ Token expiration (24 hours)
✅ User context propagation via headers (X-User-Email, X-User-Role, X-User-Id, X-Tenant-Id)

## Next Steps

1. **Generate a strong JWT secret** for production
2. **Configure HTTPS** for all services
3. **Implement token refresh** mechanism
4. **Add rate limiting** to prevent abuse
5. **Implement audit logging** for security events
6. **Add CORS configuration** if needed for frontend

## Testing Security

### Test Unauthenticated Access
```bash
curl http://localhost/api/courses
# Should return 401 Unauthorized
```

### Test Authenticated Access
```bash
# 1. Login
TOKEN=$(curl -X POST http://localhost/users/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"password"}' | jq -r '.')

# 2. Use token
curl http://localhost/api/courses \
  -H "Authorization: Bearer $TOKEN"
# Should return 200 OK with courses
```

### Test Role-Based Access
```bash
# As STUDENT - should fail
curl -X POST http://localhost/api/courses \
  -H "Authorization: Bearer $STUDENT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"title":"New Course"}'
# Should return 403 Forbidden

# As INSTRUCTOR - should succeed
curl -X POST http://localhost/api/courses \
  -H "Authorization: Bearer $INSTRUCTOR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"title":"New Course"}'
# Should return 201 Created
```

