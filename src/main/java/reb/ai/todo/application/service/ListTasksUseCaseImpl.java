package reb.ai.todo.application.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reb.ai.todo.domain.models.Task;
import reb.ai.todo.domain.port.in.ListTasksUseCase;
import reb.ai.todo.domain.port.out.TaskRepository;

/**
 * Реализация варианта использования получения списка задач.
 *
 * <p>Вариант использования только читает данные, поэтому транзакция помечена как
 * {@code readOnly}. Транзакция открывается здесь, на уровне варианта использования, а не на
 * уровне отдельного запроса к БД.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ListTasksUseCaseImpl implements ListTasksUseCase {

    private final TaskRepository taskRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Task> execute() {
        List<Task> tasks = taskRepository.findAll();
        log.debug("Found {} tasks", tasks.size());
        return tasks;
    }
}
