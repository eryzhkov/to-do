package reb.ai.todo.infrastructure.adapter.in.rest;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reb.ai.todo.domain.models.Task;
import reb.ai.todo.domain.port.in.CreateTaskUseCase;
import reb.ai.todo.domain.port.in.ListTasksUseCase;
import reb.ai.todo.domain.port.in.command.CreateTaskCommand;
import reb.ai.todo.infrastructure.adapter.in.rest.dto.CreateTaskRequest;
import reb.ai.todo.infrastructure.adapter.in.rest.dto.TaskResponse;

/**
 * REST-контроллер для работы с задачами.
 *
 * <p>Входящий адаптер: зависит только от входящих портов ({@link CreateTaskUseCase},
 * {@link ListTasksUseCase}), но не от реализаций вариантов использования.
 */
@Slf4j
@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskRestController {

    private final CreateTaskUseCase createTaskUseCase;
    private final ListTasksUseCase listTasksUseCase;

    /**
     * Создаёт новую задачу.
     *
     * @param request данные новой задачи
     * @return созданная задача и заголовок {@code Location} с её URI
     */
    @PostMapping
    public ResponseEntity<TaskResponse> create(@Valid @RequestBody CreateTaskRequest request) {
        var command = new CreateTaskCommand(request.title(), request.description());
        Task task = createTaskUseCase.execute(command);
        log.debug("Task {} created via REST", task.getId());
        return ResponseEntity
                .created(URI.create("/tasks/" + task.getId()))
                .body(TaskResponse.from(task));
    }

    /**
     * Возвращает список всех задач: сначала новые.
     *
     * @return список задач; пустой список, если задач нет
     */
    @GetMapping
    public List<TaskResponse> list() {
        List<Task> tasks = listTasksUseCase.execute();
        log.debug("Returning {} tasks via REST", tasks.size());
        return tasks.stream().map(TaskResponse::from).toList();
    }
}
