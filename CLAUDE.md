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
```

### Backend Development
```bash
# Run from root directory (mvnw is at root, not in backend/)
./mvnw clean install
./mvnw spring-boot:run
./mvnw test
./mvnw test -Dtest=LivestockServiceTest
./mvnw clean install -DskipTests
./mvnw clean package -DskipTests
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
**Config** (`config/`): Spring configuration classes for MinIO, SourceAFIS, Security, CORS, Swagger, Cache
**Security** (`security/`): JWT filter (`JwtAuthenticationFilter`) and token utilities (`JwtUtil`)
**Util** (`util/`): `ApiResponse<T>` wrapper, `PagedResponse`, `Constants`, `DateTimeUtils`

### Key Integrations
- **MinIO**: S3-compatible object storage for photos, fingerprints, PDFs, QR codes
- **SourceAFIS**: Biometric fingerprint matching with configurable threshold (40.0)
- **ZXing**: QR code generation for permits and clearances
- **Liquibase**: Database migrations in `src/main/resources/db/changelog/changes/`
- **PostgreSQL**: Primary database with UUID extension enabled
- **In-memory cache**: `CacheConfig` uses `ConcurrentMapCacheManager` for owners, livestock, clearances, permits, officers

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
- `TagCodeGenerator` province-to-code mappings (used in tag codes, not Province entity codes):
  `Bulawayo→BW, Harare→HA, Manicaland→MA, Mashonaland Central→MC, Mashonaland East→ME, Mashonaland West→MW, Masvingo→MV, Matabeleland North→MN, Matabeleland South→MS, Midlands→ML`
- Format: `^[A-Z]{2}-\d{2}-\d{3}-\d{4}$`
- Serial auto-incremented per ward using `LivestockRepository.findByTagCodePattern()`
- Extract parts using `TagCodeGenerator.extractProvinceCode()`, `extractDistrictCode()`, `extractWardCode()`, `extractSerial()`

## Database Schema

### Core Entities
- **Owner**: Livestock owners with biometric fingerprint templates stored in MinIO
- **Officer**: System users with role-based access (see Roles below)
- **Livestock**: Animals with tag codes, parentage (mother/father self-referential), GPS, photos
- **LivestockPhoto**: Multiple photos per animal with type classification
- **PoliceClearance**: Ownership verification workflow (PENDING → APPROVED/REJECTED → EXPIRED)
- **MovementPermit**: Digital movement authorization (PENDING → APPROVED → IN_TRANSIT → COMPLETED)
- **PermitVerification**: Checkpoint scan audit trail with GPS coordinates
- **Vaccination**: Animal vaccination records linked to Livestock and administering Officer
- **Province / District / Ward**: Administrative hierarchy (Province → District → Ward) with code fields

### Audit Pattern
All entities extend `BaseEntity` with `createdAt`, `updatedAt`, `createdBy`, `updatedBy` (via `@EntityListeners`), and `@Version` for optimistic locking.

### Important Relationships
- Livestock → Owner (many-to-one)
- Livestock → Livestock (mother/father self-referential)
- PoliceClearance → Livestock, Owner (many-to-one)
- MovementPermit → Livestock, PoliceClearance (many-to-one)
- PermitVerification → MovementPermit (many-to-one)
- Vaccination → Livestock, Officer (many-to-one)
- District → Province (many-to-one), Ward → District (many-to-one)

## Configuration

### Environment Variables
```bash
DATABASE_URL=jdbc:postgresql://localhost:5432/digistock
DATABASE_USERNAME=digistock
DATABASE_PASSWORD=digistock
MINIO_ENDPOINT=http://localhost:9000   # Must set explicitly; default in application.properties is port 30900
MINIO_ACCESS_KEY=minioadmin
MINIO_SECRET_KEY=minioadmin
JWT_SECRET=<hex-encoded-256-bit-key>
JWT_EXPIRATION=86400000
SERVER_PORT=8080
```

### MinIO Buckets (auto-created on startup)
`digistock-livestock-photos`, `digistock-fingerprints`, `digistock-permits`, `digistock-clearances`, `digistock-qr-codes`

### Business Rules Configuration
```yaml
digistock:
  permit.default-validity-days: 7
  clearance.default-validity-days: 14
  qr.size: 300
```

## Code Patterns

### API Response Wrapper
All controllers return `ApiResponse<T>` from `util/ApiResponse.java`:
```java
return ResponseEntity.ok(ApiResponse.success(data));
return ResponseEntity.ok(ApiResponse.success(data, "Created successfully"));
return ResponseEntity.badRequest().body(ApiResponse.error("message"));
```
Paginated results use `PagedResponse` from `util/PagedResponse.java`.

### Exception Handling
Centralized `@RestControllerAdvice` in `GlobalExceptionHandler`:
- `ResourceNotFoundException` → 404
- `DuplicateResourceException` → 409 (e.g., duplicate tag code)
- `BusinessException` → 400 (e.g., livestock is stolen, clearance expired)
- `MethodArgumentNotValidException` → 400 with field-level errors

