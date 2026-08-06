# Beauty Salon Explorer

Full-stack application for browsing, filtering, and managing beauty salons in Warsaw.

- Backend: Reactive Spring Boot API (WebFlux + R2DBC + PostgreSQL)
- Frontend: React + Vite SPA
- Architecture: Hexagonal (Backend), component-based UI (Frontend)

---

## Features

- Browse salons with pagination
- Dynamic filtering (planned enhancements)
- Full CRUD operations
- Reactive non-blocking backend
- Responsive UI (mobile-first)
- Real-time API communication

---

## Tech Stack

### Backend
- Spring Boot 3 (WebFlux)
- Spring Data R2DBC (PostgreSQL)
- Liquibase (DB migrations)
- Project Reactor (Mono/Flux)
- GraalVM Native Image
- Hexagonal Architecture
- Lombok, Jackson, Jakarta Validation

### Frontend
- React 18
- Vite
- React Router v6
- Axios
- SCSS (Sass)
- React Hooks

---

## Prerequisites

- Java 21
- Node.js 18+
- Maven 3.6+
- Docker (for PostgreSQL)

---

## Run Locally

### 1. Start database

```bash
docker compose up
```

---

### 2. Run backend

Build:
```bash
mvn clean install
```

Run JAR:
```bash
java -jar salonshub-0.0.1-SNAPSHOT.jar
```

(Optional) Native build:
```bash
mvn -Pnative native:compile
./salonshub
```

---

### 3. Run frontend

Install dependencies:
```bash
npm install
```

Start dev server:
```bash
npm run dev
```


Production build:
```bash
npm run build
npm run preview
```
```


## TODO

### Backend
- Add validation for fields in `DELETE /salons` and `GET /salons`
- Improve batch ingestion for `/ingest/batch` using SQL-native batching
- Refactor update logic to use `BeanUtils` for partial field updates
- Enhance dynamic search:
  - Add support for more filter types
  - Replace string matching with SQL `LIKE` in `SqlBuilder`
  - Add numeric range filtering (Integer, Double, etc.)

### Frontend
- Add advanced filtering with min/max inputs for numeric fields
- Implement infinite scroll instead of pagination buttons
- Optimize update flow once backend supports partial updates
```
