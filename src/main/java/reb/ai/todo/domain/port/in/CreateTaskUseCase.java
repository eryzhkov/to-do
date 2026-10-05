package reb.ai.todo.domain.port.in;

import reb.ai.todo.domain.models.Task;
import reb.ai.todo.domain.port.in.command.CreateTaskCommand;

/**
 * Входящий порт (вариант использования) создания задачи.
 */
public interface CreateTaskUseCase {

    /**
     * Создаёт новую задачу и сохраняет её в хранилище.
     *
     * @param command данные новой задачи
     * @return созданная задача с присвоенным идентификатором
     * @throws reb.ai.todo.domain.exception.InvalidTaskException если данные задачи некорректны
     */
    Task execute(CreateTaskCommand command);
}
