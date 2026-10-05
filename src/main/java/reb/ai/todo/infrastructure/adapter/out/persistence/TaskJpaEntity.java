package reb.ai.todo.infrastructure.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import reb.ai.todo.domain.models.TaskPriority;
import reb.ai.todo.domain.models.TaskStatus;

/**
 * JPA-сущность задачи.
 *
 * <p>Инфраструктурный артефакт, отображающий доменную модель {@link reb.ai.todo.domain.models.Task}
 * на таблицу {@code todo.tasks}. Аннотация {@code @ToString} не используется: сущность работает
 * через прокси Hibernate.
 */
@Getter
@Setter
@Entity
@Table(name = "tasks", schema = "todo")
public class TaskJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 255)
    private TaskStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false, length = 255)
    private TaskPriority priority;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "completed_at")
    private Instant completedAt;
}
