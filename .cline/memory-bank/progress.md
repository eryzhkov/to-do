# Progress

## What Works

- Домен: `TaskStatus`, `TaskPriority`, модель `Task` (фабрики `of(...)`/`restore(...)`, бизнес-методы, валидация).
- Миграции Liquibase: схема `todo` и таблица `todo.tasks`.
- Создание задачи end-to-end: `POST /tasks` → `201 Created`.
- Валидация входного DTO (400) и маппинг доменных ошибок в `ProblemDetail`.
- 15 тестов проходят (`./mvnw test`): 9 доменных + 2 unit сервиса + 3 интеграционных + 1 smoke.

## What's Left to Build

- Список задач (`GET /tasks`).
- Изменение приоритета и завершение задачи.
- Обновление и удаление задач.
- Пользователи и аутентификация.

## Current Status

- Этап 1 (создание задачи) завершён: домен, миграция, порты, сервис, persistence, REST, тесты.
- Готовность MVP: ~35%.

## Known Issues

- Нет.

## Evolution of Project Decisions

- **Решение**: начать с вертикального среза (создание задачи), а не горизонтального.
- **Решение**: домен не зависит от JPA — маппинг вынесен в `TaskMapper`.
- **Решение**: транзакции открываются в `application.service` через `@Transactional`.
- **Решение**: `CreateTaskCommand` вынесена в `domain.port.in.command`, так как пакет `domain.port.in`
  содержит только интерфейсы.
- **Решение**: правила тестирования (`.cline/rules/testing.md`) приведены в соответствие фактическим версиям
  стека: Testcontainers 2.x (не-generic `PostgreSQLContainer` в пакете `org.testcontainers.postgresql`),
  Spring Boot 4.x (`RestTestClient` вместо `TestRestTemplate`), наименование `*IntegrationTest` вместо `*IT`.
- **Решение**: конфликт стиля имён тестовых методов устранён — единый шаблон
  `should[DoSomething]_when[GivenCondition]` зафиксирован и в `code-standards.md`, и в `testing.md`
  (ранее `code-standards.md` требовал `methodName_condition_expectedResult`); существующие тесты уже ему соответствуют.
