# Active Context

## Current work focus

Реализация этапа 1: создание задачи через REST API. Вертикальный срез собран end-to-end.

## Recent changes

- Инициализирован Maven-проект Spring Boot 4.1.1; создана структура пакетов согласно `systemPatterns.md`.
- Домен: enum `TaskStatus` (`PLANNED`, `DONE`), `TaskPriority` (`LOW`, `MEDIUM`, `HIGH`);
  модель `Task` (фабрики `of(...)`, `restore(...)`, бизнес-методы, валидация заголовка);
  исключения `InvalidTaskException`, `CompletedTaskModificationException`.
- Миграции Liquibase: `db.changelog-master.xml` и `db.changelog-20261005-2040-create-tasks-table.xml`
  (схема `todo`, таблица `todo.tasks`).
- `application.yaml`: datasource, JPA `ddl-auto: validate`, `hibernate.default_schema: todo`, Liquibase.
- Добавлен `docker-compose.yml` (PostgreSQL 17-alpine, БД/пользователь `todo`).
- Порты: `domain.port.out.TaskRepository`, `domain.port.in.CreateTaskUseCase` + `CreateTaskCommand`.
- Сервис: `application.service.CreateTaskUseCaseImpl` (`@Transactional`).
- Persistence: `TaskJpaEntity`, `TaskJpaRepository`, `TaskMapper`, `TaskPersistenceAdapter`.
- REST: `TaskRestController` (POST `/tasks` → 201), DTO `CreateTaskRequest`/`TaskResponse`,
  `RestExceptionHandler` (`ProblemDetail`).
- Тесты: `CreateTaskUseCaseImplTest` (unit), `BaseIntegrationTest` + `TaskRestControllerIntegrationTest`
  (Testcontainers), обновлён `TodoListApplicationTests`.

## Next steps

### 1. Список задач

1. `ListTasksUseCase` + `ListTasksQuery` в `domain.port.in`.
2. Добавить `List<Task> findAll()` в `TaskRepository` и реализовать в адаптере.
3. Эндпоинт `GET /tasks` в `TaskRestController`.
4. Тесты (unit + интеграционный).

### 2. Изменение приоритета и завершение задачи

1. `ChangeTaskPriorityUseCase`, `CompleteTaskUseCase`.
2. `PUT /tasks/{id}/priority`, `POST /tasks/{id}/complete`.
3. Тесты.

## Active Decisions and Considerations

- Первый срез — только создание задачи (без списка, редактирования, удаления).
- Liquibase 5.x не содержит теги `createSchema`/`schemaExists`, поэтому схема создаётся через `<sql>`.
- Spring Boot 4.1.1: для интеграционных тестов используется `RestTestClient`
  (`TestRestTemplate` требует доп. зависимость `spring-boot-restclient`).
- `docker-compose.yml` ранее упоминался в `techContext.md`, но отсутствовал — добавлен.

## Important Patterns and Preferences

- Следовать правилам из `.cline/rules/` (`architecture.md`, `code-standards.md`,
  `dependency-management.md`, `testing.md`).
- Интеграционные тесты наследуются от `BaseIntegrationTest` (singleton Testcontainers).