### MinIO File References
Files stored as `minio://bucket/path/to/file` in the database.
- Upload via `MinioStorageService.uploadFile()`
- Generate presigned URLs via `FileController` (`/api/v1/files/signed-url`)

### QR Code Formats
- Permits: `PERMIT:{permitNumber}:{tagCode}:{validUntil}`
- Clearances: `CLEARANCE:{clearanceNumber}:{tagCode}:{expiryDate}`
- Livestock: `LIVESTOCK:{tagCode}:{ownerId}`

## Security

JWT authentication is **active**. The `JwtAuthenticationFilter` validates Bearer tokens on all `/api/v1/**` endpoints except `/api/v1/auth/**`. Authenticate via `POST /api/v1/auth/login` to obtain a token.

**Officer Roles** (full `UserRole` enum):
- `NATIONAL_ADMIN`, `PROVINCIAL_ADMIN`, `DISTRICT_ADMIN`, `ADMIN` (legacy)
- `AGRITEX_OFFICER` — registers livestock, enrolls owners, issues permits
- `POLICE_OFFICER` — issues clearances, verifies permits at checkpoints
- `VETERINARY_OFFICER` — health records, vaccination logs
- `VETERINARY_INSPECTOR` (legacy), `OWNER`, `TRANSPORTER`

**Seed credentials** (created by `AppSeeder` on first startup):
| Email | Password | Role |
|---|---|---|
| `national.admin@digistock.gov.zw` | `Admin@2024` | NATIONAL_ADMIN |
| `provincial.admin.harare@digistock.gov.zw` | `Admin@2024` | PROVINCIAL_ADMIN |
| `agritex.officer@digistock.gov.zw` | `Agritex@2024` | AGRITEX_OFFICER |
| `veterinary.officer@digistock.gov.zw` | `Vet@2024` | VETERINARY_OFFICER |
| `police.officer@digistock.gov.zw` | `Police@2024` | POLICE_OFFICER |

`AppSeeder` (a `CommandLineRunner`) also seeds Provinces, Districts, Wards, a test Owner, Livestock, and Vaccinations on first run if tables are empty.

## Development Workflow

### Adding New Endpoints
1. Create/update DTO in `dto/request/` or `dto/response/`
2. Add interface method to `I{Entity}Service.java` and implement in `{Entity}Service.java`
3. Create controller endpoint annotated with Swagger `@Operation`/`@Tag`
4. Return `ApiResponse<T>` or `ApiResponse<PagedResponse<T>>`
5. Use existing exceptions (`ResourceNotFoundException`, `BusinessException`, `DuplicateResourceException`)

### Database Changes
1. Create new Liquibase changeset in `src/main/resources/db/changelog/changes/`
2. Naming convention: `00X-description.xml`
3. Reference in `db.changelog-master.xml`
4. Include `<rollback>` instructions

### File Upload Handling
1. Accept `MultipartFile` in controller
2. Validate file type and size (max 10MB per file, 15MB per request — enforced by Spring)
3. Upload to MinIO via `MinioStorageService.uploadFile()`
4. Store `minio://bucket/path` reference in entity field

## Common Pitfalls

- **MinIO endpoint**: `application.properties` defaults to port `30900`, not `9000`. Always set `MINIO_ENDPOINT=http://localhost:9000` when running locally with docker-compose.
- **Tag code province codes vs Province entity codes**: `TagCodeGenerator` maps `"Harare"→"HA"` for tag code prefixes; the `Province` entity stores code `"HR"`. These are different fields serving different purposes.
- **Tag code uniqueness**: Check for duplicates before registering livestock.
- **Stolen status**: Validate livestock is not stolen before issuing clearances or permits.
- **Clearance expiry**: Check `expiresAt` before issuing permits.
- **MinIO references**: Store `minio://` references in DB, not file content.
- **GPS coordinates**: Use `BigDecimal` for latitude/longitude.
- **Enum validation**: Use `@Enumerated(EnumType.STRING)`.

## Troubleshooting

**Database connection errors**: Verify PostgreSQL via `docker-compose ps`; note default credentials in docker-compose are `digistock`/`digistock` but `application.properties` defaults to `postgres`/`12345678` — set env vars explicitly.
**MinIO upload failures**: Check MinIO console (http://localhost:9001) for bucket permissions; ensure `MINIO_ENDPOINT` is set correctly.
**Liquibase errors**: Run `./mvnw liquibase:status` to check pending changesets.
**AppSeeder errors**: Seeder runs once on startup when tables are empty; check province/district/ward data exists before running seeder-dependent tests.

## Documentation References

- **API Documentation**: `API_DOCUMENTATION.md`
- **System Design**: `DIGISTOCK_SYSTEM_DESIGN.md`
- **Implementation Status**: `IMPLEMENTATION_STATUS.md`
- **Backend Structure**: `BACKEND_STRUCTURE.md`
