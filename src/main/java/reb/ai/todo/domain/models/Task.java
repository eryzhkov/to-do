package reb.ai.todo.domain.models;

import java.time.Instant;

import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import reb.ai.todo.domain.exception.CompletedTaskModificationException;
import reb.ai.todo.domain.exception.InvalidTaskException;

/**
 * Доменная модель задачи.
 *
 * <p>Агрегат, инкапсулирующий бизнес-правила изменения приоритета и завершения задачи.
 * Не содержит JPA-аннотаций: за сохранение отвечает отдельная JPA-сущность инфраструктурного слоя.
 */
@Slf4j
@Getter
@ToString
public class Task {

    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant completedAt;

    private Task() {
    }

    /**
     * Создаёт новую задачу в статусе {@link TaskStatus#PLANNED} и со средним приоритетом.
     *
     * <p>Свойства {@code id}, {@code updatedAt} и {@code completedAt} остаются {@code null}:
     * идентификатор будет присвоен при сохранении в хранилище.
     *
     * @param title       заголовок задачи, обязателен и не может быть пустым
     * @param description описание задачи, необязательно
     * @return новая задача
     * @throws InvalidTaskException если заголовок {@code null} или пустой
     */
    public static Task of(String title, String description) {
        if (title == null || title.isBlank()) {
            throw new InvalidTaskException("Task title must not be blank");
        }
        var task = new Task();
        task.title = title;
        task.description = description;
        task.status = TaskStatus.PLANNED;
        task.priority = TaskPriority.MEDIUM;
        task.createdAt = Instant.now();
        return task;
    }

    /**
     * Восстанавливает задачу из персистентного состояния.
     *
     * <p>В отличие от {@link #of(String, String)} принимает все свойства, включая идентификатор и
     * временные метки. Используется инфраструктурным слоем при чтении задачи из хранилища.
     *
     * @param id          идентификатор задачи
     * @param title       заголовок задачи
     * @param description описание задачи, может быть {@code null}
     * @param status      статус задачи
     * @param priority    приоритет задачи
     * @param createdAt   время создания
     * @param updatedAt   время последнего изменения, может быть {@code null}
     * @param completedAt время завершения, может быть {@code null}
     * @return восстановленная задача
     */
    public static Task restore(Long id, String title, String description, TaskStatus status,
            TaskPriority priority, Instant createdAt, Instant updatedAt, Instant completedAt) {
        var task = new Task();
        task.id = id;
        task.title = title;
        task.description = description;
        task.status = status;
        task.priority = priority;
        task.createdAt = createdAt;
        task.updatedAt = updatedAt;
        task.completedAt = completedAt;
        return task;
    }

    /**
     * Понижает приоритет задачи до {@link TaskPriority#LOW} и обновляет время изменения.
     *
     * @throws CompletedTaskModificationException если задача уже завершена
     */
    public void setLowPriority() {
        changePriority(TaskPriority.LOW);
    }

    /**
     * Устанавливает средний приоритет задачи {@link TaskPriority#MEDIUM} и обновляет время изменения.
     *
     * @throws CompletedTaskModificationException если задача уже завершена
     */
    public void setMediumPriority() {
        changePriority(TaskPriority.MEDIUM);
    }

    /**
     * Повышает приоритет задачи до {@link TaskPriority#HIGH} и обновляет время изменения.
     *
     * @throws CompletedTaskModificationException если задача уже завершена
     */
    public void setHighPriority() {
        changePriority(TaskPriority.HIGH);
    }

    /**
     * Завершает задачу: переводит статус из {@link TaskStatus#PLANNED} в {@link TaskStatus#DONE}
     * и фиксирует время завершения.
     */
    public void done() {
        this.status = TaskStatus.DONE;
        this.completedAt = Instant.now();
        log.debug("Task completed at {}", completedAt);
    }

    private void changePriority(TaskPriority newPriority) {
        if (status == TaskStatus.DONE) {
            throw new CompletedTaskModificationException("Completed task priority cannot be changed");
        }
        this.priority = newPriority;
        this.updatedAt = Instant.now();
        log.debug("Task priority changed to {}", newPriority);
    }
}
