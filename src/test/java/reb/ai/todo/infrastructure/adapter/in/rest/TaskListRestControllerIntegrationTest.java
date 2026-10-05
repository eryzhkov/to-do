package reb.ai.todo.infrastructure.adapter.in.rest;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
import reb.ai.todo.infrastructure.adapter.in.rest.dto.TaskResponse;
import reb.ai.todo.infrastructure.adapter.out.persistence.TaskJpaEntity;
import reb.ai.todo.infrastructure.adapter.out.persistence.TaskJpaRepository;

/**
 * Интеграционный тест сценария получения списка задач через REST API.
 *
 * <p>Проверяет полный путь: HTTP-запрос → контроллер → сервис → адаптер → PostgreSQL
 * (Testcontainers). Задачи создаются напрямую через репозиторий с явным {@code createdAt}, чтобы
 * порядок в ответе не зависел от скорости выполнения теста.
 */
class TaskListRestControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private RestTestClient restTestClient;

    @Autowired
    private TaskJpaRepository taskJpaRepository;

    @BeforeEach
    void cleanUp() {
        taskJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("GET /tasks возвращает все задачи: сначала новые")
    void shouldReturnAllTasksSortedByCreatedAtDesc_whenTasksExist() {
        // given
        // PostgreSQL хранит timestamp с точностью до микросекунд, поэтому наносекунды отбрасываем
        Instant now = Instant.now().truncatedTo(ChronoUnit.MICROS);
        taskJpaRepository.save(taskEntity("Oldest", TaskStatus.PLANNED, TaskPriority.LOW, now.minusSeconds(120)));
        taskJpaRepository.save(taskEntity("Middle", TaskStatus.PLANNED, TaskPriority.MEDIUM, now.minusSeconds(60)));
        taskJpaRepository.save(taskEntity("Newest", TaskStatus.DONE, TaskPriority.HIGH, now));

        // when
        RestTestClient.ResponseSpec response = restTestClient.get().uri("/tasks").exchange();
        EntityExchangeResult<TaskResponse[]> result = response.expectBody(TaskResponse[].class).returnResult();

        // then
        assertThat(result.getStatus()).isEqualTo(HttpStatus.OK);

        TaskResponse[] body = result.getResponseBody();
        assertThat(body).isNotNull();
        assertThat(body).extracting(TaskResponse::title).containsExactly("Newest", "Middle", "Oldest");
        assertThat(body[0].status()).isEqualTo(TaskStatus.DONE);
        assertThat(body[0].priority()).isEqualTo(TaskPriority.HIGH);
        assertThat(body[0].createdAt()).isEqualTo(now);
        assertThat(body[0].completedAt()).isNull();
    }

    @Test
    @DisplayName("GET /tasks на пустом хранилище возвращает 200 и пустой массив")
    void shouldReturnEmptyArray_whenNoTasks() {
        // when
        RestTestClient.ResponseSpec response = restTestClient.get().uri("/tasks").exchange();
        EntityExchangeResult<TaskResponse[]> result = response.expectBody(TaskResponse[].class).returnResult();

        // then
        assertThat(result.getStatus()).isEqualTo(HttpStatus.OK);
        assertThat(result.getResponseBody()).isEmpty();
    }

    private static TaskJpaEntity taskEntity(String title, TaskStatus status, TaskPriority priority,
            Instant createdAt) {
        var entity = new TaskJpaEntity();
        entity.setTitle(title);
        entity.setStatus(status);
        entity.setPriority(priority);
        entity.setCreatedAt(createdAt);
        return entity;
    }
}
