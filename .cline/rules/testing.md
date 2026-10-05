# Java 21 & Spring Boot 4 Testing Rules

## Общие требования
- Пиши тесты на **JUnit 6** и **AssertJ** (`assertThat(...)`).
- Разделяй модульные (Unit) и интеграционные (Integration) тесты.
- Названия методов: `should[DoSomething]_when[GivenCondition]`.

## 1. Модульные тесты (Unit Tests)
- Изолированная проверка Services, Domain Models.
- **Запрещено:** Использовать `@SpringBootTest` или поднимать контекст Spring.
- **Инструменты:** `@ExtendWith(MockitoExtension.class)`, `@Mock`, `@InjectMocks`.

## 2. Интеграционные тесты и Testcontainers
- Используй специализированные слайс-аннотации: `@WebMvcTest` (Web), `@DataJpaTest` (Repositories).
- При тестировании слоя данных или полных сценариев (`@SpringBootTest`) используй **Testcontainers с PostgreSQL**.

### Шаблон базового класса для Testcontainers (Singleton Pattern)
Для интеграционных тестов, требующих БД, Cline должен наследоваться от абстрактного класса (например, `BaseIntegrationTest`), который настраивает контейнер один раз. Шаблон инициализации:

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    static {
        postgres.start();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
}

```

- **Правило для Cline:** При создании интеграционного теста базы данных, проверяй наличие такого базового класса и наследуйся от него. Не объявляй `@Container` внутри каждого отдельного теста.
- **Изоляция данных:** Очищай состояние таблиц между тестами через аннотацию `@Transactional` на уровне тестового класса или точечно через `@Sql`, чтобы тесты не влияли друг на друга.
