# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

DigiStock is a digital livestock management platform for Zimbabwe that replaces paper livestock cards with biometric identification, GPS tracking, and real-time verification to combat livestock rustling. The system uses a hierarchical tag coding system inspired by Mumbai's Dabbawalla sorting system.

**Core Domain**: Livestock registration → Police clearance → Movement permits → Checkpoint verification

## Commands

### Infrastructure
```bash
# Start PostgreSQL, MinIO, and Mailpit services
docker-compose up -d

# Stop services
docker-compose down

# View service logs
docker-compose logs -f [postgres|minio|mailpit]

# Check service health
docker-compose ps
```

### Backend Development
```bash
cd backend

# Build the project
./mvnw clean install

# Run application (port 8080)
./mvnw spring-boot:run

# Run tests
./mvnw test

# Run tests with coverage
./mvnw test jacoco:report

# Run specific test class
./mvnw test -Dtest=LivestockServiceTest

# Skip tests during build
./mvnw clean install -DskipTests

# Package for deployment
./mvnw clean package -DskipTests

# Format code (if configured)
./mvnw spotless:apply
```

### API Access
- **API Base**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI Spec**: http://localhost:8080/v3/api-docs
- **Health Check**: http://localhost:8080/actuator/health
- **MinIO Console**: http://localhost:9001 (minioadmin/minioadmin)
- **Mailpit Web UI**: http://localhost:8025 (email testing)

## Architecture

### Layered Architecture
```
Controller → Service → Repository → Database
     ↓          ↓          ↓
   DTOs    Business   Entities
           Logic
```

**Controllers** (`controller/`): REST API endpoints with `@RestController`, handle HTTP requests/responses using DTOs
**Services** (`service/`): Business logic with `@Service` and `@Transactional`, orchestrate repositories and integrations
**Repositories** (`repository/`): JPA data access with `@Repository`, custom queries using `@Query`
**Domain** (`domain/`): JPA entities with relationships, constraints, and audit fields
**DTOs** (`dto/`): Request validation with JSR-380, response mapping to prevent over-fetching
**Config** (`config/`): Spring configuration classes for MinIO, SourceAFIS, Security, CORS, Swagger

### Key Integrations
- **MinIO**: S3-compatible object storage for photos, fingerprints, PDFs, QR codes
- **SourceAFIS**: Biometric fingerprint matching with configurable threshold (40.0)
- **ZXing**: QR code generation for permits and clearances
- **Liquibase**: Database migrations in `src/main/resources/db/changelog/`
- **PostgreSQL**: Primary database with UUID extension enabled

### Critical Business Flows

**Livestock Registration Flow**:
1. Validate tag code format: `{PROVINCE}-{DISTRICT}-{WARD}-{SERIAL}` (e.g., HA-02-012-0234)
2. Auto-generate serial number using `TagCodeGenerator` if not provided
3. Verify owner exists, optional parent (mother/father) references
4. Store GPS coordinates for registration location
5. Upload multiple photos (front, side, brand close-up) to MinIO

**Movement Authorization Flow**:
1. **Police Clearance**: Police verify ownership and stolen status → Generate QR code → 14-day expiry
2. **Movement Permit**: AGRITEX officer validates clearance → Generate permit with route/dates → QR code → 7-day validity
3. **Checkpoint Verification**: Police scan QR → Record GPS → Update status to IN_TRANSIT → Create audit trail
4. **Completion**: Mark as COMPLETED at destination

**Tag Code System**:
- Province codes: BW, HA, MA, MC, ME, MW, MV, MN, MS, ML (10 provinces of Zimbabwe)
- Format: `^[A-Z]{2}-\d{2}-\d{3}-\d{4}$`
- Serial auto-incremented per ward to ensure uniqueness
- Parse components using `TagCodeGenerator.parseTagCode()`

## Database Schema

### Core Entities
- **Owner**: Livestock owners with biometric fingerprint templates stored in MinIO
- **Officer**: AGRITEX officers, police, vets, admins with role-based access
- **Livestock**: Animals with tag codes, parentage (mother/father), GPS, photos
- **LivestockPhoto**: Multiple photos per animal with type classification
- **PoliceClearance**: Ownership verification workflow (PENDING → APPROVED/REJECTED → EXPIRED)
- **MovementPermit**: Digital movement authorization (PENDING → APPROVED → IN_TRANSIT → COMPLETED)
- **PermitVerification**: Checkpoint scan audit trail with GPS coordinates

### Audit Pattern
All entities extend `BaseEntity` with:
- `createdAt`, `updatedAt` timestamps (auto-managed by `@EntityListeners`)
- `createdBy`, `updatedBy` auditor tracking (configured in `AuditConfig`)
- `@Version` for optimistic locking to prevent concurrent update conflicts

### Important Relationships
- Livestock → Owner (many-to-one)
- Livestock → Livestock (mother/father self-referential)
- PoliceClearance → Livestock, Owner (many-to-one)
- MovementPermit → Livestock, PoliceClearance (many-to-one)
- PermitVerification → MovementPermit (many-to-one)

## Configuration

### Environment Variables
```yaml
# Database
DATABASE_URL=jdbc:postgresql://localhost:5432/digistock
DATABASE_USERNAME=digistock
DATABASE_PASSWORD=digistock

# MinIO
MINIO_ENDPOINT=http://localhost:9000
MINIO_ACCESS_KEY=minioadmin
MINIO_SECRET_KEY=minioadmin

# SourceAFIS
SOURCEAFIS_THRESHOLD=40.0

# Server
SERVER_PORT=8080
```

