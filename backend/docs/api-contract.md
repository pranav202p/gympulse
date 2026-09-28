# Gym Equipment Availability System
## Backend API Contract

Spring Boot 3 (Java 21) REST + STOMP WebSocket API. Version 1, MVP scope.

---

## 1. Conventions

| Item | Value |
|---|---|
| Base path | `/api/v1` |
| Content type | `application/json` (UTF-8) |
| Auth | `Authorization: Bearer <accessToken>` on every endpoint except register and login |
| IDs | UUID strings |
| Timestamps | ISO-8601 UTC, e.g. `2026-10-04T09:30:00Z` |
| JSON naming | `camelCase` |
| Roles | `MEMBER`, `ADMIN` (registration always creates `MEMBER`; admins are seeded) |
| Errors | RFC 7807 `application/problem+json` with an extra `code` field (see section 3) |
| Pagination | None in MVP (gym and equipment lists are small) |
| Docs | OpenAPI at `/v3/api-docs`, Swagger UI at `/swagger-ui.html` (dev profile only) |
| Health | `GET /actuator/health` (public) |

## 2. Enums

```text
Role             MEMBER | ADMIN
EquipmentType    STRENGTH | CARDIO | FREE_WEIGHTS | OTHER
EquipmentStatus  AVAILABLE | LIMITED | FULL   (computed, never stored)
```

Status rule (single source of truth: `EquipmentStatus.from(occupied, capacity)`):

```text
occupied >= capacity        -> FULL
occupied * 2 >= capacity    -> LIMITED
otherwise                   -> AVAILABLE
```

Examples: capacity 3 -> 0 occupied AVAILABLE, 1 AVAILABLE, 2 LIMITED, 3 FULL. Capacity 2 -> 1 occupied is LIMITED. Capacity 4 -> 1 occupied AVAILABLE, 2 LIMITED. Adjust the threshold here only.

## 3. Error format

```json
{
  "type": "about:blank",
  "title": "Equipment is full",
  "status": 409,
  "detail": "All 3 units of Bench Press are currently occupied.",
  "instance": "/api/v1/equipment/6f1c.../start",
  "code": "EQUIPMENT_FULL"
}
```

Validation errors add `fieldErrors`:

```json
{
  "type": "about:blank",
  "title": "Validation failed",
  "status": 400,
  "detail": "One or more fields are invalid.",
  "instance": "/api/v1/auth/register",
  "code": "VALIDATION_ERROR",
  "fieldErrors": [
    { "field": "password", "message": "must be between 8 and 72 characters" }
  ]
}
```

Error codes:

| HTTP | code | When |
|---|---|---|
| 400 | `VALIDATION_ERROR` | Request body or parameter fails validation |
| 401 | `UNAUTHENTICATED` | Missing, expired or invalid token |
| 401 | `INVALID_CREDENTIALS` | Wrong email or password on login |
| 403 | `FORBIDDEN` | Authenticated but role not allowed |
| 404 | `GYM_NOT_FOUND` | Unknown gym id |
| 404 | `EQUIPMENT_NOT_FOUND` | Unknown equipment id |
| 404 | `NO_ACTIVE_SESSION` | Finish called with no active session on that equipment |
| 409 | `EMAIL_ALREADY_EXISTS` | Register with an existing email |
| 409 | `EQUIPMENT_FULL` | Start called when occupied >= capacity |
| 409 | `ALREADY_IN_SESSION` | User already has an active session (on any equipment) |
| 409 | `GYM_HAS_EQUIPMENT` | Delete gym that still has equipment |
| 409 | `EQUIPMENT_IN_USE` | Delete equipment that has active sessions |
| 409 | `CAPACITY_BELOW_OCCUPANCY` | Update capacity to less than current occupied count |
| 500 | `INTERNAL_ERROR` | Unhandled exception (no internals leaked) |

---

## 4. Auth

### POST `/api/v1/auth/register`

Access: public.

Request:

```json
{
  "name": "Pranav",
  "email": "pranav@example.com",
  "password": "s3cretPass!"
}
```

Validation: `name` 2-80 chars; `email` valid format, max 254, stored lowercased; `password` 8-72 chars.

