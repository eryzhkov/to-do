package reb.ai.todo.infrastructure.adapter.in.rest.dto;

import java.time.Instant;
import reb.ai.todo.domain.models.Task;
import reb.ai.todo.domain.models.TaskPriority;
import reb.ai.todo.domain.models.TaskStatus;

/**
 * DTO ответа с данными задачи.
 *
 * @param id          идентификатор задачи
 * @param title       заголовок задачи
 * @param description описание задачи
 * @param status      статус задачи
 * @param priority    приоритет задачи
 * @param createdAt   время создания
 * @param updatedAt   время последнего изменения
 * @param completedAt время завершения
 */
public record TaskResponse(
        Long id,
        String title,
        String description,
        TaskStatus status,
        TaskPriority priority,
        Instant createdAt,
        Instant updatedAt,
        Instant completedAt) {

    /**
     * Преобразует доменную модель задачи в DTO ответа.
     *
     * @param task доменная задача
     * @return DTO ответа
     */
    public static TaskResponse from(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                task.getCreatedAt(),
                task.getUpdatedAt(),
                task.getCompletedAt());
    }
}
