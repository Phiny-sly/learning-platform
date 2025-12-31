# Learning Platform - Microservices Architecture

A comprehensive learning management system built with Spring Boot microservices architecture.

## Architecture Overview

This platform consists of the following microservices:

### Core Services

1. **API Gateway** - Single entry point for all client requests
2. **Discovery Server (Eureka)** - Service discovery and registration
3. **User Management Service** - User authentication, authorization, and profile management
4. **Course Service** - Course creation, management, enrollment, ratings, and categories
5. **Content Management Service** - Multimedia content management (videos, images, PDFs, etc.)
6. **Tenant Service** - Multi-tenant organization management
7. **Notification Service** - Email, SMS, and push notifications
8. **Progress Service** - Student progress tracking and assessments

## Technology Stack

- **Java 17**
- **Spring Boot 3.1.x**
- **Spring Cloud Gateway** - API Gateway
- **Netflix Eureka** - Service Discovery
- **PostgreSQL** - Database
- **JWT** - Authentication
- **Spring Security** - Security framework
- **Lombok** - Boilerplate reduction
- **MapStruct** - Object mapping
- **ModelMapper** - DTO mapping

## Getting Started

### Prerequisites

- Java 17 or higher
- PostgreSQL 12 or higher
- Gradle 7.x or higher

### Database Setup

1. Create PostgreSQL databases for each service:
   ```sql
   CREATE DATABASE ttlearn;
   ```

2. Update database credentials in each service's `application.properties`

### Running the Services

1. **Start Discovery Server** (must be started first):
   ```bash
   cd services/discovery-server
   ./gradlew bootRun
   ```
   Access Eureka Dashboard: http://localhost:8761

2. **Start API Gateway**:
   ```bash
   cd services/api-gateway
   ./gradlew bootRun
   ```
   API Gateway runs on port 80 (configurable via `API_PORT`)

3. **Start Other Services**:
   ```bash
   # User Management
   cd services/user-management
   ./gradlew bootRun

   # Course Service
   cd services/course-service
   ./gradlew bootRun

   # Content Management
   cd services/content-management
   ./gradlew bootRun

   # Tenant Service
   cd services/tenant-service
   ./gradlew bootRun

   # Notification Service
   cd services/notification-service
   ./gradlew bootRun

   # Progress Service
   cd services/progress-service
   ./gradlew bootRun
   ```

### Environment Variables

Set the following environment variables or update `application.properties`:

- `DB_URL` - Database connection URL
- `DB_USERNAME` - Database username
- `DB_PASSWORD` - Database password
- `DISCOVERY_SERVER_PORT` - Eureka server port (default: 8761)
- `DISCOVERY_SERVER_USERNAME` - Eureka username (default: eureka)
- `DISCOVERY_SERVER_PASSWORD` - Eureka password (default: password)
- `API_PORT` - API Gateway port (default: 80)
- `MAIL_HOST` - SMTP server host
- `MAIL_USERNAME` - Email username
- `MAIL_PASSWORD` - Email password
- `AWS_ACCESS_KEY` - AWS access key (for S3)
- `AWS_SECRET_KEY` - AWS secret key
- `AWS_REGION` - AWS region
- `AWS_BUCKET_NAME` - S3 bucket name

## API Endpoints

### User Management Service

- `POST /users/create` - Create new user
- `POST /users/login` - User login (returns JWT token)
- `GET /users/details/{id}` - Get user details
- `GET /users` - List all users (Admin only)
- `PUT /users/details-change/{id}` - Update user profile
- `PUT /users/role-change` - Update user role (Admin only)
- `POST /users/password-reset/request` - Request password reset
- `POST /users/password-reset` - Reset password with code
- `DELETE /users/{id}` - Delete user (Admin only)

### Course Service

- `POST /api/courses` - Create course
- `GET /api/courses` - List courses (with pagination, search, filtering)
- `GET /api/courses/{id}` - Get course details
- `PATCH /api/courses/{id}` - Update course
- `DELETE /api/courses/{id}` - Delete course
- `PUT /api/courses/{id}/tags` - Add tags to course
- `DELETE /api/courses/{id}/tags` - Remove tags from course