Response `201 Created`:

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 3600,
  "user": {
    "id": "0b8f6a52-6f0e-4d6c-9d43-1c1f5a3c9e11",
    "name": "Pranav",
    "email": "pranav@example.com",
    "role": "MEMBER"
  }
}
```

Errors: `400 VALIDATION_ERROR`, `409 EMAIL_ALREADY_EXISTS`.

### POST `/api/v1/auth/login`

Access: public.

Request:

```json
{
  "email": "pranav@example.com",
  "password": "s3cretPass!"
}
```

Response `200 OK`: same body as register.

Errors: `400 VALIDATION_ERROR`, `401 INVALID_CREDENTIALS`.

Token: JWT, HS256, claims `sub` (user id), `role`, `iat`, `exp`. Access token only for MVP; refresh tokens are out of scope.

---

## 5. Current user

### GET `/api/v1/users/me`

Access: MEMBER, ADMIN.

Response `200 OK`:

```json
{
  "id": "0b8f6a52-6f0e-4d6c-9d43-1c1f5a3c9e11",
  "name": "Pranav",
  "email": "pranav@example.com",
  "role": "MEMBER"
}
```

### GET `/api/v1/users/me/session`

Returns the caller's active session, if any. Lets the frontend restore "I am currently on Bench Press" after a page reload.

Access: MEMBER, ADMIN.

Response `200 OK` with a `SessionResponse` (see section 8), or `204 No Content` when there is no active session.

---

## 6. Gyms

`GymResponse`:

```json
{
  "id": "a3c5d1f0-2b6e-4c1a-8f77-0d9a1e2b3c4d",
  "name": "Gold Gym",
  "location": "Koramangala, Bengaluru",
  "createdAt": "2026-10-04T09:00:00Z"
}
```

### GET `/api/v1/gyms`

Access: MEMBER, ADMIN. Response `200 OK`: `GymResponse[]`.

### GET `/api/v1/gyms/{gymId}`

Access: MEMBER, ADMIN. Response `200 OK`: `GymResponse`. Errors: `404 GYM_NOT_FOUND`.

### POST `/api/v1/gyms`

Access: ADMIN.

Request:

```json
{
  "name": "Gold Gym",
  "location": "Koramangala, Bengaluru"
}
```

Validation: `name` 2-120 chars, required; `location` max 255, required.

Response `201 Created`, `Location: /api/v1/gyms/{id}`, body `GymResponse`.

### PUT `/api/v1/gyms/{gymId}`

Access: ADMIN. Request: same as POST. Response `200 OK`: `GymResponse`. Errors: `404 GYM_NOT_FOUND`, `400 VALIDATION_ERROR`.

### DELETE `/api/v1/gyms/{gymId}`

Access: ADMIN. Response `204 No Content`. Errors: `404 GYM_NOT_FOUND`, `409 GYM_HAS_EQUIPMENT`.

---

## 7. Equipment

`EquipmentResponse`:

```json
{
  "id": "6f1c2d3e-4a5b-4c6d-8e7f-9a0b1c2d3e4f",
  "gymId": "a3c5d1f0-2b6e-4c1a-8f77-0d9a1e2b3c4d",
  "name": "Bench Press",
  "type": "STRENGTH",
  "capacity": 3,
  "occupiedCount": 2,
  "availableCount": 1,
  "status": "LIMITED",
  "createdAt": "2026-10-04T09:05:00Z",
  "updatedAt": "2026-10-04T09:05:00Z"
}
```

`occupiedCount`, `availableCount` and `status` are always computed at read time from active sessions.

### GET `/api/v1/gyms/{gymId}/equipment`

Access: MEMBER, ADMIN. Response `200 OK`: `EquipmentResponse[]`, ordered by `name` ascending. Errors: `404 GYM_NOT_FOUND`.

### GET `/api/v1/equipment/{equipmentId}`

Access: MEMBER, ADMIN. Response `200 OK`: `EquipmentResponse`. Errors: `404 EQUIPMENT_NOT_FOUND`.

### POST `/api/v1/gyms/{gymId}/equipment`

Access: ADMIN.

Request:

```json
{
  "name": "Bench Press",
  "type": "STRENGTH",
  "capacity": 3
}
```

Validation: `name` 2-120 chars; `type` one of `EquipmentType`; `capacity` integer 1-50.

Response `201 Created`, `Location: /api/v1/equipment/{id}`, body `EquipmentResponse`. Errors: `404 GYM_NOT_FOUND`, `400 VALIDATION_ERROR`.

### PUT `/api/v1/equipment/{equipmentId}`

Access: ADMIN. Request: same as POST. Response `200 OK`: `EquipmentResponse`.

Errors: `404 EQUIPMENT_NOT_FOUND`, `400 VALIDATION_ERROR`, `409 CAPACITY_BELOW_OCCUPANCY`.

Broadcasts an `EquipmentUpdateMessage` (see section 9) when capacity changes.

### DELETE `/api/v1/equipment/{equipmentId}`

Access: ADMIN. Response `204 No Content`. Errors: `404 EQUIPMENT_NOT_FOUND`, `409 EQUIPMENT_IN_USE`.

---

## 8. Sessions (occupancy)

`SessionResponse`:

```json
{
  "id": "c7d8e9f0-1a2b-4c3d-8e4f-5a6b7c8d9e0f",
  "equipmentId": "6f1c2d3e-4a5b-4c6d-8e7f-9a0b1c2d3e4f",
  "equipmentName": "Bench Press",
  "gymId": "a3c5d1f0-2b6e-4c1a-8f77-0d9a1e2b3c4d",
  "startedAt": "2026-10-04T09:30:00Z",
  "endedAt": null,
  "durationSeconds": null,
  "equipment": {
    "id": "6f1c2d3e-4a5b-4c6d-8e7f-9a0b1c2d3e4f",
    "capacity": 3,
    "occupiedCount": 3,
    "availableCount": 0,
    "status": "FULL"
  }
}
```

`endedAt` and `durationSeconds` are `null` while the session is active. `equipment` is a snapshot of the equipment state right after the operation.

### POST `/api/v1/equipment/{equipmentId}/start`

Marks the caller as using one unit. No request body.

Access: MEMBER, ADMIN.

Server behaviour, in one transaction:

1. Lock the equipment row (`SELECT ... FOR UPDATE`).
2. Reject if the caller already has an active session on any equipment.
3. Count active sessions; reject if `count >= capacity`.
4. Insert session with `started_at = now`.
5. After commit, broadcast `EquipmentUpdateMessage` to `/topic/gym/{gymId}/equipment`.

Response `201 Created`: `SessionResponse` (`endedAt: null`).

Errors: `404 EQUIPMENT_NOT_FOUND`, `409 EQUIPMENT_FULL`, `409 ALREADY_IN_SESSION`.

### POST `/api/v1/equipment/{equipmentId}/finish`

Ends the caller's active session on this equipment. No request body.

Access: MEMBER, ADMIN.

Server behaviour: find the caller's active session for this equipment, set `ended_at = now`, commit, broadcast.

Response `200 OK`: `SessionResponse` with `endedAt` and `durationSeconds` populated.

Errors: `404 EQUIPMENT_NOT_FOUND`, `404 NO_ACTIVE_SESSION`.

Both operations are safe to retry from the client's point of view: a repeated `start` returns `409 ALREADY_IN_SESSION`, a repeated `finish` returns `404 NO_ACTIVE_SESSION`.

---

## 9. WebSocket (real-time)

| Item | Value |
|---|---|
| Endpoint | `/ws` (STOMP over WebSocket, SockJS fallback enabled) |
| Auth | STOMP `CONNECT` frame header `Authorization: Bearer <accessToken>`; invalid or missing token closes the connection |
| Subscribe destination | `/topic/gym/{gymId}/equipment` |
| Direction | Server to client only. Clients change state through REST, never through the socket |
| Application prefix | none (no client-to-server STOMP messages in MVP) |

Message payload (`EquipmentUpdateMessage`):

```json
{
  "gymId": "a3c5d1f0-2b6e-4c1a-8f77-0d9a1e2b3c4d",
  "equipmentId": "6f1c2d3e-4a5b-4c6d-8e7f-9a0b1c2d3e4f",
  "name": "Bench Press",
  "capacity": 3,
  "occupiedCount": 2,
  "availableCount": 1,
  "status": "LIMITED",
  "updatedAt": "2026-10-04T09:31:12Z"
}
```

Published when: a session starts, a session finishes, or an admin changes capacity. Equipment add/remove is not broadcast in MVP; clients pick those up on the next REST fetch.

Client rules:

- On entering `/gyms/:gymId/equipment`: fetch the list over REST first, then subscribe.
- Merge each message into the store by `equipmentId` (replace the matching row).
- On reconnect: refetch the list over REST, then resume applying messages (messages missed while disconnected are not replayed).

---

## 10. Endpoint summary and access matrix

| Method | Path | MEMBER | ADMIN | Success |
|---|---|:-:|:-:|---|
| POST | `/auth/register` | public | public | 201 |
| POST | `/auth/login` | public | public | 200 |
| GET | `/users/me` | yes | yes | 200 |
| GET | `/users/me/session` | yes | yes | 200 / 204 |
| GET | `/gyms` | yes | yes | 200 |
| GET | `/gyms/{gymId}` | yes | yes | 200 |
| POST | `/gyms` | no | yes | 201 |
| PUT | `/gyms/{gymId}` | no | yes | 200 |
| DELETE | `/gyms/{gymId}` | no | yes | 204 |
| GET | `/gyms/{gymId}/equipment` | yes | yes | 200 |
| POST | `/gyms/{gymId}/equipment` | no | yes | 201 |
| GET | `/equipment/{equipmentId}` | yes | yes | 200 |
| PUT | `/equipment/{equipmentId}` | no | yes | 200 |
| DELETE | `/equipment/{equipmentId}` | no | yes | 204 |
| POST | `/equipment/{equipmentId}/start` | yes | yes | 201 |
| POST | `/equipment/{equipmentId}/finish` | yes | yes | 200 |

All paths above are relative to `/api/v1`.

---

## 11. Deferred (not in the 3-month MVP)

Reserved so the URL space stays clean; do not implement in this cycle.

| Feature | Reserved endpoints |
|---|---|
| Availability notifications | `POST /equipment/{id}/subscribe`, `DELETE /equipment/{id}/subscribe` |
| Session history | `GET /users/me/sessions` (paginated) |
| Refresh tokens | `POST /auth/refresh`, `POST /auth/logout` |
| Waitlist | `POST /equipment/{id}/queue`, `DELETE /equipment/{id}/queue` |
| Admin analytics | `GET /gyms/{gymId}/stats` |
