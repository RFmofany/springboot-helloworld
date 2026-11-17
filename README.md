# Spring Boot 3.2.0 + Spring Security Demo

A small Spring Boot 3.2.0 service with:

- `POST /api/auth/login` — username/password login. Demo account: `test` / `123456`.
- `GET /api/hello` — protected by Spring Security, returns the plain text `Hello World`.
- `GET /api/me` — returns the authenticated username.
- `POST /api/auth/logout` — revokes the token.

Authentication uses a short-lived bearer token kept in memory. The login endpoint verifies the password with `BCryptPasswordEncoder`, then issues a random token; `TokenAuthenticationFilter` reads the `Authorization: Bearer <token>` header and populates the `SecurityContext`. Sessions are stateless and CSRF is disabled because there is no cookie-based browser session.

## Run

Java 17+ is required (Spring Boot 3.x). I used JDK 21 locally.

```bash
mvn spring-boot:run
```

The service starts on `http://127.0.0.1:8080`.

## Try it

```bash
curl -i http://127.0.0.1:8080/api/hello
# HTTP/1.1 401

curl -s -X POST http://127.0.0.1:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"123456"}'
# {"tokenType":"Bearer","token":"...","username":"test"}

curl -H "Authorization: Bearer <token>" http://127.0.0.1:8080/api/hello
# Hello World
```

Wrong password returns `401` with `{"error":"invalid username or password"}`.

## Test

```bash
mvn test
```

Result:

```text
Tests run: 4, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

The tests cover: unauthenticated `GET /api/hello` → 401, wrong password → 401, valid login then `GET /api/hello` → `Hello World`, invalid token → 401.
