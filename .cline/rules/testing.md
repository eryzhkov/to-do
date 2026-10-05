# Java 21 & Spring Boot 4 Testing Rules

## Общие требования
- Пиши тесты на **JUnit 6** и **AssertJ** (`assertThat(...)`).
- Разделяй модульные (Unit) и интеграционные (Integration) тесты.
- Названия методов: `should[DoSomething]_when[GivenCondition]` (например, `shouldCreateTask_whenRequestIsValid`).

## 1. Модульные тесты (Unit Tests)
- Изолированная проверка Services, Domain Models.
- **Запрещено:** Использовать `@SpringBootTest` или поднимать контекст Spring.
- **Инструменты:** `@ExtendWith(MockitoExtension.class)`, `@Mock`, `@InjectMocks`.

## 2. Интеграционные тесты и Testcontainers
- Используй специализированные слайс-аннотации: `@WebMvcTest` (Web), `@DataJpaTest` (Repositories).
- При тестировании слоя данных или полных сценариев (`@SpringBootTest`) используй **Testcontainers с PostgreSQL**.

### Особенности Testcontainers 2.x (актуальная версия в проекте)
API отличается от 1.x, шаблоны из старых примеров **не компилируются**:
- `PostgreSQLContainer` **не является generic** — пиши `PostgreSQLContainer`, а не `PostgreSQLContainer<?>`;
- класс перенесён в пакет `org.testcontainers.postgresql` (артефакт `org.testcontainers:testcontainers-postgresql`);
- используй конструктор с `DockerImageName` — так версия и тег образа задаются явно;
- версия образа должна совпадать с версией в `docker-compose.yml` (`postgres:17-alpine`).

### Именование классов интеграционных тестов
Surefire не запускает по маске `*IT` (для этого нужен Failsafe plugin, которого в `pom.xml` нет), поэтому интеграционные тесты называй `*IntegrationTest` — иначе они молча не выполняются при `./mvnw test`.

### Шаблон базового класса для Testcontainers (Singleton Pattern)
Для интеграционных тестов, требующих БД, наследуйся от абстрактного класса `BaseIntegrationTest`, который настраивает контейнер один раз. Шаблон инициализации:

```java
package reb.ai.todo;

import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@AutoConfigureRestTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

    static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer(DockerImageName.parse("postgres:17-alpine"));

    static {
        POSTGRES.start();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }
}

```

- **Правило для Cline:** При создании интеграционного теста базы данных, проверяй наличие такого базового класса и наследуйся от него. Не объявляй `@Container` внутри каждого отдельного теста.
- **HTTP-проверки:** для вызовов эндпоинтов используй `RestTestClient`
  (`org.springframework.test.web.servlet.client.RestTestClient`) с `@AutoConfigureRestTestClient`.
  `TestRestTemplate` в Spring Boot 4 требует дополнительной зависимости `spring-boot-restclient`, которой в проекте нет.
  Аннотация `@AutoConfigureRestTestClient` безвредна и для не-HTTP интеграционных тестов.
- **Изоляция данных:** Очищай состояние таблиц между тестами через аннотацию `@Transactional` на уровне тестового класса или точечно через `@Sql`, чтобы тесты не влияли друг на друга.
