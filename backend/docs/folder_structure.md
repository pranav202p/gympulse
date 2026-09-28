## 1.2 Backend (Spring Boot)

Package-by-feature. Each feature folder holds its own controller, service, repository, entity and DTOs. No layered top-level packages (`controllers/`, `services/`) and no hexagonal/ports-and-adapters layers; that matches the naive-first approach and can be refactored later if a real pain point shows up.

```text
backend/
├── pom.xml
├── mvnw
├── mvnw.cmd
├── .mvn/
├── Dockerfile
└── src/
    ├── main/
    │   ├── java/com/pranav/gymtracker/
    │   │   ├── GymTrackerApplication.java
    │   │   │
    │   │   ├── common/
    │   │   │   ├── config/
    │   │   │   │   ├── CorsConfig.java
    │   │   │   │   ├── JpaAuditingConfig.java
    │   │   │   │   └── OpenApiConfig.java
    │   │   │   ├── entity/
    │   │   │   │   └── BaseEntity.java            // id (UUID), createdAt, updatedAt
    │   │   │   └── error/
    │   │   │       ├── ApiException.java          // carries ErrorCode + HTTP status
    │   │   │       ├── ErrorCode.java             // enum of all error codes
    │   │   │       └── GlobalExceptionHandler.java // @RestControllerAdvice -> ProblemDetail
    │   │   │
    │   │   ├── auth/
    │   │   │   ├── AuthController.java
    │   │   │   ├── AuthService.java
    │   │   │   ├── JwtService.java                // create/validate tokens
    │   │   │   ├── JwtAuthenticationFilter.java
    │   │   │   ├── SecurityConfig.java            // filter chain, role rules, CORS
    │   │   │   └── dto/
    │   │   │       ├── RegisterRequest.java       // record
    │   │   │       ├── LoginRequest.java          // record
    │   │   │       └── AuthResponse.java          // record
    │   │   │
    │   │   ├── user/
    │   │   │   ├── User.java
    │   │   │   ├── Role.java                      // enum: MEMBER, ADMIN
    │   │   │   ├── UserRepository.java
    │   │   │   ├── UserService.java
    │   │   │   ├── UserController.java            // /users/me
    │   │   │   └── dto/
    │   │   │       └── UserResponse.java
    │   │   │
    │   │   ├── gym/
    │   │   │   ├── Gym.java
    │   │   │   ├── GymRepository.java
    │   │   │   ├── GymService.java
    │   │   │   ├── GymController.java
    │   │   │   └── dto/
    │   │   │       ├── GymRequest.java
    │   │   │       └── GymResponse.java
    │   │   │
    │   │   ├── equipment/
    │   │   │   ├── Equipment.java
    │   │   │   ├── EquipmentType.java             // enum
    │   │   │   ├── EquipmentStatus.java           // enum + static from(occupied, capacity)
    │   │   │   ├── EquipmentRepository.java       // includes findByIdForUpdate (row lock)
    │   │   │   ├── EquipmentService.java
    │   │   │   ├── EquipmentController.java
    │   │   │   └── dto/
    │   │   │       ├── EquipmentRequest.java
    │   │   │       └── EquipmentResponse.java
    │   │   │
    │   │   ├── session/
    │   │   │   ├── EquipmentSession.java
    │   │   │   ├── SessionRepository.java         // count active, find active by user
    │   │   │   ├── SessionService.java            // start/finish + concurrency guard
    │   │   │   ├── SessionController.java
    │   │   │   └── dto/
    │   │   │       └── SessionResponse.java
    │   │   │
    │   │   └── realtime/
    │   │       ├── WebSocketConfig.java           // STOMP endpoint /ws, broker /topic
    │   │       ├── WebSocketAuthInterceptor.java  // validates JWT on STOMP CONNECT
    │   │       ├── EquipmentBroadcaster.java      // called by SessionService / EquipmentService
    │   │       └── EquipmentUpdateMessage.java    // record, payload pushed to clients
    │   │
    │   └── resources/
    │       ├── application.yml
    │       ├── application-dev.yml
    │       ├── application-prod.yml
    │       └── db/
    │           └── migration/                     // Flyway
    │               ├── V1__create_users.sql
    │               ├── V2__create_gyms.sql
    │               ├── V3__create_equipment.sql
    │               └── V4__create_equipment_sessions.sql
    │
    └── test/
        └── java/com/pranav/gymtracker/
            ├── support/
            │   └── PostgresContainerConfig.java   // Testcontainers
            ├── auth/
            │   └── AuthControllerIT.java
            ├── equipment/
            │   └── EquipmentStatusTest.java       // pure unit test
            └── session/
                ├── SessionServiceTest.java
                └── SessionConcurrencyIT.java      // N threads, last unit
```

