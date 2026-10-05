package reb.ai.todo.domain.port.out;

import reb.ai.todo.domain.models.Task;

/**
 * Исходящий порт (контракт) хранения задач.
 *
 * <p>Реализация располагается в инфраструктурном слое
 * ({@code infrastructure.adapter.out.persistence}) и подставляется извне, благодаря чему домен
 * не зависит от конкретного хранилища.
 */
public interface TaskRepository {

    /**
     * Сохраняет задачу.
     *
     * @param task задача для сохранения
     * @return сохранённая задача с присвоенным идентификатором
     */
    Task save(Task task);
}
