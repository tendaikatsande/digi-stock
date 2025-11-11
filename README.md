# DigiStock - Digital Livestock Management System for Zimbabwe

## 🐄 Overview

DigiStock is a comprehensive digital livestock management platform designed to revolutionize cattle tracking and eliminate livestock rustling in Zimbabwe. The system replaces traditional paper livestock cards with a modern, secure digital solution that integrates biometric identification, GPS tracking, and real-time verification.

### Key Features

- **Digital Livestock Registration** - Multi-layer identification using RFID tags, QR codes, and freeze branding
- **Biometric Enrollment** - Fingerprint capture for both owners and officers using SourceAFIS
- **Police Clearance System** - Verify ownership legitimacy before movement
- **Movement Permits** - Digital permits with QR codes for roadblock verification
- **Parentage Tracking** - Complete lineage tracking (mother/father relationships)
- **Offline-First Mobile Apps** - Work in rural areas with limited connectivity
- **Real-Time Verification** - Police can scan and verify permits at checkpoints
- **Audit Trail** - Immutable tracking of all livestock movements

## 🏗️ Architecture

DigiStock is built as a monorepo containing:

```
digi-stock/
├── backend/              # Spring Boot REST API
├── mobile-officer/       # Flutter app for AGRITEX & Police officers
├── mobile-owner/         # Flutter app for livestock owners
├── admin-portal/         # React admin dashboard
└── docs/                 # Documentation
```

### Technology Stack

**Backend:**
- Spring Boot 3.2 (Java 17)
- PostgreSQL 15
- MinIO (S3-compatible object storage)
- SourceAFIS (biometric fingerprint matching)
- Liquibase (database migrations)
- OAuth2/JWT (authentication)

**Mobile:**
- Flutter 3.x
- SQLite (offline storage)
- local_auth (biometric)

**Admin Portal:**
- React 18 + TypeScript
- Material UI
- Charts.js (analytics)

## 🚀 Quick Start

### Prerequisites

- Java 17+
- Maven 3.8+
- Docker & Docker Compose
- PostgreSQL 15+ (or use Docker)
- MinIO (or use Docker)

### 1. Start Infrastructure Services

```bash
# Start PostgreSQL and MinIO
docker-compose up -d

# Verify services are running
docker-compose ps
```

- PostgreSQL: `localhost:5432`
- MinIO API: `localhost:9000`
- MinIO Console: `http://localhost:9001` (minioadmin/minioadmin)

### 2. Run the Backend

```bash
cd backend

# Build the project
./mvnw clean install

# Run the application
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080`

### 3. Access the API Documentation

Once running, visit:
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- Actuator Health: `http://localhost:8080/actuator/health`

### Default Credentials

**Admin Login:**
- Email: `admin@digistock.zw`
- Password: `Admin@123` ⚠️ **Change in production!**

## 📊 Database Schema

### Core Entities

1. **Owners** - Livestock owners with biometric enrollment
2. **Officers** - AGRITEX officers, police, vets, admins
3. **Livestock** - Individual animals with tag codes, photos, parentage
4. **LivestockPhoto** - Multiple photos per animal
5. **PoliceClearance** - Ownership verification before movement
6. **MovementPermit** - Authorized livestock movements
7. **PermitVerification** - Checkpoint scans and audit trail

### Tag Code Format

Hierarchical identification inspired by Mumbai's Dabbawalla system:

```
{PROVINCE}-{DISTRICT}-{WARD}-{SERIAL}

Example: HA-02-012-0234
  HA     = Harare province
  02     = Chitungwiza district
  012    = Ward 12
  0234   = Serial number
```

### Province Codes

| Code | Province              |
|------|-----------------------|
| BW   | Bulawayo              |
| HA   | Harare                |
| MA   | Manicaland            |
| MC   | Mashonaland Central   |
| ME   | Mashonaland East      |
| MW   | Mashonaland West      |
| MV   | Masvingo              |
| MN   | Matabeleland North    |
| MS   | Matabeleland South    |
| ML   | Midlands              |

## 🔐 Security Features

### Biometric Authentication

- **SourceAFIS Integration** - Industry-standard fingerprint matching
- **Fingerprint Enrollment** - Both officers and owners
- **Template Storage** - Encrypted templates in MinIO
- **Match Threshold** - Configurable (default: 40.0)

### Authentication & Authorization

- OAuth2 + JWT tokens
- Role-based access control (RBAC)
- Roles: ADMIN, AGRITEX_OFFICER, POLICE_OFFICER, OWNER, VETERINARY_INSPECTOR

## 📱 API Endpoints (Planned)

### Authentication
- `POST /api/v1/auth/login` - Username/password login
- `POST /api/v1/auth/biometric/login` - Fingerprint login
- `POST /api/v1/auth/biometric/enroll` - Enroll fingerprint

### Livestock
- `POST /api/v1/livestock` - Register new livestock
- `GET /api/v1/livestock/{id}` - Get livestock details
- `GET /api/v1/livestock?tag_code=...` - Search by tag

### Police Clearances
- `POST /api/v1/clearances` - Create clearance request
- `POST /api/v1/clearances/{id}/approve` - Approve clearance
- `GET /api/v1/clearances/{id}` - Get clearance details

### Movement Permits
- `POST /api/v1/permits` - Issue movement permit
- `POST /api/v1/permits/{id}/verify` - Verify at checkpoint
- `POST /api/v1/permits/{id}/complete` - Mark movement complete

### File Storage
- `POST /api/v1/files/upload` - Upload to MinIO
- `GET /api/v1/files/{fileId}/signed-url` - Get presigned URL

## 🗂️ MinIO Buckets

| Bucket                      | Purpose                      |
|-----------------------------|------------------------------|
| digistock-livestock-photos  | Animal photos                |
| digistock-fingerprints      | Biometric templates          |
| digistock-permits           | Movement permit PDFs         |
| digistock-clearances        | Police clearance PDFs        |
| digistock-qr-codes          | Generated QR codes           |

## 🔧 Configuration

Key configuration in `backend/src/main/resources/application.yml`:

```yaml
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/digistock

# MinIO
minio.endpoint=http://localhost:9000
minio.access-key=minioadmin
minio.secret-key=minioadmin

# SourceAFIS
sourceafis.match-threshold=40.0
sourceafis.enable-caching=true

# Business Rules
digistock.permit.default-validity-days=7
digistock.clearance.default-validity-days=14
```

## 🧪 Testing

```bash
cd backend

# Run all tests
./mvnw test

# Run with coverage
./mvnw test jacoco:report
```

## 📦 Deployment

### Docker Build

```bash
cd backend
./mvnw clean package -DskipTests
docker build -t digistock-backend:latest .
```

### Kubernetes

Helm charts coming soon in `/k8s` directory.

## 🤝 Contributing

This is a government-backed initiative led by AGRITEX. For questions or contributions:

1. Create a feature branch from `main`
2. Follow Java code style guidelines
3. Write tests for new features
4. Submit a pull request

## 📄 License

Copyright © 2025 DigiStock - Zimbabwe Government
All rights reserved.

## 🙏 Acknowledgments

- **AGRITEX** - Agricultural extension services
- **Zimbabwe Republic Police** - Anti-Stock Theft Unit
- **SourceAFIS** - Open-source fingerprint matching
- **MinIO** - S3-compatible object storage
- Inspired by Mumbai's Dabbawalla sorting system

## 📞 Contact

For support or inquiries:
- Email: support@digistock.zw
- Website: https://digistock.zw

---

**Made with ❤️ for Zimbabwe's farmers and livestock owners**
