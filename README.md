

# URL Shortener Service

## Overview
This project is a URL Shortener service built with Spring Boot 3.5, supporting the creation, management, and analytics of shortened URLs. It features unique short code generation, access logging, and a RESTful API with HATEOAS links.

---

## Architecture & Patterns Used
- **Spring Boot**: Rapid application development, dependency injection, and configuration management
- **Clean Architecture**: Separation of concerns between controllers, services, repositories, and models
- **Event-driven**: Uses Spring events for asynchronous access logging
- **HATEOAS**: Hypermedia links in API responses for discoverability
- **Flyway**: Database migrations for schema management
- **OpenAPI/Swagger**: API documentation and UI
- **Profiles**: Isolated configurations for local/dev/e2e
- **Distributed Tracing**: Micrometer + OpenTelemetry for observability
- **Exception Handling**: Centralized with custom error responses

---

## API Documentation (Swagger)
- Swagger UI: [http://localhost:8085/swagger-ui/index.html](http://localhost:8085/swagger-ui/index.html)
- OpenAPI JSON: [http://localhost:8085/v3/api-docs](http://localhost:8085/v3/api-docs)
- API Spec: See [`api-spec/api-docs.yaml`](api-spec/api-docs.yaml)
- Postman collection: postman-collection/url-shortner-collection.json

### Main Endpoints
- `POST /urls` — Shorten a URL
- `GET /urls/{code}` — Get info about a short URL (with HATEOAS links)
- `GET /urls/{code}/redirect` — Redirect to the original URL
- `GET /urls/{code}/access-logs` — Paginated access logs for a short URL

---



## How to Compile
This is a Maven project (Java 21):

```sh
mvn clean package
```
The JAR will be generated in `target/urlshortener-0.0.1-SNAPSHOT.jar`.

---

## How to Run Locally
By default, runs with an in-memory H2 database and local profile:

```sh
mvn spring-boot:run
# or
java -jar target/urlshortener-0.0.1-SNAPSHOT.jar
```
Access the app at [http://localhost:8085](http://localhost:8085)

---

## Profiles
- **default**: Local development, H2 in-memory DB
- **e2e**: End-to-end testing, uses PostgreSQL (see `application-e2e.yml`)
    - Activate with `-Dspring.profiles.active=e2e` or `SPRING_PROFILES_ACTIVE=e2e`

---

## How to Run with Docker
Build the JAR first:

```sh
mvn clean package
```

### Standalone Docker
Build and run the image:

```sh
docker build -t firosemalik/url-shortener-service:1.0.0 .
docker run -p 8085:8085 firosemalik/url-shortener-service:1.0.0
```

### Docker Compose (with PostgreSQL)
Recommended for local development/testing:

```sh
docker compose -f docker/compose/docker-compose.yml up --build
```
This will start both the service and a PostgreSQL database. The service will be available at [http://localhost:8085](http://localhost:8085).

---

## Database Migrations
Managed by Flyway. See `src/main/resources/db/migration/` for migration scripts.

---

## Test Types
- **Unit Tests**: For utility and mapping logic
- **Component Tests**: For service layer (with Spring context)
- **WebMvc Tests**: For controller layer (mocked services)
- **Exception Handler Tests**: For error response validation
- **Blackbox tests (RestAssure)**: Checks API aligns with the contract

Run all tests:
```sh
mvn test
```

---

## Security & Secrets
- The `hashid.salt` should be stored securely (e.g., Vault, env variable)
- See `application.yml` for config; override via environment variables in production

---

## Observability
- Distributed tracing via Micrometer + OpenTelemetry
- Actuator endpoints enabled: `/actuator/health`, `/actuator/info`

---

## Future Enhancements
- Caching (e.g., Redis)
- Rate limiting, circuit breaker
- Kubernetes deployment
- Secret management integration
- Advanced analytics

---

## Contact
firosemalik@gmail.com
