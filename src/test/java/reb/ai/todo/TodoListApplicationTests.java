package reb.ai.todo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Smoke-тест: проверяет, что Spring-контекст приложения поднимается.
 *
 * <p>Наследуется от {@link BaseIntegrationTest}, поэтому использует общий контейнер PostgreSQL.
 */
class TodoListApplicationTests extends BaseIntegrationTest {

	@Test
	@DisplayName("Контекст приложения загружается без ошибок")
	void shouldLoadContext_whenApplicationStarts() {
	}

}
