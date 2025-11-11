# DigiStock Backend Structure

This document describes the backend project structure and organization.

## Technology Stack

- **Framework**: Spring Boot 3.2.1
- **Language**: Java 17
- **Database**: PostgreSQL 15 (Production), H2 (Unit Tests)
- **ORM**: Spring Data JPA with Hibernate
- **Database Migration**: Liquibase
- **Security**: Spring Security with OAuth2 Resource Server (JWT)
- **Object Storage**: MinIO
- **Biometric Matching**: SourceAFIS
- **QR Code Generation**: ZXing
- **PDF Generation**: iText 7
- **Build Tool**: Maven
- **Testing**: JUnit 5, Spring Test, Mockito
- **API Documentation**: SpringDoc OpenAPI 3 (Swagger)

## Project Structure

```
digi-stock/
├── src/
│   ├── main/
│   │   ├── java/zw/co/digistock/
│   │   │   ├── DigistockApplication.java          # Main application entry point
│   │   │   │
│   │   │   ├── config/                            # Configuration classes
│   │   │   │   ├── AuditConfig.java              # JPA Auditing configuration
│   │   │   │   ├── MinioConfig.java              # MinIO object storage config
│   │   │   │   ├── OpenApiConfig.java            # Swagger/OpenAPI config
│   │   │   │   ├── SecurityConfig.java           # Spring Security config
│   │   │   │   ├── SourceAfisConfig.java         # Biometric matching config
│   │   │   │   └── WebConfig.java                # Web and CORS config
│   │   │   │
│   │   │   ├── controller/                        # REST API Controllers
│   │   │   │   ├── FileController.java           # File upload/download
│   │   │   │   ├── LivestockController.java      # Livestock management
│   │   │   │   ├── MovementPermitController.java # Movement permits
│   │   │   │   ├── OwnerController.java          # Owner management
│   │   │   │   └── PoliceClearanceController.java # Police clearances
│   │   │   │
│   │   │   ├── domain/                            # JPA Entities (Domain Model)
│   │   │   │   ├── base/
│   │   │   │   │   └── BaseEntity.java           # Base entity with common fields
│   │   │   │   ├── enums/
│   │   │   │   │   ├── ClearanceStatus.java      # Clearance status enum
│   │   │   │   │   ├── PermitStatus.java         # Permit status enum
│   │   │   │   │   └── UserRole.java             # User role enum
│   │   │   │   ├── Livestock.java                # Livestock entity
│   │   │   │   ├── LivestockPhoto.java           # Livestock photos
│   │   │   │   ├── MovementPermit.java           # Movement permit entity
│   │   │   │   ├── Officer.java                  # Officer entity
│   │   │   │   ├── Owner.java                    # Owner entity
│   │   │   │   ├── PermitVerification.java       # Permit verification log
│   │   │   │   └── PoliceClearance.java          # Police clearance entity
│   │   │   │
│   │   │   ├── dto/                               # Data Transfer Objects
│   │   │   │   ├── request/                      # Request DTOs
│   │   │   │   │   ├── CreateClearanceRequest.java
│   │   │   │   │   ├── CreatePermitRequest.java
│   │   │   │   │   ├── RegisterLivestockRequest.java
│   │   │   │   │   └── RegisterOwnerRequest.java
│   │   │   │   └── response/                     # Response DTOs
│   │   │   │       ├── ClearanceResponse.java
│   │   │   │       ├── LivestockResponse.java
│   │   │   │       ├── OwnerResponse.java
│   │   │   │       └── PermitResponse.java
│   │   │   │
│   │   │   ├── exception/                         # Exception Handling
│   │   │   │   ├── BusinessException.java        # Base business exception
│   │   │   │   ├── DuplicateResourceException.java
│   │   │   │   ├── GlobalExceptionHandler.java   # Global exception handler
│   │   │   │   └── ResourceNotFoundException.java
│   │   │   │
│   │   │   ├── mapper/                            # MapStruct Mappers
│   │   │   │   ├── LivestockMapper.java          # Livestock entity-DTO mapper
│   │   │   │   └── OwnerMapper.java              # Owner entity-DTO mapper
│   │   │   │
│   │   │   ├── repository/                        # Spring Data JPA Repositories
│   │   │   │   ├── LivestockPhotoRepository.java
│   │   │   │   ├── LivestockRepository.java
│   │   │   │   ├── MovementPermitRepository.java
│   │   │   │   ├── OfficerRepository.java
│   │   │   │   ├── OwnerRepository.java
│   │   │   │   ├── PermitVerificationRepository.java
│   │   │   │   └── PoliceClearanceRepository.java
│   │   │   │
│   │   │   ├── service/                           # Business Logic Layer
│   │   │   │   ├── biometric/
│   │   │   │   │   └── BiometricService.java     # Fingerprint matching
│   │   │   │   ├── qr/
│   │   │   │   │   └── QrCodeService.java        # QR code generation
│   │   │   │   ├── storage/
│   │   │   │   │   └── MinioStorageService.java  # File storage operations
│   │   │   │   ├── ILivestockService.java        # Livestock service interface
│   │   │   │   ├── LivestockService.java         # Livestock business logic
│   │   │   │   ├── MovementPermitService.java    # Movement permit logic
│   │   │   │   ├── OwnerService.java             # Owner management logic
│   │   │   │   ├── PoliceClearanceService.java   # Clearance logic
│   │   │   │   └── TagCodeGenerator.java         # Tag code generation
│   │   │   │
│   │   │   └── util/                              # Utility Classes
│   │   │       ├── ApiResponse.java              # Generic API response wrapper
│   │   │       ├── Constants.java                # Application constants
│   │   │       ├── DateTimeUtils.java            # Date/time utilities
│   │   │       ├── PagedResponse.java            # Pagination wrapper
│   │   │       └── ValidationUtils.java          # Validation helpers
│   │   │
│   │   └── resources/
│   │       ├── application.yml                    # Main application config
│   │       └── db/
│   │           └── changelog/                     # Liquibase migrations
│   │               ├── db.changelog-master.xml
│   │               └── changes/
│   │                   ├── 001-initial-schema.xml
│   │                   ├── 002-add-indexes.xml
│   │                   └── 003-seed-data.xml
│   │
│   └── test/
│       ├── java/zw/co/digistock/
│       │   ├── controller/                        # Controller unit tests
│       │   ├── service/                           # Service unit tests
│       │   ├── repository/                        # Repository tests
│       │   ├── integration/                       # Integration tests
│       │   └── util/                              # Utility tests
│       │
│       └── resources/
│           └── application-test.yml               # Test configuration
│
├── pom.xml                                        # Maven dependencies
├── docker-compose.yml                             # Docker services (Postgres, MinIO)
├── .gitignore
├── README.md
├── API_DOCUMENTATION.md
├── IMPLEMENTATION_STATUS.md
└── BACKEND_STRUCTURE.md                          # This file
```

