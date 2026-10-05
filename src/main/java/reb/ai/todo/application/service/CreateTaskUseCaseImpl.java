package reb.ai.todo.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reb.ai.todo.domain.models.Task;
import reb.ai.todo.domain.port.in.CreateTaskUseCase;
import reb.ai.todo.domain.port.in.command.CreateTaskCommand;
import reb.ai.todo.domain.port.out.TaskRepository;

/**
 * Реализация варианта использования создания задачи.
 *
 * <p>Оркеструет создание доменной модели и её сохранение. Транзакция открывается здесь, на уровне
 * варианта использования, а не на уровне отдельного запроса к БД.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CreateTaskUseCaseImpl implements CreateTaskUseCase {

    private final TaskRepository taskRepository;

    @Override
    @Transactional
    public Task execute(CreateTaskCommand command) {
        Task task = Task.of(command.title(), command.description());
        Task savedTask = taskRepository.save(task);
        log.debug("Task {} created", savedTask.getId());
        return savedTask;
    }
}
