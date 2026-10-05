package reb.ai.todo.infrastructure.adapter.in.rest;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.client.EntityExchangeResult;
import org.springframework.test.web.servlet.client.RestTestClient;
import reb.ai.todo.BaseIntegrationTest;
import reb.ai.todo.domain.models.TaskPriority;
import reb.ai.todo.domain.models.TaskStatus;
import reb.ai.todo.infrastructure.adapter.in.rest.dto.CreateTaskRequest;
import reb.ai.todo.infrastructure.adapter.in.rest.dto.TaskResponse;
import reb.ai.todo.infrastructure.adapter.out.persistence.TaskJpaRepository;

/**
 * Интеграционный тест сценария создания задачи через REST API.
 *
 * <p>Проверяет полный путь: HTTP-запрос → контроллер → сервис → адаптер → PostgreSQL (Testcontainers).
 */
class TaskRestControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private RestTestClient restTestClient;

    @Autowired
    private TaskJpaRepository taskJpaRepository;

    @BeforeEach
    void cleanUp() {
        taskJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("POST /tasks создаёт задачу, возвращает 201 и сохраняет её в БД")
    void shouldCreateTask_whenRequestIsValid() {
        // given
        var request = new CreateTaskRequest("Write tests", "Cover create flow");

        // when
        RestTestClient.ResponseSpec response = restTestClient.post()
                .uri("/tasks")
                .body(request)
                .exchange();
        EntityExchangeResult<TaskResponse> result = response.expectBody(TaskResponse.class).returnResult();

        // then
        assertThat(result.getStatus()).isEqualTo(HttpStatus.CREATED);

        TaskResponse body = result.getResponseBody();
        assertThat(body).isNotNull();
        assertThat(body.id()).isNotNull();
        assertThat(body.title()).isEqualTo("Write tests");
        assertThat(body.description()).isEqualTo("Cover create flow");
        assertThat(body.status()).isEqualTo(TaskStatus.PLANNED);
        assertThat(body.priority()).isEqualTo(TaskPriority.MEDIUM);
        assertThat(body.createdAt()).isNotNull();
        assertThat(body.updatedAt()).isNull();
        assertThat(body.completedAt()).isNull();
        assertThat(result.getResponseHeaders().getLocation()).hasPath("/tasks/" + body.id());
        assertThat(taskJpaRepository.findById(body.id())).isPresent();
    }

    @Test
    @DisplayName("POST /tasks с пустым заголовком возвращает 400 и не сохраняет задачу")
    void shouldReturnBadRequest_whenTitleIsBlank() {
        // given
        var request = new CreateTaskRequest("   ", null);

        // when
        RestTestClient.ResponseSpec response = restTestClient.post().uri("/tasks").body(request).exchange();

        // then
        response.expectStatus().isBadRequest();
        assertThat(taskJpaRepository.count()).isZero();
    }

    @Test
    @DisplayName("POST /tasks без заголовка возвращает 400")
    void shouldReturnBadRequest_whenTitleIsMissing() {
        // given
        Map<String, Object> request = Map.of("description", "no title");

        // when
        RestTestClient.ResponseSpec response = restTestClient.post().uri("/tasks").body(request).exchange();

        // then
        response.expectStatus().isBadRequest();
        assertThat(taskJpaRepository.count()).isZero();
    }
}
