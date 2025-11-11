# DigiStock API Documentation

## Base URL

- **Local Development**: `http://localhost:8080`
- **Production**: `https://api.digistock.zw`

## API Documentation UI

- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`

---

## Authentication

🚧 **Current Status**: Authentication is disabled for MVP development.
🔜 **Planned**: OAuth2/JWT authentication will be added in future releases.

For now, use the `X-Officer-Id` header to simulate authenticated requests:
```
X-Officer-Id: <UUID of officer>
```

---

## API Endpoints

### 🐄 Livestock Management

#### Register New Livestock
```http
POST /api/v1/livestock
Content-Type: application/json

{
  "tagCode": "HA-02-012-0234",
  "name": "Tendai",
  "breed": "Brahman",
  "sex": "F",
  "birthDate": "2021-03-10",
  "color": "Brown",
  "distinguishingMarks": "white patch on left hind leg",
  "ownerId": "uuid",
  "motherId": "uuid (optional)",
  "fatherId": "uuid (optional)",
  "registrationLatitude": -17.8252,
  "registrationLongitude": 31.0335
}
```

#### Upload Livestock Photo
```http
POST /api/v1/livestock/{id}/photos
Content-Type: multipart/form-data

file: (image file)
description: "Front view"
photoType: "REGISTRATION"
```

#### Get Livestock by ID
```http
GET /api/v1/livestock/{id}
```

#### Get Livestock by Tag Code
```http
GET /api/v1/livestock/tag/{tagCode}
```

#### Get Livestock by Owner
```http
GET /api/v1/livestock/owner/{ownerId}
```

#### Get Offspring
```http
GET /api/v1/livestock/{id}/offspring
```

#### Mark as Stolen
```http
POST /api/v1/livestock/{id}/mark-stolen
```

#### Mark as Recovered
```http
POST /api/v1/livestock/{id}/mark-recovered
```

#### Get Stolen Livestock
```http
GET /api/v1/livestock/stolen
```

---

### 👤 Owner Management

#### Register New Owner
```http
POST /api/v1/owners
Content-Type: application/json

{
  "nationalId": "AB1234567",
  "firstName": "Tendai",
  "lastName": "Katsande",
  "phoneNumber": "+263771234567",
  "email": "tendai@example.com",
  "address": "123 Main Street",
  "ward": "Ward 12",
  "district": "Chitungwiza",
  "province": "Harare"
}
```

#### Enroll Fingerprint
```http
POST /api/v1/owners/{id}/fingerprint
Content-Type: multipart/form-data

file: (fingerprint image)
```

#### Upload Owner Photo
```http
POST /api/v1/owners/{id}/photo
Content-Type: multipart/form-data

file: (photo file)
```

#### Get Owner by ID
```http
GET /api/v1/owners/{id}
```

#### Get Owner by National ID
```http
GET /api/v1/owners/national-id/{nationalId}
```

#### Get Owners by District
```http
GET /api/v1/owners/district/{district}
```

#### Search Owners by Name
```http
GET /api/v1/owners/search?q={searchTerm}
```

#### Get All Owners
```http
GET /api/v1/owners
```

#### Update Owner
```http
PUT /api/v1/owners/{id}
Content-Type: application/json

{
  "nationalId": "AB1234567",
  "firstName": "Tendai",
  "lastName": "Katsande",
  ...
}
```

---

### 🚔 Police Clearance

#### Create Clearance
```http
POST /api/v1/clearances
Content-Type: application/json
X-Officer-Id: {officer-uuid}

{
  "livestockId": "uuid",
  "ownerId": "uuid",
  "notes": "Optional notes",
  "issueLatitude": -17.8252,
  "issueLongitude": 31.0335
}
```

#### Approve Clearance
```http
POST /api/v1/clearances/{id}/approve
X-Officer-Id: {officer-uuid}
```

#### Reject Clearance
```http
POST /api/v1/clearances/{id}/reject?reason={reason}
```

#### Get Clearance by ID
```http
GET /api/v1/clearances/{id}
```

#### Get Clearance by Number
```http
GET /api/v1/clearances/number/{clearanceNumber}
```

#### Get Valid Clearances for Livestock
```http
GET /api/v1/clearances/livestock/{livestockId}/valid
```

#### Get Clearances by Owner
```http
GET /api/v1/clearances/owner/{ownerId}
```

#### Get Pending Clearances
```http
GET /api/v1/clearances/pending
```

---

### 📜 Movement Permits

#### Create Permit
```http
POST /api/v1/permits
Content-Type: application/json
X-Officer-Id: {officer-uuid}

{
  "clearanceId": "uuid",
  "livestockId": "uuid",
  "fromLocation": "Goromonzi, Ward 12",
  "toLocation": "Mutoko, Ward 5",
  "purpose": "Sale",
  "transportMode": "Truck",
  "vehicleNumber": "ACP 1234",
  "driverName": "John Doe",
  "validFrom": "2025-01-15",
  "validUntil": "2025-01-22",
  "issueLatitude": -17.8252,
  "issueLongitude": 31.0335
}
```

#### Verify Permit (Checkpoint Scan)
```http
POST /api/v1/permits/{id}/verify
X-Officer-Id: {officer-uuid}

