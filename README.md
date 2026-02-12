# RealEstate CRM

Full-stack Real Estate CRM monorepo with:
- `backend`: Spring Boot 3 (Java 21), PostgreSQL, JWT auth, role-based access, S3 document storage
- `frontend`: React 18 + Vite + Tailwind, routed SPA
- `docker-compose`: production-style local orchestration (Postgres + backend + frontend Nginx)

## Architecture

- Backend provides REST APIs for project lifecycle, enquiries, follow-ups, bookings, clients, users, dashboard, documents, amenities, and bank/disbursement details.
- Frontend consumes backend through `/api/*` routes.
- In Docker, Nginx (frontend container) proxies `/api/*` to the backend container.
- Security is stateless JWT with access + refresh tokens.
- Daily scheduler generates follow-up tasks.
- Documents and letterheads are stored in AWS S3; downloads are served via pre-signed URLs.

## Repository Structure

```text
.
├── backend/
│   ├── src/main/java/com/ysminfosolution/realestate/
│   │   ├── controller/      # 17 REST controllers
│   │   ├── service/         # services + implementations
│   │   ├── repository/      # Spring Data JPA repositories
│   │   ├── model/           # JPA entities
│   │   ├── security/        # JWT filter, login filter, security config
│   │   ├── scheduler/       # daily task generator
│   │   └── error/           # exception + advice handlers
│   └── src/main/resources/
│       ├── application*.properties
│       ├── logback-spring.xml
│       └── static/          # actuator dashboard page/scripts
├── frontend/
│   ├── src/pages/           # dashboard, projects, registration, enquiry, follow-up, etc.
│   ├── src/services/        # API service layer
│   ├── src/contexts/        # auth + local data context
│   └── nginx.conf           # SPA hosting + /api proxy
├── docker-compose.yml
└── .env                     # runtime variables (gitignored)
```

## Tech Stack

- Backend: Spring Boot `3.5.x`, Java `21`, Spring Security, Spring Data JPA, Validation, Actuator, springdoc-openapi
- Database: PostgreSQL `16`
- Auth: JWT (Nimbus JOSE JWT)
- Storage: AWS SDK v2 (`S3Client`, `S3Presigner`)
- Frontend: React `18`, React Router `6`, Vite `7`, TailwindCSS, Recharts
- Deployment runtime: Docker + Nginx

## Environment Variables

Configured via `.env` and used by `docker-compose.yml` / Spring properties:

- `POSTGRES_DB`
- `POSTGRES_USER`
- `POSTGRES_PASSWORD`
- `SPRING_PROFILE` (`dev` or `prod`)
- `REALESTATE_JWT_SECRET` (base64-encoded secret for JWT signing)
- `REALESTATE_BASE_DIRECTORY` (base path used by backend logs/files)
- `JAVA_OPTS`
- `AWS_ACCESS_KEY_ID`
- `AWS_SECRET_ACCESS_KEY`
- `AWS_REGION`
- `AWS_S3_BUCKET`
- `VITE_API_URL` (present in `.env`; frontend code currently uses fixed `/api`)

## Run with Docker (Recommended)

1. Populate `.env` with required values.
2. Start stack:

```bash
docker compose up --build -d
```

3. Open app:
- Frontend: `http://localhost:3000`
- Backend via proxy: `http://localhost:3000/api/...`
- Swagger UI via proxy: `http://localhost:3000/api/swagger-ui/index.html`

## Run Locally (Without Docker)

### Backend

```bash
cd backend
./mvnw spring-boot:run
```

Default backend port: `8080`.

### Frontend

```bash
cd frontend
npm ci
npm run dev
```

Vite runs on `3000`.

Note: frontend services call `/api/*`. For non-Docker local dev, configure a Vite proxy (or serve through Nginx) so `/api` forwards to backend `http://localhost:8080`.

## Authentication & Authorization

- Public endpoints:
  - `POST /login`
  - `POST /refresh`
  - `POST /register-organization`
  - `GET /status`
- Protected endpoints require `Authorization: Bearer <accessToken>`.
- Roles used in code: `ADMIN`, `EMPLOYEE` (and `DEVELOPER` in admin controller preauth expression).
- Access token TTL: 24h in properties (`security.jwt.access-ttl-seconds=86400`).
- Refresh token TTL: 14 days (`security.jwt.refresh-ttl-seconds=1209600`).

## API Surface (High Level)

Main backend modules and base paths:

- `/projects` (CRUD + project structure + basic list)
- `/wings` (project wing management)
- `/amenities` (project amenities)
- `/bankProjectInfo` (approved bank info)
- `/documents` (document metadata + S3 pre-signed download URL)
- `/disbursements` (project disbursement stages)
- `/enquiries` (lead lifecycle + property options + status/cancel flows)
- `/followUps` (follow-up timeline and task windows)
- `/bookings` (create booking from enquiry/property/client context)
- `/clients` (client basic/detail + update)
- `/users` (organization users + create/update/delete)
- `/admins` (project employee allocation)
- `/dashboard` (global dashboard aggregates)

## Domain Model Coverage

Core entities include:
- `Organization`, `User`, `AdminUserInfo`, `EmployeeUserInfo`, `ClientUserInfo`
- `Project`, `Wing`, `Floor`, `Flat`
- `Enquiry`, `FollowUp`, `FollowUpNode`, `Task`, `Booking`
- `Document`, `Amenity`, `BankProjectInfo`, `Disbursement`

## Scheduler

- `DailyTaskScheduler` runs daily at `01:00` server time and regenerates follow-up tasks for due follow-ups.

## Logging

- Configured via `backend/src/main/resources/logback-spring.xml`.
- File logging is controlled by `LOG_TO_FILE`.
- Log directory is driven by `realestate.base.directory`.

## Database Dump / Restore Notes

`backend/Instructions.md` contains sample `pg_dump` and `pg_restore` commands for `backend/realestate.dump`.

## Testing

Backend currently includes a basic Spring context-load test:

```bash
cd backend
./mvnw test
```

## Current Implementation Notes

- `frontend/src/App.jsx` has some routes/pages currently commented out (`bookings`, `payments`, `notifications`, `settings`).
- `backend` has an `EmployeeController` scaffold with no endpoints yet.
- Frontend still keeps a local `DataContext`; primary business data flows are API-driven through `src/services/*`.
