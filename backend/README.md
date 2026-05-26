# Warsaw Beauty Salon Explorer – Backend

Reactive REST API for exploring and managing beauty salons in Warsaw.
Built with **Hexagonal Architecture**, **Spring WebFlux**, and **R2DBC** for fully non-blocking database access.
Supports dynamic filtering, pagination, batch ingestion, and GraalVM native image compilation.

---

## Prerequisites

* Java 21
* Maven 3.6+
* Docker (for PostgreSQL)

---

## Run Locally

### 1. Start database

First, start PostgreSQL via Docker:

```bash
docker compose up
```

---

### 2. Run application (JAR mode)

Build the project:

```bash
mvn clean install
```

Run the application:

```bash
java -jar salonshub-0.0.1-SNAPSHOT.jar
```

---

### 3. Run native image (GraalVM)

Build native binary:

```bash
mvn -Pnative native:compile "-DspringAot.native.buildArgs=--no-fallback -O3 --gc=G1 -march=native"
```

Run it:

```bash
./salonshub
```

On Windows:

```bash
salonshub.exe
```

---

## Technologies Used

* Spring Boot 3 + WebFlux – reactive REST layer
* Spring Data R2DBC + PostgreSQL – reactive database access with JSONB and array support
* Liquibase – database schema versioning (XML changelogs)
* Project Reactor – Mono/Flux asynchronous streams
* GraalVM Native Image – ahead-of-time compilation (faster startup, lower memory usage)
* Hexagonal Architecture (Ports & Adapters) – separation of domain, application, and infrastructure layers
* Lombok, Jackson, Jakarta Validation – boilerplate reduction, JSON mapping, and request validation

---

## TODO

1. Improve update logic using `BeanCopyUtils` to update only changed fields
2. Enhance dynamic search:
 
    * support additional filter types
    * replace string matching with SQL `LIKE` in `SqlBuilder`
    * add range filtering for numeric types (Integer, Double, etc.)
