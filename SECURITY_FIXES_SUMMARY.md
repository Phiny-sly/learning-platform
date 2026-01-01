# Security Context Checks - Implementation Summary

## Critical Security Issues Fixed

### ✅ Issue 1: User Details Update Without Authorization
**Problem**: Users could update any user's details by providing any user ID
**Fix**: Added security check in `updateUserDetails()` to verify current user matches userId (unless admin)

### ✅ Issue 2: User Details View Without Authorization  
**Problem**: Users could view any user's details by providing any user ID
**Fix**: Added security check in `getUserById()` and `getUserByEmail()` to verify ownership

### ✅ Issue 3: Enrollment Without Authorization
**Problem**: Users could enroll other users or view any enrollment
**Fix**: 
- Auto-set `studentId` from security context
- Validate `studentId` matches current user (unless admin/instructor)
- Filter enrollments to show only own (unless admin/instructor)

### ✅ Issue 4: Progress Update Without Authorization
**Problem**: Users could update any user's progress
**Fix**: Added security check to verify `studentId` matches current user (unless admin/instructor)

### ✅ Issue 5: Course Creation/Update Without Authorization
**Problem**: Users could create courses with any creator ID, update/delete any course
**Fix**: 
- Auto-set `createdBy` from security context
- Verify ownership before update/delete (unless admin)

## New Components

### 1. SecurityUtils (`services/common`)
Utility class for accessing current user information:
- `getCurrentUserId()` - Get current user's ID
- `getCurrentUserEmail()` - Get current user's email
- `isAdmin()` - Check if user is admin
- `canAccess(resourceUserId)` - Check if user can access resource

### 2. UserPrincipal (`services/common`)
Enhanced principal that stores:
- User ID
- Email
- Tenant ID
- Role

### 3. Updated JWT Filters
Both `JwtAuthenticationFilter` (common) and `JwtAuthFilter` (user-management) now:
- Extract user ID from JWT token
- Create `UserPrincipal` with all user info
- Store in SecurityContext for easy access

## Security Checks Added

### User Management Service
```java
// Before: No check
public UserDto updateUserDetails(long userId, ...) { ... }

// After: Security check
public UserDto updateUserDetails(long userId, ...) {
    if (!SecurityUtils.canAccess(userId)) {
        throw new AccessDeniedException("You can only update your own user details");
    }
    ...
}
```

### Enrollment Service
```java
// Before: No check
public EnrollmentDTO create(UUID courseId, CreateEnrollmentDTO payload) { ... }

// After: Auto-set and validate
public EnrollmentDTO create(UUID courseId, CreateEnrollmentDTO payload) {
    UUID currentUserUUID = new UUID(0, SecurityUtils.getCurrentUserId());
    if (!payload.getStudentId().equals(currentUserUUID) && !isAdmin()) {
        throw new AccessDeniedException("You can only enroll yourself");
    }
    ...
}
```

### Progress Service
```java
// Before: No check
public CourseProgressDTO updateLessonProgress(UUID courseId, UUID studentId, ...) { ... }

// After: Security check
public CourseProgressDTO updateLessonProgress(UUID courseId, UUID studentId, ...) {
    UUID currentUserUUID = new UUID(0, SecurityUtils.getCurrentUserId());
    if (!studentId.equals(currentUserUUID) && !isAdmin()) {
        throw new AccessDeniedException("You can only update your own progress");
    }
    ...
}
```

### Course Service
```java
// Before: Could set any creator
public CourseDTO create(CreateCourseDTO payload) {
    if (payload.getCreatedBy() == null) {
        payload.setCreatedBy(UUID.randomUUID()); // Random!
    }
    ...
}

// After: Auto-set from security context
public CourseDTO create(CreateCourseDTO payload) {
    UUID currentUserUUID = new UUID(0, SecurityUtils.getCurrentUserId());
    payload.setCreatedBy(currentUserUUID); // Current user
    ...
}
```

## New Endpoints

### User Management
- `GET /api/users/me` - Get current user's details (no ID needed)

### Progress Service
- `PUT /api/progress/course/{courseId}/lesson` - Update own progress (no studentId needed)
- `GET /api/progress/course/{courseId}` - Get own progress (no studentId needed)

## Access Control Matrix

