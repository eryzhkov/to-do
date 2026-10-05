package reb.ai.todo.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reb.ai.todo.domain.models.Task;
import reb.ai.todo.domain.models.TaskPriority;
import reb.ai.todo.domain.models.TaskStatus;
import reb.ai.todo.domain.port.in.command.CreateTaskCommand;
import reb.ai.todo.domain.port.out.TaskRepository;

/**
 * Модульные тесты {@link CreateTaskUseCaseImpl}.
 *
 * <p>Проверяют оркестрацию без поднятия Spring-контекста: хранилище замокано.
 */
@ExtendWith(MockitoExtension.class)
class CreateTaskUseCaseImplTest {

    private static final long GENERATED_ID = 42L;

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private CreateTaskUseCaseImpl createTaskUseCase;

    @Test
    @DisplayName("Создание задачи: новая задача сохраняется в статусе PLANNED с приоритетом MEDIUM")
    void shouldCreatePlannedTaskWithMediumPriority_whenCommandIsValid() {
        // given
        var command = new CreateTaskCommand("Buy milk", "2 litres");
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> withGeneratedId(invocation.getArgument(0)));

        // when
        Task result = createTaskUseCase.execute(command);

        // then
        assertThat(result.getId()).isEqualTo(GENERATED_ID);
        assertThat(result.getTitle()).isEqualTo("Buy milk");
        assertThat(result.getDescription()).isEqualTo("2 litres");
        assertThat(result.getStatus()).isEqualTo(TaskStatus.PLANNED);
        assertThat(result.getPriority()).isEqualTo(TaskPriority.MEDIUM);
        assertThat(result.getCreatedAt()).isNotNull();
        assertThat(result.getUpdatedAt()).isNull();
        assertThat(result.getCompletedAt()).isNull();
    }

    @Test
    @DisplayName("Создание задачи: в репозиторий передаются данные из команды")
    void shouldPassCommandDataToRepository_whenExecuted() {
        // given
        var command = new CreateTaskCommand("Buy milk", null);
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> withGeneratedId(invocation.getArgument(0)));

        // when
        createTaskUseCase.execute(command);

        // then
        var taskCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(taskCaptor.capture());
        assertThat(taskCaptor.getValue().getTitle()).isEqualTo("Buy milk");
        assertThat(taskCaptor.getValue().getDescription()).isNull();
        assertThat(taskCaptor.getValue().getId()).isNull();
    }

    private static Task withGeneratedId(Task task) {
        return Task.restore(GENERATED_ID, task.getTitle(), task.getDescription(), task.getStatus(),
                task.getPriority(), task.getCreatedAt(), task.getUpdatedAt(), task.getCompletedAt());
    }
}
