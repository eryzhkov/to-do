package reb.ai.todo.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA репозиторий для доступа к таблице {@code todo.tasks}.
 */
public interface TaskJpaRepository extends JpaRepository<TaskJpaEntity, Long> {
}
