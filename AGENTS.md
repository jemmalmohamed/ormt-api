# Repository Guidelines

## Project Structure & Module Organization
This repository is a Spring Boot API. Application code lives under `src/main/java/ma/org/ormt`, grouped by domain in `modules/<domain>/...` with the usual `controllers`, `services`, `repositories`, `models`, and `dtos` layers. Shared configuration is under `src/main/java/ma/org/ormt/config`. Database migrations live in `src/main/resources/db/migration`, and seeded/auth resources live in `src/main/resources/init-data`. Tests mirror production packages under `src/test/java`.

## Build, Test, and Development Commands
- `./mvnw spring-boot:run` starts the API with the default `dev` profile.
- `./mvnw test` runs the JUnit 5 test suite.
- `./mvnw clean verify` runs a full Maven verification build.
- `./mvnw -DskipTests package` builds the jar without tests.
- `./mvnw -Dspring-boot.run.profiles=test spring-boot:run` is useful when reproducing test-profile behavior locally.

Run commands from the repository root. Prefer the Maven wrapper (`./mvnw`) over a system Maven install.

## Coding Style & Naming Conventions
Follow the existing Java/Spring style:
- 4-space indentation
- `PascalCase` for classes, `camelCase` for methods/fields, `UPPER_SNAKE_CASE` for constants
- package paths remain lowercase and domain-oriented, for example `modules.analytics.domain.controllers.admin`

Keep controllers thin, put business logic in services, and keep DTO mapping close to the DTO classes. For every API change, follow the patterns already established in neighboring modules instead of inventing a new structure. Name Flyway files incrementally using the existing pattern, for example `V1.1.31__Add_New_Field.sql`.

## Testing Guidelines
Tests use Spring Boot Test, JUnit Jupiter, Mockito, and Testcontainers, but do not add or prioritize unit tests by default for routine changes. Name test classes `*Test` and place them in matching packages under `src/test/java` only when test work is explicitly needed. Prefer compile/build verification first, then targeted manual or integration validation when the change is risky.

## Commit & Pull Request Guidelines
Recent history uses Conventional Commit-style subjects such as `feat: ...`. Keep commits short, imperative, and scoped when possible (`feat: add bulk import validation`). PRs should include:
- a concise summary of behavior changes
- linked issue or task reference when available
- migration/config notes if `db/migration` or `application-*.yml` changed
- sample request/response or screenshots for API/admin-facing changes

## Security & Configuration Tips
Do not commit secrets. Use the profile files in `src/main/resources/application-*.yml` as templates only. Review Flyway and seed changes carefully, since they affect shared environments and local resets.
