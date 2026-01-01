# Inter-Service Security Implementation

## Overview
Inter-service communication is now secured using JWT tokens. Services can authenticate with each other using either:
1. **User JWT tokens** (propagated from original request)
2. **Service-to-service tokens** (generated automatically)

## Architecture

### Components

1. **FeignAuthInterceptor** (`services/common`)
   - Automatically adds JWT tokens to all Feign client requests
   - Priority order:
     1. User JWT token from request context (if available)
     2. User JWT token from SecurityContext
     3. Service-to-service token (auto-generated)

2. **ServiceTokenGenerator** (`services/common`)
   - Generates short-lived service tokens (1 hour)
   - Tokens include service name and type="SERVICE"
   - Uses same JWT secret as user tokens

3. **ServiceAuthFilter** (`services/common`)
   - Validates service-to-service tokens
   - Checks for `X-Service-Request: true` header
   - Verifies token type is "SERVICE"
   - Grants "SERVICE" authority

## How It Works

### Scenario 1: User-Initiated Request
```
User → API Gateway → Course Service → User Service
       (JWT)         (JWT propagated)  (JWT validated)
```

1. User sends request with JWT token
2. API Gateway validates and forwards token
3. Course Service receives request with JWT
4. Course Service calls User Service via Feign
5. FeignAuthInterceptor adds JWT token to Feign request
6. User Service validates JWT token

### Scenario 2: Service-to-Service Request
```
Course Service → Notification Service
(No user context)  (Service token generated)
```

1. Course Service needs to call Notification Service
2. No user JWT token available
3. FeignAuthInterceptor generates service token
4. Service token added to request with `X-Service-Request: true`
5. Notification Service validates service token
6. Request proceeds with SERVICE authority

## Configuration

### Feign Client Configuration
Each service that uses Feign clients needs:

```java
@Configuration
public class FeignConfig {
    @Bean
    public FeignAuthInterceptor feignAuthInterceptor() {
        return new FeignAuthInterceptor();
    }
    
    @Bean
    public ServiceTokenGenerator serviceTokenGenerator() {
        return new ServiceTokenGenerator();
    }
}
```

### Security Configuration
Each service needs ServiceAuthFilter:

```java
@Configuration
@EnableWebSecurity
public class ServiceSecurityConfig {
    @Autowired
    private ServiceAuthFilter serviceAuthFilter;
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        return http
            .addFilterBefore(serviceAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }
}
```

## Token Types

### User Tokens
- **Type**: User authentication
- **Claims**: email, role, userId, tenantId
- **Expiration**: 24 hours
- **Usage**: User-initiated requests

### Service Tokens
- **Type**: SERVICE
- **Claims**: service name, type="SERVICE", scope="INTER_SERVICE"
- **Expiration**: 1 hour
- **Usage**: Service-to-service communication
- **Authority**: "SERVICE"

## Security Features

✅ **Token Propagation**: User JWT tokens automatically propagated
✅ **Service Tokens**: Auto-generated for service-to-service calls
✅ **Token Validation**: All tokens validated at receiving service
✅ **Type Checking**: Service tokens verified as type "SERVICE"
✅ **Short Expiration**: Service tokens expire in 1 hour
✅ **Header Identification**: `X-Service-Request` header identifies service calls

## Headers

### Request Headers (Added by FeignAuthInterceptor)
- `Authorization: Bearer <token>` - JWT token (user or service)
- `X-Service-Request: true/false` - Indicates if service-to-service call
- `X-Source-Service: <service-name>` - Source service name

### Response Headers
- Standard HTTP headers
- No additional security headers needed

## Service-to-Service Communication Flow

```
┌─────────────┐
│   Service   │
│      A      │
└──────┬──────┘
       │
       │ Feign Client Call
       │
       ▼
┌─────────────────────┐
│ FeignAuthInterceptor│
│  - Check for user    │
│    JWT token         │
│  - If not found,     │
│    generate service  │
│    token             │
└──────┬──────────────┘
       │
       │ HTTP Request
       │ + Authorization
       │ + X-Service-Request
       │
       ▼
┌─────────────┐
│   Service   │
│      B      │
└──────┬──────┘
       │
       │ ServiceAuthFilter
       │  - Validate token
       │  - Check type
       │  - Set authority
       │
       ▼
┌─────────────┐
│  Endpoint   │
└─────────────┘
```

## Implementation Status

### ✅ Completed
- FeignAuthInterceptor for automatic token injection
- ServiceTokenGenerator for service tokens
- ServiceAuthFilter for service token validation
- Course Service security configuration updated

### 🔄 In Progress
- Update all services to use ServiceAuthFilter
- Configure Feign clients in all services
- Test inter-service communication

### 📋 To Do
- Add service token rotation
- Implement service token revocation
- Add service-to-service rate limiting
- Create service registry for authorized services

## Testing

### Test User Token Propagation
```bash
# 1. Login to get user token
TOKEN=$(curl -X POST http://localhost/users/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"password"}' \
  | jq -r '.accessToken')

# 2. Call Course Service (which calls User Service)
curl http://localhost/api/courses \
  -H "Authorization: Bearer $TOKEN"

# User Service should receive the same JWT token
```

### Test Service Token Generation
```bash
# Call a service endpoint that triggers inter-service call
# without user authentication (internal service call)
# Service token should be auto-generated
```

## Best Practices

1. **Always use Feign clients** for inter-service communication
2. **Don't manually add tokens** - let FeignAuthInterceptor handle it
3. **Validate service tokens** at receiving service
4. **Use short expiration** for service tokens (1 hour)
5. **Monitor service-to-service calls** for security
6. **Log all inter-service requests** for audit

## Security Considerations

⚠️ **Service tokens use same secret as user tokens**
- Consider separate secret for service tokens in production
- Or use mutual TLS (mTLS) for service-to-service communication

⚠️ **Service tokens are short-lived (1 hour)**
- Reduces risk if token is compromised
- Services need to regenerate tokens periodically

⚠️ **No token revocation for service tokens**
- Consider implementing token blacklist
- Or use shorter expiration times

## Future Enhancements

1. **Mutual TLS (mTLS)**
   - Certificate-based authentication
   - More secure than JWT for service-to-service

2. **Service Registry**
   - Whitelist of authorized services
   - Service-to-service permissions

3. **Token Rotation**
   - Automatic service token rotation
   - Reduced attack window

4. **Service API Keys**
   - Alternative to JWT tokens
   - Easier to revoke

