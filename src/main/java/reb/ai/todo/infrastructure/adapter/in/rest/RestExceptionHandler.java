package reb.ai.todo.infrastructure.adapter.in.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reb.ai.todo.domain.exception.CompletedTaskModificationException;
import reb.ai.todo.domain.exception.InvalidTaskException;

/**
 * Обработчик исключений REST-слоя.
 *
 * <p>Преобразует бизнес-исключения домена в HTTP-ответы формата {@link ProblemDetail}
 * (RFC 7807), чтобы технические детали не протекали в домен.
 */
@Slf4j
@RestControllerAdvice
public class RestExceptionHandler {

    /**
     * Обрабатывает попытку создать задачу в некорректном состоянии.
     *
     * @param exception бизнес-исключение
     * @return ответ {@code 400 Bad Request}
     */
    @ExceptionHandler(InvalidTaskException.class)
    public ProblemDetail handleInvalidTask(InvalidTaskException exception) {
        log.debug("Invalid task request: {}", exception.getMessage());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
        problem.setTitle("Invalid task");
        return problem;
    }

    /**
     * Обрабатывает попытку изменить завершённую задачу.
     *
     * @param exception бизнес-исключение
     * @return ответ {@code 409 Conflict}
     */
    @ExceptionHandler(CompletedTaskModificationException.class)
    public ProblemDetail handleCompletedTaskModification(CompletedTaskModificationException exception) {
        log.debug("Completed task modification attempt: {}", exception.getMessage());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());
        problem.setTitle("Completed task cannot be modified");
        return problem;
    }
}