## Layer Architecture

The application follows a layered architecture pattern:

```
┌─────────────────────────────────────────┐
│         REST API Layer                  │
│  (Controllers - HTTP endpoints)         │
└─────────────────────────────────────────┘
                  ↓
┌─────────────────────────────────────────┐
│       Service Layer                     │
│  (Business logic, transactions)         │
└─────────────────────────────────────────┘
                  ↓
┌─────────────────────────────────────────┐
│     Repository Layer                    │
│  (Data access, JPA repositories)        │
└─────────────────────────────────────────┘
                  ↓
┌─────────────────────────────────────────┐
│         Database                        │
│  (PostgreSQL - persistence)             │
└─────────────────────────────────────────┘
```

### Layer Responsibilities

1. **Controller Layer** (`controller/`)
   - Handle HTTP requests/responses
   - Input validation (Bean Validation)
   - Call service layer methods
   - Return DTOs (never entities)
   - API versioning (`/api/v1`)

2. **Service Layer** (`service/`)
   - Business logic implementation
   - Transaction management
   - Coordinate between repositories
   - DTO ↔ Entity mapping
   - Business validation
   - Exception handling

3. **Repository Layer** (`repository/`)
   - Data access operations
   - Spring Data JPA queries
   - Custom queries with `@Query`
   - No business logic

4. **Domain Layer** (`domain/`)
   - JPA entities
   - Relationships between entities
   - No business logic (anemic domain model)

## Key Design Patterns & Best Practices

### 1. **API Design**
- RESTful endpoints with proper HTTP verbs
- Consistent URL structure: `/api/v1/{resource}`
- Proper HTTP status codes
- JSON request/response format
- OpenAPI/Swagger documentation

### 2. **Pagination Support**
**IMPORTANT**: Current APIs return `List<DTO>` which is problematic for large datasets.

