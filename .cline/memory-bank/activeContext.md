# Active Context

## Current work focus

Реализация этапа 2: вывод списка задач через REST API. Вертикальный срез собран end-to-end.

## Recent changes

### Срез «Список задач»

- Домен: входящий порт `domain.port.in.ListTasksUseCase` (без `ListTasksQuery` — см. решения ниже).
- Порт хранилища: `TaskRepository.findAll()` с контрактом сортировки «сначала новые».
- Сервис: `application.service.ListTasksUseCaseImpl` (`@Transactional(readOnly = true)`).
- Persistence: `TaskPersistenceAdapter.findAll()` через `Sort.by(Sort.Direction.DESC, "createdAt")`.
- REST: `GET /tasks` в `TaskRestController` → `200` + JSON-массив `TaskResponse` (пусто → `[]`).
- Тесты: `ListTasksUseCaseImplTest` (3 unit), `TaskListRestControllerIntegrationTest` (2 интеграционных).
- Проверено вживую: `GET /tasks` на пустой БД → `200 []`; после двух `POST /tasks` список вернул
  обе задачи в порядке создания (новые первыми).

### Срез «Создание задачи»

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

### 1. Изменение приоритета и завершение задачи

1. `ChangeTaskPriorityUseCase`, `CompleteTaskUseCase`.
2. `PUT /tasks/{id}/priority`, `POST /tasks/{id}/complete`.
3. Понадобятся `TaskNotFoundException` в `domain.exception` и чтение задачи по идентификатору в
   `TaskRepository`.
4. Тесты (unit + интеграционные).

### 2. Фильтрация и сортировка списка (отложено из среза «Список задач»)

1. `ListTasksQuery` в `domain.port.in.query`: статус, приоритет, поле и направление сортировки.
2. Расширение `TaskRepository.findAll(...)` и параметры запроса `GET /tasks`.
3. Решить вопрос индексов в новой liquibase-миграции (например, `todo.tasks(created_at)`).

## Active Decisions and Considerations

- Первый срез — только создание задачи (без списка, редактирования, удаления).
- Срез «Список задач» сделан минимальным: `GET /tasks` без параметров, сортировка `createdAt DESC`
  зашита в адаптер. `ListTasksQuery` сознательно не создавалась: запись без полей не несёт
  информации, она появится вместе с фильтрами.
- Ответ `GET /tasks` — JSON-массив без обёртки (`{items: [...]}` не вводится): метаданных у
  коллекции нет. Если появится пагинация, обёртка будет добавлена — это ломающее изменение для
  клиента, принято осознанно.
- Сортировка описана в контракте порта (`TaskRepository.findAll()`), но выполняется на стороне БД
  через Spring Data `Sort`: `Pageable`/`Page` в домен не протекают.
- В интеграционных тестах метки времени обрезаются до микросекунд
  (`Instant.now().truncatedTo(ChronoUnit.MICROS)`): PostgreSQL хранит `timestamp with time zone` без
  наносекунд, иначе сравнение `Instant` после round-trip падает.
- Liquibase 5.x не содержит теги `createSchema`/`schemaExists`, поэтому схема создаётся через `<sql>`.
- Spring Boot 4.1.1: для интеграционных тестов используется `RestTestClient`
  (`TestRestTemplate` требует доп. зависимость `spring-boot-restclient`).
- `docker-compose.yml` ранее упоминался в `techContext.md`, но отсутствовал — добавлен.

## Important Patterns and Preferences

- Следовать правилам из `.cline/rules/` (`architecture.md`, `code-standards.md`,
  `dependency-management.md`, `testing.md`).
- Интеграционные тесты наследуются от `BaseIntegrationTest` (singleton Testcontainers).
