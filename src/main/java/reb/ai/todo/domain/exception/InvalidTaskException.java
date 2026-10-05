package reb.ai.todo.domain.exception;

/**
 * Бизнес-исключение: попытка создать задачу в некорректном состоянии (например, с пустым заголовком).
 */
public class InvalidTaskException extends RuntimeException {

    public InvalidTaskException(String message) {
        super(message);
    }
}
