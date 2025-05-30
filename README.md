# Spring Boot Keycloak Authorization Code Demo

**⚠️ IMPORTANT DISCLAIMER: This is a demonstration project designed to showcase Keycloak authentication with Authorization Code flow in Spring Boot. This code is NOT production-ready and should NOT be used as-is in production environments.**

## Overview

This Spring Boot application demonstrates how to integrate Keycloak authentication using the Authorization Code flow. The project serves as a backend API that validates JWT tokens issued by Keycloak and provides protected endpoints for authenticated users.

## Features

- **Authorization Code Flow**: Implements OAuth 2.0 Authorization Code flow for server-side applications
- **JWT Token Validation**: Validates Keycloak-issued JWT tokens
- **Resource Server Configuration**: Acts as an OAuth 2.0 resource server
- **Protected Endpoints**: Demonstrates route protection based on authentication
- **CORS Configuration**: Configured for frontend integration
- **Debug Logging**: Extensive logging for development and troubleshooting

## Quick Start

### Prerequisites

- Java 21 or higher (you can use java 17 by updating the java version in the pom.xml)
- Maven 3.6+
- A running Keycloak instance (version 12+ recommended)
- Configured Keycloak realm and client

### Installation

1. Clone the repository:
```bash
git clone <repository-url>
cd spring-boot-keycloak-demo
```

2. **Configure your Keycloak settings** by editing `src/main/resources/application.properties`:

```properties
spring.application.name=auth-code-demo

server.port=8080

# Keycloak configuration - UPDATE THESE VALUES
keycloak.auth-server-url=https://your-keycloak-domain
keycloak.realm=your-keycloak-realm
keycloak.resource=your-keycloak-client-id
keycloak.credentials.secret=your-keycloak-client-secret
keycloak.public-client=false

# For Keycloak 12+ (Current versions), use these endpoint patterns:
# Token endpoint
keycloak.token-endpoint=${keycloak.auth-server-url}/realms/${keycloak.realm}/protocol/openid-connect/token

# Spring Security OAuth2 Resource Server properties
spring.security.oauth2.resourceserver.jwt.jwk-set-uri=${keycloak.auth-server-url}/realms/${keycloak.realm}/protocol/openid-connect/certs
spring.security.oauth2.resourceserver.jwt.issuer-uri=${keycloak.auth-server-url}/realms/${keycloak.realm}

# SSL and security settings (DEVELOPMENT ONLY)
server.ssl.enabled=false
spring.webflux.client.ssl-context.enable-hostname-verification=false

# Debug logging (DEVELOPMENT ONLY)
logging.level.org.springframework.security=DEBUG
logging.level.org.springframework.web=DEBUG
logging.level.org.springframework.web.client.RestTemplate=DEBUG
logging.level.org.springframework.http.client=DEBUG
```

3. Build and run the application:
```bash
mvn clean install
mvn spring-boot:run
```

4. The API will be available at `http://localhost:8080`

## Keycloak Version Compatibility

### Current Keycloak Versions (12+)

For **Keycloak 12 and newer** (current versions), use these endpoint patterns:

```properties
# Authorization endpoint
${keycloak.auth-server-url}/realms/${keycloak.realm}/protocol/openid-connect/auth

# Token endpoint  
${keycloak.auth-server-url}/realms/${keycloak.realm}/protocol/openid-connect/token

# JWK Set URI
${keycloak.auth-server-url}/realms/${keycloak.realm}/protocol/openid-connect/certs

# Issuer URI
${keycloak.auth-server-url}/realms/${keycloak.realm}

# User Info endpoint
${keycloak.auth-server-url}/realms/${keycloak.realm}/protocol/openid-connect/userinfo

# Logout endpoint
${keycloak.auth-server-url}/realms/${keycloak.realm}/protocol/openid-connect/logout
```

### Legacy Keycloak 11 and Earlier

For **Keycloak 11 and earlier** (legacy versions), the endpoints were:

```properties
# Authorization endpoint (legacy)
${keycloak.auth-server-url}/auth/realms/${keycloak.realm}/protocol/openid-connect/auth

# Token endpoint (legacy)
${keycloak.auth-server-url}/auth/realms/${keycloak.realm}/protocol/openid-connect/token

# JWK Set URI (legacy)
${keycloak.auth-server-url}/auth/realms/${keycloak.realm}/protocol/openid-connect/certs
```

**Note**: The main difference is the removal of `/auth` from the URL path in Keycloak 12+.

## Keycloak Client Configuration

Your Keycloak client should be configured with the following settings:

- **Client Type**: Confidential (since `public-client=false`)
- **Client Authentication**: ON
- **Authorization**: ON
- **Standard Flow Enabled**: ON
- **Valid Redirect URIs**: `http://localhost:3000/*` (adjust for your frontend)
- **Web Origins**: `http://localhost:3000` (adjust for your frontend)
- **Access Type**: confidential

## API Endpoints

The application provides the following endpoints:

- `GET /api/public` - Public endpoint (no authentication required)
- `GET /api/protected` - Protected endpoint (requires valid JWT token)
- `GET /api/user` - Returns user information from JWT token
- `GET /api/admin` - Admin-only endpoint (requires admin role)

