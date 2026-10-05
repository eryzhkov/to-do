package reb.ai.todo.infrastructure.adapter.out.persistence;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import reb.ai.todo.domain.models.Task;
import reb.ai.todo.domain.port.out.TaskRepository;

/**
 * Адаптер исходящего порта {@link TaskRepository} поверх Spring Data JPA.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TaskPersistenceAdapter implements TaskRepository {

    private final TaskJpaRepository taskJpaRepository;
    private final TaskMapper taskMapper;

    @Override
    public Task save(Task task) {
        TaskJpaEntity savedEntity = taskJpaRepository.save(taskMapper.toEntity(task));
        log.debug("Task persisted with id {}", savedEntity.getId());
        return taskMapper.toDomain(savedEntity);
    }

    @Override
    public List<Task> findAll() {
        return taskJpaRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt")).stream()
                .map(taskMapper::toDomain)
                .toList();
    }
}
