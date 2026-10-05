# Tech Context

## Используемые технологии

- **Java 21** (LTS, используем virtual threads где уместно)
- **Spring Boot 4.1.1** (Spring Framework 7, Jakarta EE 11)
- **PostgreSQL 17.6** (драйвер: org.postgresql:postgresql)
- **Apache Maven** (wrapper: mvnw)
- **Liquibase** (миграции БД)
- **Testcontainers** (интеграционные тесты: PostgreSQL)

## Локальная инфраструктура

- **PostgreSQL**:`localhost:5432`, БД: `todo`, пользователь `todo/todo`, схема `todo`. 
- **Docker Compose**: docker-compose.yml` в корне проекта.

## Настройка окружения разработки

### Сборка 

```shell
./mvnw clean package
```

### Локальный запуск (нужен Docker для PostgreSQL)

```shell
docker-compose up -d
./mvnw spring-boot:run
```

### Тесты

```shell
./mvnw clean test
```

## Технические ограничения

- **Java 21 обязательна**: Spring Boot 4 требует минимум Java 17, но мы используем virtual threads и pattern matching для switch. Код с `--release 17` не соберётся.
- **PostgreSQL 17.6**: Синтаксис MySQL недопустим.
- **Схема БД**: Только через Liquibase. Ручные DDL-запросы запрещены.

## Паттерны использования инструментов

- **Testcontainers**: `@Testcontainers` + `@Container` для PostgreSQL. Контейнер переиспользуется между тестами.
- **Логирование**: SLF4J + Logback.