## Frontend Integration

A complete Angular frontend for this demo is available at:
**https://github.com/Mzed-man/keycloak-auth-code-frontend**

The frontend demonstrates:
- Authorization Code flow initiation
- Token handling and storage
- API calls with JWT tokens
- User profile display

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/example/authcodedemo/
│   │       ├── config/          # Security configuration
│   │       ├── controller/      # REST controllers
│   │       ├── service/         # Business logic
│   │       └── AuthCodeDemoApplication.java
│   └── resources/
│       └── application.properties  # Configuration file (EDIT THIS)
```

## How It Works

1. **Client Registration**: The Spring Boot app is registered as a confidential client in Keycloak
2. **Token Validation**: Incoming requests with JWT tokens are validated against Keycloak's JWK set
3. **Resource Protection**: Spring Security protects endpoints based on authentication and roles
4. **CORS Support**: Configured to work with frontend applications
5. **Debug Logging**: Extensive logging for development and troubleshooting

## Production Considerations

**🚨 This demo project lacks several critical production features:**

### Security Issues in This Demo

- **SSL Disabled**: `server.ssl.enabled=false` - NEVER do this in production
- **Hostname Verification Disabled**: Security vulnerability
- **Debug Logging Enabled**: Exposes sensitive information in logs
- **Hardcoded Configuration**: Sensitive values should use environment variables
- **No Rate Limiting**: Vulnerable to DoS attacks
- **No Input Validation**: Missing comprehensive validation
- **No Monitoring**: No health checks or metrics

### Recommended Production Enhancements

1. **Environment-Based Configuration**:
   ```properties
   # Use environment variables instead of hardcoded values
   keycloak.auth-server-url=${KEYCLOAK_URL}
   keycloak.realm=${KEYCLOAK_REALM}
   keycloak.resource=${KEYCLOAK_CLIENT_ID}
   keycloak.credentials.secret=${KEYCLOAK_CLIENT_SECRET}
   ```

2. **SSL/TLS Configuration**:
   ```properties
   # Enable SSL in production
   server.ssl.enabled=true
   server.ssl.key-store=classpath:keystore.p12
   server.ssl.key-store-password=${SSL_KEYSTORE_PASSWORD}
   server.ssl.key-store-type=PKCS12
   ```

3. **Security Headers**: Implement proper security headers:
   - `Strict-Transport-Security`
   - `X-Frame-Options`
   - `X-Content-Type-Options`
   - `Content-Security-Policy`

4. **Production Logging**:
   ```properties
   # Production logging levels
   logging.level.org.springframework.security=WARN
   logging.level.org.springframework.web=WARN
   logging.level.org.springframework.web.client.RestTemplate=WARN
   ```

5. **Additional Security Measures**:
   - Implement rate limiting with Spring Cloud Gateway or similar
   - Add comprehensive input validation
   - Configure proper CORS policies
   - Implement health checks and monitoring
   - Add request/response auditing
   - Use connection pooling for database connections
   - Implement proper exception handling

6. **Deployment Considerations**:
   - Use Docker containers with security best practices
   - Configure reverse proxy (Nginx/Apache) with proper security headers
   - Implement blue-green or rolling deployments
   - Set up proper monitoring and alerting
   - Use secrets management systems (Vault, AWS Secrets Manager, etc.)

## Testing

To test the application:

1. Start your Keycloak instance
2. Configure the realm and client as described above
3. Run the Spring Boot application
4. Use the provided frontend or test with curl:

```bash
# Public endpoint
curl http://localhost:8080/api/public

# Protected endpoint (requires Authorization header with JWT token)
curl -H "Authorization: Bearer YOUR_JWT_TOKEN" http://localhost:8080/api/protected
```

## Troubleshooting

### Common Issues

1. **Token Validation Failures**: Check JWK Set URI and issuer URI configuration
2. **CORS Errors**: Verify CORS configuration matches your frontend URL
3. **SSL Errors**: Ensure SSL settings match your Keycloak configuration
4. **Connection Errors**: Verify Keycloak is running and accessible

### Debug Information

The application includes extensive debug logging. Check the console output for detailed information about:
- Token validation process
- Security filter chain execution
- HTTP requests and responses
- OAuth2 resource server configuration

## Dependencies

Key dependencies used in this project:

- Spring Boot Starter Web
- Spring Boot Starter Security
- Spring Security OAuth2 Resource Server
- Spring Security OAuth2 Jose (for JWT handling)

## License

This project is for demonstration purposes only. Use at your own risk.

## Support

This is a demo project and is not actively maintained for production use. For production Keycloak implementations with Spring Boot, consult the official Spring Security and Keycloak documentation.

## Related Projects

- **Frontend Demo**: https://github.com/Mzed-man/keycloak-auth-code-frontend
- **Spring Security OAuth2**: https://spring.io/projects/spring-security-oauth
- **Keycloak Documentation**: https://www.keycloak.org/documentation

---

**Remember: This is a learning tool, not a production template. Always follow security best practices and conduct thorough security reviews before deploying authentication systems to production.**
