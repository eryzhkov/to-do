package reb.ai.todo.infrastructure.adapter.out.persistence;

import org.springframework.stereotype.Component;
import reb.ai.todo.domain.models.Task;

/**
 * Маппер между доменной моделью {@link Task} и JPA-сущностью {@link TaskJpaEntity}.
 *
 * <p>Изолирует детали ORM от домена: домен ничего не знает о JPA, а инфраструктура — о внутреннем
 * устройстве агрегата.
 */
@Component
public class TaskMapper {

    /**
     * Преобразует доменную модель в JPA-сущность.
     *
     * @param task доменная задача
     * @return JPA-сущность
     */
    public TaskJpaEntity toEntity(Task task) {
        TaskJpaEntity entity = new TaskJpaEntity();
        entity.setId(task.getId());
        entity.setTitle(task.getTitle());
        entity.setDescription(task.getDescription());
        entity.setStatus(task.getStatus());
        entity.setPriority(task.getPriority());
        entity.setCreatedAt(task.getCreatedAt());
        entity.setUpdatedAt(task.getUpdatedAt());
        entity.setCompletedAt(task.getCompletedAt());
        return entity;
    }

    /**
     * Преобразует JPA-сущность в доменную модель.
     *
     * @param entity JPA-сущность
     * @return доменная задача
     */
    public Task toDomain(TaskJpaEntity entity) {
        return Task.restore(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getStatus(),
                entity.getPriority(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getCompletedAt());
    }
}
