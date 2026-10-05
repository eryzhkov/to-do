package reb.ai.todo.infrastructure.adapter.in.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO запроса на создание задачи.
 *
 * @param title       заголовок задачи, обязателен
 * @param description описание задачи, может быть {@code null}
 */
public record CreateTaskRequest(

        @NotBlank(message = "title must not be blank")
        @Size(max = 255, message = "title must not exceed 255 characters")
        String title,

        String description) {
}
