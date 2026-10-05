package reb.ai.todo.domain.models;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reb.ai.todo.domain.exception.CompletedTaskModificationException;
import reb.ai.todo.domain.exception.InvalidTaskException;

@DisplayName("Доменная модель Task")
class TaskTest {

    private static final String TITLE = "Купить молоко";
    private static final String DESCRIPTION = "Два литра, 3,2%";

    @Test
    @DisplayName("of() создаёт задачу в статусе PLANNED со средним приоритетом")
    void shouldCreatePlannedTaskWithMediumPriority_whenTitleIsProvided() {
        Task task = Task.of(TITLE, DESCRIPTION);

        assertThat(task.getId()).isNull();
        assertThat(task.getTitle()).isEqualTo(TITLE);
        assertThat(task.getDescription()).isEqualTo(DESCRIPTION);
        assertThat(task.getStatus()).isEqualTo(TaskStatus.PLANNED);
        assertThat(task.getPriority()).isEqualTo(TaskPriority.MEDIUM);
        assertThat(task.getCreatedAt()).isNotNull();
        assertThat(task.getUpdatedAt()).isNull();
        assertThat(task.getCompletedAt()).isNull();
    }

    @Test
    @DisplayName("of() допускает отсутствие описания")
    void shouldCreateTask_whenDescriptionIsNull() {
        Task task = Task.of(TITLE, null);

        assertThat(task.getDescription()).isNull();
    }

    @Test
    @DisplayName("of() выбрасывает исключение при пустом заголовке")
    void shouldThrowInvalidTaskException_whenTitleIsBlank() {
        assertThatThrownBy(() -> Task.of("   ", DESCRIPTION))
                .isInstanceOf(InvalidTaskException.class);
    }

    @Test
    @DisplayName("of() выбрасывает исключение при null-заголовке")
    void shouldThrowInvalidTaskException_whenTitleIsNull() {
        assertThatThrownBy(() -> Task.of(null, DESCRIPTION))
                .isInstanceOf(InvalidTaskException.class);
    }

    @Test
    @DisplayName("setLowPriority() устанавливает LOW и обновляет updatedAt")
    void shouldSetLowPriorityAndUpdateTimestamp_whenTaskIsPlanned() {
        Task task = Task.of(TITLE, DESCRIPTION);

        task.setLowPriority();

        assertThat(task.getPriority()).isEqualTo(TaskPriority.LOW);
        assertThat(task.getUpdatedAt()).isNotNull();
        assertThat(task.getUpdatedAt()).isAfterOrEqualTo(task.getCreatedAt());
    }

    @Test
    @DisplayName("setMediumPriority() устанавливает MEDIUM и обновляет updatedAt")
    void shouldSetMediumPriorityAndUpdateTimestamp_whenTaskIsPlanned() {
        Task task = Task.of(TITLE, DESCRIPTION);
        task.setHighPriority();

        task.setMediumPriority();

        assertThat(task.getPriority()).isEqualTo(TaskPriority.MEDIUM);
        assertThat(task.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("setHighPriority() устанавливает HIGH и обновляет updatedAt")
    void shouldSetHighPriorityAndUpdateTimestamp_whenTaskIsPlanned() {
        Task task = Task.of(TITLE, DESCRIPTION);

        task.setHighPriority();

        assertThat(task.getPriority()).isEqualTo(TaskPriority.HIGH);
        assertThat(task.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Изменение приоритета завершённой задачи запрещено")
    void shouldThrowCompletedTaskModificationException_whenChangingPriorityOfCompletedTask() {
        Task task = Task.of(TITLE, DESCRIPTION);
        task.done();

        assertThatThrownBy(task::setLowPriority)
                .isInstanceOf(CompletedTaskModificationException.class);
        assertThatThrownBy(task::setMediumPriority)
                .isInstanceOf(CompletedTaskModificationException.class);
        assertThatThrownBy(task::setHighPriority)
                .isInstanceOf(CompletedTaskModificationException.class);
        assertThat(task.getPriority()).isEqualTo(TaskPriority.MEDIUM);
    }

    @Test
    @DisplayName("done() переводит задачу в статус DONE и фиксирует completedAt")
    void shouldCompleteTask_whenTaskIsPlanned() {
        Task task = Task.of(TITLE, DESCRIPTION);

        task.done();

        assertThat(task.getStatus()).isEqualTo(TaskStatus.DONE);
        assertThat(task.getCompletedAt()).isNotNull();
    }
}