### MinIO Buckets (auto-created on startup)
- `digistock-livestock-photos`: Animal photos
- `digistock-fingerprints`: Biometric templates (encrypted)
- `digistock-permits`: Movement permit PDFs
- `digistock-clearances`: Police clearance PDFs
- `digistock-qr-codes`: Generated QR codes

### Business Rules Configuration
```yaml
digistock:
  permit:
    default-validity-days: 7      # Movement permit expiry
  clearance:
    default-validity-days: 14     # Police clearance expiry
  qr:
    size: 300                       # QR code dimensions in pixels
```

## Code Patterns

### Exception Handling
Centralized `@RestControllerAdvice` in `GlobalExceptionHandler`:
- `ResourceNotFoundException` → 404 Not Found
- `DuplicateResourceException` → 409 Conflict (e.g., tag code already exists)
- `BusinessException` → 400 Bad Request (e.g., livestock is stolen, clearance expired)
- `MethodArgumentNotValidException` → 400 with field-level validation errors

### DTO Validation
Use JSR-380 annotations on request DTOs:
```java
@NotBlank(message = "Tag code is required")
@Pattern(regexp = "^[A-Z]{2}-\\d{2}-\\d{3}-\\d{4}$")
private String tagCode;

@NotNull(message = "Owner ID is required")
private UUID ownerId;
```

### Service Transactions
All service methods use `@Transactional` to ensure data consistency:
```java
@Transactional
public LivestockResponse registerLivestock(RegisterLivestockRequest request) {
    // Multiple database operations in single transaction
}
```

### MinIO File References
Files stored in MinIO use reference format: `minio://bucket/path/to/file`
- Extract bucket and object path using `MinioStorageService.parseMInioUrl()`
- Generate presigned URLs via `FileController` for secure time-limited access
- Store references in database, not file contents

### QR Code Formats
Structured data for parsing at checkpoints:
- Permits: `PERMIT:{permitNumber}:{tagCode}:{validUntil}`
- Clearances: `CLEARANCE:{clearanceNumber}:{tagCode}:{expiryDate}`
- Livestock: `LIVESTOCK:{tagCode}:{ownerId}`

## Security Notes

### Current State (MVP)
- Authentication **disabled** for development (`SecurityConfig` permits all endpoints)
- Use `X-Officer-Id` header to simulate authenticated requests
- BCrypt password encoder configured for future use

### Future Implementation
- OAuth2/JWT authentication planned
- Role-based method security with `@PreAuthorize`
- Roles: ADMIN, AGRITEX_OFFICER, POLICE_OFFICER, OWNER, VETERINARY_INSPECTOR

## Testing Strategy

### Test Structure
```
src/test/java/
  └── zw/co/digistock/
      ├── service/          # Unit tests for business logic
      ├── repository/       # Integration tests with @DataJpaTest
      └── controller/       # API tests with @WebMvcTest
```

### Test Data
- Default admin user created by Liquibase seed data (003-seed-data.xml)
- Email: `admin@digistock.zw`, Password: `Admin@123`

## Development Workflow

### Adding New Endpoints
1. Create/update DTO in `dto/request/` or `dto/response/`
2. Add business logic to service layer with `@Transactional`
3. Create controller endpoint with proper HTTP method and path
4. Update Swagger annotations for API documentation
5. Handle exceptions using existing exception types

### Database Changes
1. Create new Liquibase changeset in `db/changelog/`
2. Use semantic naming: `00X-description.xml`
3. Include rollback instructions
4. Test with `./mvnw liquibase:rollback` before committing

### File Upload Handling
1. Accept `MultipartFile` in controller
2. Validate file type and size (max 10MB per file, 15MB per request)
3. Upload to MinIO using `MinioStorageService.uploadFile()`
4. Store MinIO reference (`minio://bucket/path`) in entity
5. Generate presigned URLs via `/api/v1/files/signed-url` for retrieval

## Common Pitfalls

- **Tag code uniqueness**: Always check for duplicates before registering livestock
- **Stolen status**: Validate livestock is not stolen before issuing clearances or permits
- **Clearance expiry**: Check `expiresAt` date before issuing permits
- **Transactional boundaries**: Ensure `@Transactional` on service methods to prevent partial updates
- **MinIO references**: Store references, not file content, in database fields
- **GPS coordinates**: Use `BigDecimal` for latitude/longitude to maintain precision
- **Enum validation**: Use `@Enumerated(EnumType.STRING)` for readability in database
- **Parent references**: Validate mother/father livestock exists before setting parentage

## Troubleshooting

**Database connection errors**: Verify PostgreSQL is running via `docker-compose ps`
**MinIO upload failures**: Check MinIO console (http://localhost:9001) for bucket permissions
**Liquibase errors**: Run `./mvnw liquibase:status` to check pending changesets
**QR code generation fails**: Ensure `digistock-qr-codes` bucket exists in MinIO
**Fingerprint matching errors**: Verify fingerprint image format and SourceAFIS threshold configuration

## Documentation References

- **API Documentation**: See `API_DOCUMENTATION.md` for complete endpoint reference
- **Implementation Status**: See `IMPLEMENTATION_STATUS.md` for completed features and statistics
- **README**: See `README.md` for project overview and setup instructions
