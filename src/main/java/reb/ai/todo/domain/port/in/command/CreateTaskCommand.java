package reb.ai.todo.domain.port.in.command;

/**
 * Команда создания задачи.
 *
 * <p>Относится к входящему порту, но вынесена в отдельный пакет: пакет {@code domain.port.in}
 * по правилам архитектуры содержит только интерфейсы.
 *
 * @param title       заголовок задачи, обязателен
 * @param description описание задачи, может быть {@code null}
 */
public record CreateTaskCommand(String title, String description) {
}
