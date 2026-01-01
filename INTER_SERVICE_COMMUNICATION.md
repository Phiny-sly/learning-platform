# Inter-Service Communication

This document describes the inter-service communication patterns implemented in the learning platform.

## Overview

The platform uses **Spring Cloud OpenFeign** for declarative HTTP clients, enabling services to communicate with each other through service discovery (Eureka).

## Communication Patterns

### 1. Course Service → User Management Service

**Purpose**: Validate user existence and retrieve user details

**Feign Client**: `UserServiceClient` in `course-service`

**Endpoints Used**:
- `GET /api/users/details/{id}` - Get user by ID
- `GET /api/users/email/{email}` - Get user by email

**Usage**:
- Validates user IDs when creating courses
- Retrieves user information for notifications

### 2. Course Service → Notification Service

**Purpose**: Send notifications for course-related events

**Feign Client**: `NotificationServiceClient` in `course-service`

**Endpoints Used**:
- `POST /api/notifications` - Create notification

**Usage**:
- Sends notifications when courses are created
- Sends enrollment confirmation notifications

### 3. Progress Service → Course Service

**Purpose**: Validate course existence

**Feign Client**: `CourseServiceClient` in `progress-service`

**Endpoints Used**:
- `GET /api/courses/{id}` - Get course by ID

**Usage**:
- Validates course IDs before tracking progress
- Ensures course exists before creating progress records

### 4. Progress Service → Notification Service

**Purpose**: Send progress-related notifications

**Feign Client**: `NotificationServiceClient` in `progress-service`

**Endpoints Used**:
- `POST /api/notifications` - Create notification

**Usage**:
- Sends notifications when courses are completed
- Notifies users of progress milestones

### 5. Content Management → Course Service

**Purpose**: Validate course IDs for content linking

**Feign Client**: `CourseServiceClient` in `content-management`

**Endpoints Used**:
- `GET /api/courses/{id}` - Get course by ID

**Usage**:
- Validates course IDs when uploading multimedia content
- Ensures course exists before linking content

### 6. Notification Service → User Management Service

**Purpose**: Retrieve user details for notifications

**Feign Client**: `UserServiceClient` in `notification-service`

**Endpoints Used**:
- `GET /api/users/details/{id}` - Get user by ID

**Usage**:
- Retrieves user email and phone for sending notifications
- Gets user preferences for notification delivery

## Service Discovery

All services are registered with **Eureka** and use service names for communication:

- `user-management` - User Management Service
- `course-service` - Course Service
- `notification-service` - Notification Service
- `progress-service` - Progress Service
- `content-management` - Content Management Service
- `tenant-service` - Tenant Service

## Feign Client Configuration

Feign clients are configured with:
- **Service Discovery**: Uses Eureka to resolve service instances
- **Load Balancing**: Automatically load balances across service instances
- **Error Handling**: Gracefully handles service unavailability (optional dependencies)

## Error Handling

All Feign clients are injected as **optional dependencies** (`@Autowired(required = false)`), allowing services to:
- Continue operating if other services are unavailable
- Gracefully degrade functionality
- Log errors without breaking the main flow

## Example Usage

### Creating a Course with User Validation

```java
@Autowired(required = false)
private UserServiceClient userServiceClient;

public CourseDTO create(CreateCourseDTO payload) {
    // Validate user if client is available
    if (userServiceClient != null && payload.getCreatedBy() != null) {
        try {
            UserServiceClient.UserDto user = userServiceClient.getUserById(userId);
            // Proceed with course creation
        } catch (Exception e) {
            // Handle validation error
        }
    }
    // Create course...
}
```

### Sending Notifications

```java
@Autowired(required = false)
private NotificationServiceClient notificationServiceClient;

public void sendNotification(Long userId, String message) {
    if (notificationServiceClient != null) {
        try {
            NotificationServiceClient.CreateNotificationRequest request = 
                new NotificationServiceClient.CreateNotificationRequest();
            request.setUserId(userId);
            request.setMessage(message);
            request.setType("EMAIL");
            notificationServiceClient.createNotification(request);
        } catch (Exception e) {
            // Log error, don't fail main operation
        }
    }
}
```

## Benefits

1. **Service Decoupling**: Services communicate through well-defined interfaces
2. **Type Safety**: Feign clients provide compile-time type checking
3. **Load Balancing**: Automatic load balancing across service instances
4. **Resilience**: Optional dependencies allow graceful degradation
5. **Service Discovery**: No hardcoded URLs, services discovered automatically

## Future Enhancements

- Add circuit breakers (Resilience4j) for better fault tolerance
- Implement retry mechanisms for transient failures
- Add request/response logging for debugging
- Implement distributed tracing (Zipkin/Jaeger)
- Add rate limiting for service-to-service calls

