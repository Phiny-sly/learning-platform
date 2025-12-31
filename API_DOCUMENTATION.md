# API Documentation

## Base URL
All API requests should be made to the API Gateway:
- Development: `http://localhost:80`
- Production: Configure as needed

## Authentication
Most endpoints require JWT authentication. Include the token in the Authorization header:
```
Authorization: Bearer <your-jwt-token>
```

## User Management Service

### Create User
```http
POST /users/create
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "phoneNumber": "+1234567890",
  "password": "securePassword123",
  "role": "REGULAR_USER",
  "tenantId": 1
}
```

### Login
```http
POST /users/login
Content-Type: application/json

{
  "email": "john.doe@example.com",
  "password": "securePassword123"
}
```
Returns: JWT token string

### Get User Details
```http
GET /users/details/{id}
Authorization: Bearer <token>
```

### List All Users (Admin Only)
```http
GET /users?page=0&size=10&sort=id,asc
Authorization: Bearer <token>
```

### Update User Profile
```http
PUT /users/details-change/{id}
Authorization: Bearer <token>
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Smith",
  "email": "john.smith@example.com",
  "phoneNumber": "+1234567890"
}
```

### Request Password Reset
```http
POST /users/password-reset/request
Content-Type: application/json

{
  "email": "john.doe@example.com"
}
```

### Reset Password
```http
POST /users/password-reset
Content-Type: application/json

{
  "email": "john.doe@example.com",
  "code": "reset-code-uuid",
  "newPassword": "newSecurePassword123"
}
```

## Course Service

### Create Course
```http
POST /api/courses
Authorization: Bearer <token>
Content-Type: application/json

{
  "title": "Introduction to Java",
  "description": "Learn Java programming from scratch",
  "code": "JAVA-101",
  "startDate": "2024-01-01T00:00:00",
  "endDate": "2024-12-31T23:59:59",
  "status": "UPCOMING"
}
```

### List Courses
```http
GET /api/courses?page=0&size=10&sort=created,asc&q=java&category={categoryId}
```

### Get Course Details
```http
GET /api/courses/{id}
```

### Update Course
```http
PATCH /api/courses/{id}
Authorization: Bearer <token>
Content-Type: application/json

{
  "title": "Advanced Java Programming",
  "status": "ONGOING"
}
```

### Enroll in Course
```http
POST /api/enrollments?course={courseId}
Authorization: Bearer <token>
Content-Type: application/json

{
  "studentId": "student-uuid"
}
```

### List Enrollments
```http
GET /api/enrollments?course={courseId}&page=0&size=10
```

## Content Management Service

### Upload Multimedia
```http
POST /api/multimedia/upload
Authorization: Bearer <token>
Content-Type: multipart/form-data

title: "Introduction Video"
type: "VIDEO"
file: <file>
courseId: "course-uuid" (optional)
lessonId: "lesson-uuid" (optional)
orderIndex: 1 (optional)
description: "Course introduction" (optional)
```

### Get All Multimedia
```http
GET /api/multimedia
```

### Get Multimedia by Course
```http
GET /api/multimedia/course/{courseId}
```

### Download File
```http
GET /api/multimedia/download/{fileName}
```

## Notification Service

### Create Notification
```http
POST /api/notifications
Content-Type: application/json

{
  "userId": 1,
  "title": "Course Enrollment",
  "message": "You have been enrolled in Java 101",
  "type": "EMAIL",
  "email": "user@example.com"
}
```

### Get User Notifications
```http
GET /api/notifications/user/{userId}?page=0&size=10
```

## Progress Service

### Update Lesson Progress
```http
PUT /api/progress/course/{courseId}/student/{studentId}/lesson
Authorization: Bearer <token>
Content-Type: application/json

{
  "lessonId": "lesson-uuid",
  "timeSpentMinutes": 30,
  "completed": true
}
```

### Get Course Progress
```http
GET /api/progress/course/{courseId}/student/{studentId}
```

## Tenant Service

### Create Tenant
```http
POST /api/tenants
Content-Type: application/json

{
  "name": "Acme Corporation",
  "email": "contact@acme.com",
  "phone": "+1234567890",
  "logoUrl": "https://example.com/logo.png"
}
```

### List Tenants
```http
GET /api/tenants?page=0&size=10
```

## Error Responses

All services return standard HTTP status codes:
- `200 OK` - Success
- `201 Created` - Resource created
- `204 No Content` - Success with no content
- `400 Bad Request` - Invalid request
- `401 Unauthorized` - Authentication required
- `403 Forbidden` - Insufficient permissions
- `404 Not Found` - Resource not found
- `500 Internal Server Error` - Server error

Error response format:
```json
{
  "message": "Error description",
  "timestamp": "2024-01-01T00:00:00",
  "status": 400
}
```

