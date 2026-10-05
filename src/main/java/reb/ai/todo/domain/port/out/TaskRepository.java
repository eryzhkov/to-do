package reb.ai.todo.domain.port.out;

import java.util.List;
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

    /**
     * Возвращает все задачи, отсортированные по времени создания (сначала новые).
     *
     * @return список задач; пустой список, если задач нет
     */
    List<Task> findAll();
}
