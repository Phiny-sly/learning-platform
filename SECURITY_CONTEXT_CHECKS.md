# Security Context Checks Implementation

## Overview
Added comprehensive security context checks to ensure users can only access and modify their own resources, unless they have admin privileges.

## Security Issues Fixed

### 1. User Management Service
**Before**: Users could update/view any user's details by providing any user ID
**After**: Users can only update/view their own details (admins can access all)

**Fixed Operations**:
- ✅ `updateUserDetails(userId, ...)` - Now checks if current user matches userId
- ✅ `getUserById(userId)` - Now checks if current user matches userId
- ✅ `getUserByEmail(email)` - Now checks if current user matches the user's ID

### 2. Enrollment Service
**Before**: Users could enroll other users or view any enrollment
**After**: Users can only enroll themselves and view their own enrollments

**Fixed Operations**:
- ✅ `create(courseId, payload)` - Auto-sets studentId to current user, validates if provided
- ✅ `read(...)` - Filters to show only own enrollments (unless admin/instructor)
- ✅ `readById(id)` - Checks if enrollment belongs to current user
- ✅ `updateById(id, ...)` - Checks if enrollment belongs to current user

### 3. Progress Service
**Before**: Users could update/view any user's progress
**After**: Users can only update/view their own progress

**Fixed Operations**:
- ✅ `updateLessonProgress(courseId, studentId, ...)` - Validates studentId matches current user
- ✅ `getCourseProgress(courseId, studentId)` - Validates studentId matches current user

### 4. Course Service
**Before**: Users could create courses with any creator ID, update/delete any course
**After**: Users can only update/delete courses they created

**Fixed Operations**:
- ✅ `create(payload)` - Auto-sets createdBy to current user
- ✅ `updateById(id, ...)` - Checks if course was created by current user
- ✅ `deleteById(id)` - Checks if course was created by current user

## Implementation Details

### SecurityUtils Class
Created utility class in `services/common` with methods:
- `getCurrentUserId()` - Get current user's ID from SecurityContext
- `getCurrentUserEmail()` - Get current user's email
- `getCurrentTenantId()` - Get current user's tenant ID
- `isAdmin()` - Check if current user is admin
- `hasAuthority(authority)` - Check if user has specific authority
- `isOwner(resourceUserId)` - Check if current user owns resource
- `canAccess(resourceUserId)` - Check if user can access (owner or admin)

### UserPrincipal Class
Enhanced authentication to store user information:
- Stores user ID, email, tenant ID, and role in SecurityContext
- Makes user information easily accessible throughout the application

### JWT Authentication Filter Update
Updated to extract and store user ID from JWT token:
- Extracts userId, tenantId, and role from token
- Creates UserPrincipal with all user information
- Stores in SecurityContext for easy access

## Access Control Rules

### User Management
- **View own details**: ✅ All authenticated users
- **View other users**: ❌ Only ADMIN
- **Update own details**: ✅ All authenticated users
- **Update other users**: ❌ Only ADMIN
- **Delete users**: ❌ Only ADMIN

### Enrollments
- **Enroll self**: ✅ All authenticated users
- **Enroll others**: ❌ Only ADMIN/INSTRUCTOR
- **View own enrollments**: ✅ All authenticated users
- **View all enrollments**: ❌ Only ADMIN/INSTRUCTOR
- **Update own enrollment**: ✅ All authenticated users
- **Update any enrollment**: ❌ Only ADMIN/INSTRUCTOR

### Progress
- **Update own progress**: ✅ All authenticated users
- **Update other's progress**: ❌ Only ADMIN/INSTRUCTOR
- **View own progress**: ✅ All authenticated users
- **View other's progress**: ❌ Only ADMIN/INSTRUCTOR

### Courses
- **Create course**: ✅ INSTRUCTOR/ADMIN (auto-sets creator)
- **Update own course**: ✅ INSTRUCTOR/ADMIN (creator)
- **Update any course**: ❌ Only ADMIN
- **Delete own course**: ✅ INSTRUCTOR/ADMIN (creator)
- **Delete any course**: ❌ Only ADMIN

## UUID/Long Conversion

Since course-service uses UUID and user-management uses Long:
- Conversion: `UUID currentUserUUID = new UUID(0, currentUserId)`
- This uses the least significant 64 bits of the UUID
- For production, consider standardizing on one ID type

## Exception Handling

New exceptions added:
- `AccessDeniedException` in user-management, course-service, and progress-service
- Thrown when user tries to access/modify resources they don't own
- Returns 403 Forbidden status

## Testing

### Test User Can Only Update Own Details
```bash
# Login as user1
TOKEN1=$(curl -X POST http://localhost/users/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user1@example.com","password":"password"}' \
  | jq -r '.accessToken')

# Try to update user2's details (should fail)
curl -X PUT http://localhost/users/details-change/2 \
  -H "Authorization: Bearer $TOKEN1" \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Hacked"}'
# Should return 403 Forbidden
```

### Test User Can Only View Own Enrollments
```bash
# Login as student
TOKEN=$(curl -X POST http://localhost/users/login \
  -H "Content-Type: application/json" \
  -d '{"email":"student@example.com","password":"password"}' \
  | jq -r '.accessToken')

# View enrollments (should only show own)
curl http://localhost/api/enrollments \
  -H "Authorization: Bearer $TOKEN"
# Should only return enrollments for logged-in student
```

### Test User Can Only Update Own Progress
```bash
# Try to update another student's progress (should fail)
curl -X PUT http://localhost/api/progress/course/{courseId}/student/{otherStudentId}/lesson \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"lessonId":"...","completed":true}'
# Should return 403 Forbidden
```

## Security Improvements

✅ **User ID Validation**: All operations now verify user identity
✅ **Resource Ownership**: Users can only access their own resources
✅ **Admin Override**: Admins can access all resources
✅ **Role-Based Filtering**: Different access levels for different roles
✅ **Automatic ID Setting**: Creator/student IDs auto-set from security context
✅ **Exception Handling**: Proper error messages for unauthorized access

## Remaining Considerations

1. **UUID/Long Standardization**: Consider using one ID type across all services
2. **Tenant Isolation**: Add tenant-based access control
3. **Audit Logging**: Log all access attempts (successful and failed)
4. **Rate Limiting**: Apply per-user rate limiting
5. **Resource-Level Permissions**: Fine-grained permissions for specific resources

