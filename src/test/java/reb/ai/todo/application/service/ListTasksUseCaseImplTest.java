package reb.ai.todo.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reb.ai.todo.domain.models.Task;
import reb.ai.todo.domain.models.TaskPriority;
import reb.ai.todo.domain.models.TaskStatus;
import reb.ai.todo.domain.port.out.TaskRepository;

/**
 * Модульные тесты {@link ListTasksUseCaseImpl}.
 *
 * <p>Проверяют оркестрацию без поднятия Spring-контекста: хранилище замокано.
 */
@ExtendWith(MockitoExtension.class)
class ListTasksUseCaseImplTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private ListTasksUseCaseImpl listTasksUseCase;

    @Test
    @DisplayName("Список задач: возвращаются все задачи, порядок из хранилища не меняется")
    void shouldReturnAllTasks_whenRepositoryHasTasks() {
        // given
        var newest = task(2L, "Newest", TaskStatus.DONE, TaskPriority.HIGH, Instant.parse("2026-10-05T12:00:00Z"));
        var oldest = task(1L, "Oldest", TaskStatus.PLANNED, TaskPriority.LOW, Instant.parse("2026-10-05T10:00:00Z"));
        when(taskRepository.findAll()).thenReturn(List.of(newest, oldest));

        // when
        List<Task> result = listTasksUseCase.execute();

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Task::getTitle).containsExactly("Newest", "Oldest");
        assertThat(result).extracting(Task::getId).containsExactly(2L, 1L);
    }

    @Test
    @DisplayName("Список задач: пустое хранилище даёт пустой список")
    void shouldReturnEmptyList_whenRepositoryIsEmpty() {
        // given
        when(taskRepository.findAll()).thenReturn(List.of());

        // when
        List<Task> result = listTasksUseCase.execute();

        // then
        assertThat(result).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("Список задач: хранилище запрашивается ровно один раз")
    void shouldCallRepositoryOnce_whenExecuted() {
        // given
        when(taskRepository.findAll()).thenReturn(List.of());

        // when
        listTasksUseCase.execute();

        // then
        verify(taskRepository).findAll();
    }

    private static Task task(Long id, String title, TaskStatus status, TaskPriority priority,
            Instant createdAt) {
        return Task.restore(id, title, null, status, priority, createdAt, null, null);
    }
}