| Operation | Owner | Admin | Instructor | Student |
|-----------|-------|-------|------------|---------|
| Update own details | ✅ | ✅ | ✅ | ✅ |
| Update other's details | ❌ | ✅ | ❌ | ❌ |
| View own details | ✅ | ✅ | ✅ | ✅ |
| View other's details | ❌ | ✅ | ❌ | ❌ |
| Enroll self | ✅ | ✅ | ✅ | ✅ |
| Enroll others | ❌ | ✅ | ✅ | ❌ |
| View own enrollments | ✅ | ✅ | ✅ | ✅ |
| View all enrollments | ❌ | ✅ | ✅ | ❌ |
| Update own progress | ✅ | ✅ | ✅ | ✅ |
| Update other's progress | ❌ | ✅ | ✅ | ❌ |
| Create course | ❌ | ✅ | ✅ | ❌ |
| Update own course | ✅ | ✅ | ✅ | ❌ |
| Update any course | ❌ | ✅ | ❌ | ❌ |

## Testing Examples

### Test: User Cannot Update Another User's Details
```bash
# Login as user1 (ID: 1)
TOKEN1=$(curl -X POST http://localhost/users/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user1@example.com","password":"password"}' \
  | jq -r '.accessToken')

# Try to update user2's details (ID: 2) - SHOULD FAIL
curl -X PUT http://localhost/users/details-change/2 \
  -H "Authorization: Bearer $TOKEN1" \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Hacked"}'
# Expected: 403 Forbidden - "You can only update your own user details"
```

### Test: User Can Only View Own Enrollments
```bash
# Login as student
TOKEN=$(curl -X POST http://localhost/users/login \
  -H "Content-Type: application/json" \
  -d '{"email":"student@example.com","password":"password"}' \
  | jq -r '.accessToken')

# View enrollments - should only show own
curl http://localhost/api/enrollments \
  -H "Authorization: Bearer $TOKEN"
# Expected: Only enrollments for logged-in student
```

### Test: User Cannot Update Another's Progress
```bash
# Try to update another student's progress - SHOULD FAIL
curl -X PUT http://localhost/api/progress/course/{courseId}/student/{otherStudentId}/lesson \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"lessonId":"...","completed":true}'
# Expected: 403 Forbidden - "You can only update your own progress"
```

## Security Improvements

✅ **All user operations** now verify ownership
✅ **All enrollment operations** now verify ownership
✅ **All progress operations** now verify ownership
✅ **All course operations** now verify ownership
✅ **Auto-set user IDs** from security context (prevents spoofing)
✅ **Filtered queries** to show only own resources
✅ **Proper error messages** for unauthorized access
✅ **Admin override** for all operations

## Files Modified

1. `services/common/src/main/java/com/phiny/labs/common/security/SecurityUtils.java` - NEW
2. `services/common/src/main/java/com/phiny/labs/common/security/UserPrincipal.java` - NEW
3. `services/common/src/main/java/com/phiny/labs/common/security/JwtAuthenticationFilter.java` - UPDATED
4. `services/user-management/src/main/java/com/phiny/labs/usermanagement/config/JwtAuthFilter.java` - UPDATED
5. `services/user-management/src/main/java/com/phiny/labs/usermanagement/service/UserServiceImpl.java` - UPDATED
6. `services/user-management/src/main/java/com/phiny/labs/usermanagement/controller/UserController.java` - UPDATED
7. `services/course-service/src/main/java/com/phiny/labs/courseservice/service/EnrollmentService.java` - UPDATED
8. `services/course-service/src/main/java/com/phiny/labs/courseservice/service/CourseService.java` - UPDATED
9. `services/course-service/src/main/java/com/phiny/labs/courseservice/repository/EnrollmentRepository.java` - UPDATED
10. `services/progress-service/src/main/java/com/phiny/labs/progressservice/service/ProgressService.java` - UPDATED
11. `services/progress-service/src/main/java/com/phiny/labs/progressservice/controller/ProgressController.java` - UPDATED

## Next Steps

1. **Add tenant isolation** - Ensure users can only access resources in their tenant
2. **Add audit logging** - Log all access attempts (successful and failed)
3. **Add resource-level permissions** - Fine-grained permissions for specific resources
4. **Standardize ID types** - Use one ID type (UUID or Long) across all services
5. **Add integration tests** - Test all security scenarios

