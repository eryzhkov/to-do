package reb.ai.todo.domain.port.in;

import java.util.List;
import reb.ai.todo.domain.models.Task;

/**
 * Входящий порт (вариант использования) получения списка задач.
 *
 * <p>Контракт описывает только намерение: вернуть весь список задач. Детали выборки и сортировки
 * скрыты за исходящим портом {@link reb.ai.todo.domain.port.out.TaskRepository}, поэтому домен не
 * зависит от способа хранения.
 */
public interface ListTasksUseCase {

    /**
     * Возвращает все задачи.
     *
     * @return задачи, отсортированные по времени создания (сначала новые); пустой список, если
     *         задач нет
     */
    List<Task> execute();
}