**Recommended approach**:
```java
// Service Interface
Page<LivestockResponse> getLivestockByOwner(UUID ownerId, Pageable pageable);

// Service Implementation
public Page<LivestockResponse> getLivestockByOwner(UUID ownerId, Pageable pageable) {
    Page<Livestock> page = livestockRepository.findByOwnerId(ownerId, pageable);
    return page.map(this::mapToResponse);
}

// Controller
@GetMapping("/owner/{ownerId}")
public ResponseEntity<Page<LivestockResponse>> getLivestockByOwner(
        @PathVariable UUID ownerId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(defaultValue = "createdAt,desc") String[] sort) {

    Sort.Order order = new Sort.Order(
        sort[1].equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC,
        sort[0]
    );
    Pageable pageable = PageRequest.of(page, size, Sort.by(order));

    Page<LivestockResponse> response = livestockService.getLivestockByOwner(ownerId, pageable);
    return ResponseEntity.ok(response);
}
```

**No need for custom `PagedResponse` wrapper** - Spring's `Page` already includes:
- `content`: List of items
- `totalElements`: Total records
- `totalPages`: Total pages
- `size`: Page size
- `number`: Current page number
- `first`: Is first page
- `last`: Is last page

### 3. **DTO Pattern**
- Separate DTOs for requests and responses
- Never expose entities in REST APIs
- Use MapStruct for mapping
- Validate input with Bean Validation annotations

### 4. **Service Interface Pattern**
Define interfaces for services to:
- Enable easier testing (mocking)
- Support multiple implementations
- Follow dependency inversion principle

### 5. **Exception Handling**
- Custom business exceptions
- Global exception handler with `@ControllerAdvice`
- Consistent error response format
- Appropriate HTTP status codes

### 6. **Database Best Practices**
- Use Liquibase for schema migrations
- Define indexes for frequently queried columns
- Use `@Transactional` appropriately
- Mark read-only queries as `@Transactional(readOnly = true)`
- Use connection pooling (HikariCP)

### 7. **Security**
- JWT-based authentication
- OAuth2 Resource Server
- Role-based access control
- Secure endpoints with method security

### 8. **Testing Strategy**
- **Unit Tests**: Test individual components (services, utilities)
- **Integration Tests**: Test component interactions with real database
- **Controller Tests**: Test REST endpoints with MockMvc
- Use H2 for fast unit tests, PostgreSQL (Testcontainers) for integration tests

## Configuration

### Application Properties
- `application.yml`: Main configuration
- `application-test.yml`: Test configuration
- Environment-specific profiles (dev, staging, prod)

### External Services
- **PostgreSQL**: Primary database
- **MinIO**: Object storage for photos, fingerprints, documents
- **SourceAFIS**: Biometric fingerprint matching

## API Versioning

Current version: `v1`

Base path: `/api/v1`

For future versions, organize controllers in version-specific packages:
```
controller/
├── v1/
│   ├── LivestockController.java
│   └── OwnerController.java
└── v2/
    ├── LivestockController.java
    └── OwnerController.java
```

## Building and Running

### Prerequisites
- Java 17+
- Maven 3.8+
- Docker & Docker Compose (for PostgreSQL and MinIO)

### Start Infrastructure
```bash
docker-compose up -d
```

### Build Project
```bash
mvn clean package
```

### Run Application
```bash
mvn spring-boot:run
```

### Run Tests
```bash
mvn test
```

### Access Swagger UI
```
http://localhost:8080/swagger-ui.html
```

## Future Improvements

1. **Add Pagination**: Update all list endpoints to use `Page<T>` instead of `List<T>`
2. **Add Caching**: Implement caching for frequently accessed data (Spring Cache)
3. **Add Search**: Implement full-text search with Elasticsearch or PostgreSQL full-text search
4. **Add Metrics**: Enhanced monitoring with Micrometer and Prometheus
5. **Add Rate Limiting**: Protect APIs from abuse
6. **Add API Versioning**: Support multiple API versions simultaneously
7. **Add WebSocket**: Real-time updates for permit verification
8. **Add Batch Processing**: Bulk import/export of data
9. **Add Audit Trail**: Comprehensive audit logging of all operations
10. **Add Multi-tenancy**: Support multiple organizations

## References

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Data JPA](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [MapStruct Documentation](https://mapstruct.org/documentation/stable/reference/html/)
- [Liquibase Documentation](https://docs.liquibase.com/)
- [MinIO Java SDK](https://min.io/docs/minio/linux/developers/java/minio-java.html)
- [SourceAFIS Documentation](https://sourceafis.machinezoo.com/)
