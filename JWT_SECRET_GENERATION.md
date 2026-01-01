# JWT Secret Generation Guide

## Overview
A strong JWT secret is critical for the security of your application. This guide shows you how to generate a secure secret key.

## Why a Strong Secret Matters
- Weak secrets can be brute-forced
- Compromised secrets allow attackers to forge tokens
- Production systems require cryptographically secure secrets

## Generation Methods

### Method 1: Using OpenSSL (Recommended)
```bash
# Generate a 256-bit (32-byte) random key and encode in Base64
openssl rand -base64 32
```

### Method 2: Using Java
```java
import java.security.SecureRandom;
import java.util.Base64;

SecureRandom random = new SecureRandom();
byte[] bytes = new byte[32];
random.nextBytes(bytes);
String secret = Base64.getEncoder().encodeToString(bytes);
System.out.println(secret);
```

### Method 3: Using Online Tools (Development Only)
⚠️ **WARNING**: Only use for development/testing. Never use online tools for production secrets.

## Configuration

### Development
In `application.properties`:
```properties
jwt.secret=your-generated-secret-here
```

### Production
Use environment variables:
```bash
export JWT_SECRET="your-generated-secret-here"
```

Or in Docker/Kubernetes:
```yaml
env:
  - name: JWT_SECRET
    valueFrom:
      secretKeyRef:
        name: jwt-secret
        key: secret
```

## Secret Requirements

1. **Length**: Minimum 32 bytes (256 bits) when Base64 encoded
2. **Randomness**: Use cryptographically secure random number generator
3. **Storage**: Store securely, never commit to version control
4. **Rotation**: Rotate secrets periodically (every 90 days recommended)

## Best Practices

1. ✅ Use different secrets for different environments (dev, staging, prod)
2. ✅ Store secrets in environment variables or secret management systems
3. ✅ Rotate secrets regularly
4. ✅ Never log or expose secrets
5. ✅ Use secret management tools (AWS Secrets Manager, HashiCorp Vault, etc.)

## Example Secret Format
```
jwt.secret=K8vJ2mN5pQ7sT9uW1xY3zA5bC7dE9fG1hI3jK5lM7nO9pQ1rS3tU5vW7xY9z
```

## Verification

To verify your secret is properly configured:
1. Start the application
2. Attempt to login
3. If login succeeds, the secret is working
4. If you get "Invalid token" errors, check the secret configuration

## Security Checklist

- [ ] Secret is at least 32 bytes
- [ ] Secret is randomly generated
- [ ] Secret is stored securely (not in code)
- [ ] Different secrets for each environment
- [ ] Secret rotation plan in place
- [ ] Access to secrets is restricted

