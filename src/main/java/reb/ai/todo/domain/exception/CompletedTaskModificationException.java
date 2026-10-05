package reb.ai.todo.domain.exception;

/**
 * Бизнес-исключение: попытка изменить задачу, находящуюся в статусе {@code DONE}.
 *
 * <p>Завершённая задача не может быть отредактирована.
 */
public class CompletedTaskModificationException extends RuntimeException {

    public CompletedTaskModificationException(String message) {
        super(message);
    }
}
