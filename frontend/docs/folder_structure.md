## 1.3 Frontend (Angular)
 
Standalone components (no NgModules), signals for state, functional guards and interceptors, lazy-loaded feature routes, new control flow (`@if`, `@for`). Three top-level buckets under `app/`: `core` (singletons, loaded once), `shared` (dumb reusable pieces), `features` (one folder per user-facing area).
 
```text
frontend/
├── angular.json
├── package.json
├── tsconfig.json
├── tsconfig.app.json
├── tsconfig.spec.json
├── proxy.conf.json                  # /api and /ws -> http://localhost:8080 (avoids CORS in dev)
├── Dockerfile                       # multi-stage: node build -> nginx
├── nginx.conf                       # serves SPA, proxies /api and /ws to backend
├── public/                          # static assets, favicon
└── src/
    ├── main.ts
    ├── index.html
    ├── styles.scss                  # design tokens (CSS variables), global resets
    ├── environments/
    │   ├── environment.ts           # production: apiUrl '/api/v1', wsUrl '/ws'
    │   └── environment.development.ts
    │
    └── app/
        ├── app.component.ts
        ├── app.config.ts            # provideRouter, provideHttpClient(withInterceptors([...]))
        ├── app.routes.ts            # top-level lazy routes
        │
        ├── core/
        │   ├── auth/
        │   │   ├── auth.service.ts          # login, register, logout, token handling
        │   │   ├── auth.store.ts            # signals: currentUser, isAuthenticated, role
        │   │   ├── auth.interceptor.ts      # attaches Authorization: Bearer <token>
        │   │   ├── auth.guard.ts            # canActivateFn: must be logged in
        │   │   └── role.guard.ts            # canActivateFn: requires ADMIN
        │   ├── http/
        │   │   ├── error.interceptor.ts     # maps ProblemDetail -> ApiError, handles 401
        │   │   └── api-error.ts
        │   ├── realtime/
        │   │   └── equipment-socket.service.ts  # STOMP client, exposes stream per gym
        │   └── layout/
        │       ├── shell.component.ts       # header + router-outlet
        │       └── header.component.ts
        │
        ├── shared/
        │   ├── models/                      # TS interfaces mirroring the API contract
        │   │   ├── user.model.ts
        │   │   ├── gym.model.ts
        │   │   ├── equipment.model.ts
        │   │   ├── session.model.ts
        │   │   └── problem-detail.model.ts
        │   ├── ui/
        │   │   ├── status-badge/            # text label + color for AVAILABLE/LIMITED/FULL
        │   │   ├── loading-spinner/
        │   │   ├── empty-state/
        │   │   └── confirm-dialog/
        │   └── pipes/
        │       └── duration.pipe.ts
        │
        └── features/
            ├── auth/
            │   ├── auth.routes.ts
            │   ├── login/
            │   │   └── login.component.ts
            │   └── register/
            │       └── register.component.ts
            │
            ├── gyms/
            │   ├── gyms.routes.ts
            │   ├── gym.service.ts
            │   └── gym-select/
            │       └── gym-select.component.ts
            │
            ├── equipment/
            │   ├── equipment.routes.ts
            │   ├── equipment.service.ts     # REST calls: list, start, finish
            │   ├── equipment.store.ts       # signal state per gym, merges WebSocket updates
            │   ├── equipment-list/
            │   │   └── equipment-list.component.ts
            │   └── equipment-card/
            │       └── equipment-card.component.ts
            │
            └── admin/
                ├── admin.routes.ts          # guarded by role.guard
                ├── admin-gym-list/
                │   └── admin-gym-list.component.ts
                └── admin-equipment-form/
                    └── admin-equipment-form.component.ts
```