?latitude=-17.8252
&longitude=31.0335
&notes=Verified at Marondera roadblock
```

#### Complete Movement
```http
POST /api/v1/permits/{id}/complete
?latitude=-17.8252
&longitude=31.0335
```

#### Cancel Permit
```http
POST /api/v1/permits/{id}/cancel?reason={reason}
```

#### Get Permit by ID
```http
GET /api/v1/permits/{id}
```

#### Get Permit by Number
```http
GET /api/v1/permits/number/{permitNumber}
```

#### Get Permits for Livestock
```http
GET /api/v1/permits/livestock/{livestockId}
```

#### Get Permits by Status
```http
GET /api/v1/permits/status/{status}

Status: PENDING | APPROVED | IN_TRANSIT | COMPLETED | EXPIRED | CANCELLED
```

#### Get Valid Permits
```http
GET /api/v1/permits/valid
```

---

### 📁 File Management

#### Get Presigned URL
```http
GET /api/v1/files/signed-url
?fileRef=minio://bucket/path/to/file
&expiryMinutes=60
```

Response:
```json
{
  "fileRef": "minio://bucket/path/to/file",
  "signedUrl": "https://minio.example.com/...",
  "expiryMinutes": 60
}
```

---

## Response Format

### Success Response
```json
{
  "id": "uuid",
  "tagCode": "HA-02-012-0234",
  "name": "Tendai",
  ...
}
```

### Error Response
```json
{
  "timestamp": "2025-01-11T10:30:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "Livestock not found with tagCode: 'HA-02-012-9999'",
  "path": "/api/v1/livestock/tag/HA-02-012-9999"
}
```

### Validation Error Response
```json
{
  "timestamp": "2025-01-11T10:30:00Z",
  "status": 400,
  "error": "Validation Failed",
  "message": "Invalid input data",
  "path": "/api/v1/livestock",
  "validationErrors": {
    "tagCode": "Tag code is required",
    "ownerId": "Owner ID is required"
  }
}
```

---

## HTTP Status Codes

- `200 OK` - Request succeeded
- `201 Created` - Resource created successfully
- `400 Bad Request` - Invalid input data or business logic violation
- `404 Not Found` - Resource not found
- `409 Conflict` - Duplicate resource (e.g., tag code already exists)
- `500 Internal Server Error` - Unexpected server error

---

## Business Rules

### Livestock Registration
- Tag code must be unique
- Tag code format: `{PROVINCE}-{DISTRICT}-{WARD}-{SERIAL}`
- Owner must exist before registering livestock
- Mother and father (if provided) must exist

### Police Clearance
- Only POLICE_OFFICER or ADMIN can create clearances
- Livestock must not be reported stolen
- Owner must match livestock owner
- Clearance expires after 14 days (configurable)
- QR code generated on approval

### Movement Permit
- Only AGRITEX_OFFICER or ADMIN can issue permits
- Valid police clearance required
- Clearance must not be expired
- Livestock must not be stolen
- Permit expires after 7 days (configurable)
- QR code generated on issuance

### Permit Verification
- Creates verification record with GPS coordinates
- Flags invalid permits
- Detects stolen livestock
- Updates permit status to IN_TRANSIT on first verification

---

## Tag Code Structure

```
HA-02-012-0234
│  │  │   │
│  │  │   └─ Serial (4 digits, auto-incremented per ward)
│  │  └───── Ward code (3 digits)
│  └──────── District code (2 digits)
└─────────── Province code (2 letters)

Province Codes:
  BW - Bulawayo          MA - Manicaland       MC - Mashonaland Central
  ME - Mashonaland East  MW - Mashonaland West MV - Masvingo
  MN - Matabeleland North MS - Matabeleland South ML - Midlands
  HA - Harare
```

---

## Testing Endpoints

### Health Check
```http
GET /actuator/health
```

### Application Info
```http
GET /actuator/info
```

---

## Rate Limiting

🚧 Not yet implemented. Will be added in future releases.

---

## Pagination

🚧 Not yet implemented for list endpoints. Currently returns all results.
🔜 Planned: Add pagination support with `page`, `size`, and `sort` parameters.

---

## Security Headers

All API responses include:
- `X-Content-Type-Options: nosniff`
- `X-Frame-Options: DENY`
- `X-XSS-Protection: 1; mode=block`

---

## CORS Policy

Allowed origins:
- `http://localhost:3000` (React dev)
- `http://localhost:5173` (Vite dev)
- `https://digistock.zw`
- `https://admin.digistock.zw`

Allowed methods: `GET`, `POST`, `PUT`, `DELETE`, `OPTIONS`, `PATCH`

---

## Changelog

### v1.0.0 (2025-01-11)
- Initial API release
- Livestock registration and management
- Owner management with biometric enrollment
- Police clearance workflow
- Movement permit issuance and verification
- File storage with MinIO integration
- QR code generation
- Parentage tracking

---

For questions or support, contact: support@digistock.zw
