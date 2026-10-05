package reb.ai.todo.domain.models;

/**
 * Статус задачи, отражающий её жизненный цикл.
 */
public enum TaskStatus {

    /** Задача создана и запланирована к выполнению. */
    PLANNED,

    /** Задача выполнена. */
    DONE
}