- `POST /api/enrollments?course={courseId}` - Enroll in course
- `GET /api/enrollments` - List enrollments
- `GET /api/enrollments/{id}` - Get enrollment details
- `PATCH /api/enrollments/{id}` - Update enrollment
- `DELETE /api/enrollments/{id}` - Cancel enrollment

- `GET /api/categories` - List categories
- `POST /api/categories` - Create category
- `GET /api/categories/{id}` - Get category details
- `PATCH /api/categories/{id}` - Update category
- `DELETE /api/categories/{id}` - Delete category

- `GET /api/tags` - List tags
- `POST /api/tags` - Create tag
- `GET /api/tags/{id}` - Get tag details
- `PATCH /api/tags/{id}` - Update tag
- `DELETE /api/tags/{id}` - Delete tag

- `GET /api/topics` - List topics
- `POST /api/topics` - Create topic
- `GET /api/topics/{id}` - Get topic details
- `PATCH /api/topics/{id}` - Update topic
- `DELETE /api/topics/{id}` - Delete topic

- `GET /api/ratings` - List ratings
- `POST /api/ratings` - Create rating
- `GET /api/ratings/{id}` - Get rating details
- `PATCH /api/ratings/{id}` - Update rating
- `DELETE /api/ratings/{id}` - Delete rating

### Content Management Service

- `POST /api/multimedia` - Upload multimedia content
- `GET /api/multimedia` - List all multimedia
- `GET /api/multimedia/{id}` - Get multimedia details
- `GET /api/multimedia/course/{courseId}` - Get multimedia by course
- `DELETE /api/multimedia/{id}` - Delete multimedia

### Tenant Service

- `POST /api/tenants` - Create tenant
- `GET /api/tenants` - List tenants
- `GET /api/tenants/{id}` - Get tenant details
- `PATCH /api/tenants/{id}` - Update tenant
- `DELETE /api/tenants/{id}` - Delete tenant

### Notification Service

- `POST /api/notifications` - Create notification
- `GET /api/notifications/user/{userId}` - Get user notifications
- `GET /api/notifications/{id}` - Get notification details
- `POST /api/notifications/process-pending` - Process pending notifications

### Progress Service

- `PUT /api/progress/course/{courseId}/student/{studentId}/lesson` - Update lesson progress
- `GET /api/progress/course/{courseId}/student/{studentId}` - Get course progress

## Authentication

Most endpoints require JWT authentication. Include the token in the Authorization header:

```
Authorization: Bearer <your-jwt-token>
```

To get a token, use the login endpoint:
```bash
POST /users/login
{
  "email": "user@example.com",
  "password": "password"
}
```

## User Roles

- **REGULAR_USER** - Standard student user
- **INSTRUCTOR** - Course instructor
- **ADMIN** - System administrator

## Features

### User Management
- User registration and authentication
- JWT-based security
- Password reset functionality
- Role-based access control
- User profile management

### Course Management
- Course creation and management
- Category and tag organization
- Course enrollment
- Ratings and reviews
- Topic management

### Content Management
- Multimedia upload (videos, images, PDFs, etc.)
- AWS S3 integration for file storage
- Content organization by course and lesson

### Progress Tracking
- Lesson completion tracking
- Course progress monitoring
- Assessment management
- Time spent tracking

### Notifications
- Email notifications
- SMS notifications (integration ready)
- Push notifications (integration ready)
- In-app notifications

### Multi-tenancy
- Tenant management
- Organization-level isolation

## Development

### Building the Project

```bash
./gradlew build
```

### Running Tests

```bash
./gradlew test
```

## Project Structure

```
learning-platform/
├── services/
│   ├── api-gateway/
│   ├── discovery-server/
│   ├── user-management/
│   ├── course-service/
│   ├── content-management/
│   ├── tenant-service/
│   ├── notification-service/
│   ├── progress-service/
│   └── common/
├── build.gradle
├── settings.gradle
└── README.md
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Submit a pull request

## License

This project is licensed under the MIT License.

## Support

For issues and questions, please open an issue on the repository.

