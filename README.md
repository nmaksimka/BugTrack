# BugTrack

Система трекинга багов в студенческих проектах: проекты и команды, регистрация багов, триаж, исправление и проверка, релизы, отчёты.

Учебный проект по курсу ООП, модуль 3 — реализация по диаграммам модулей 1–2.

## Стек

Java 21 · Spring Boot 3.5 (Web, Security, Data JPA, Validation, Mail) · Thymeleaf · PostgreSQL 17 · Flyway · Docker Compose · Mailpit · JUnit 5 + Testcontainers

## Запуск

Нужны JDK 21+ и Docker Desktop.

```bash
./mvnw spring-boot:run
```

Spring Boot сам поднимет PostgreSQL и Mailpit из `compose.yaml`.

- приложение — http://localhost:8080
- письма (Mailpit) — http://localhost:8025

Демо-пользователи (пароль `password123`):

| Почта | Роль |
|---|---|
| maria@bugtrack.local | руководитель проекта |
| sergey@bugtrack.local | тимлид |
| ivan@bugtrack.local | разработчик |
| anna@bugtrack.local | тестировщик |
| admin@bugtrack.local | администратор системы |

## Тесты

```bash
./mvnw verify
```

## Документы

- [docs/mapping.md](docs/mapping.md) — соответствие диаграмм и кода
- [docs/changes.md](docs/changes.md) — изменения относительно диаграмм и макетов
