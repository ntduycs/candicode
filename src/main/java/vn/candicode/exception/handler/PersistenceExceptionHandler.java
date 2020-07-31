package vn.candicode.exception.handler;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import vn.candicode.exception.PersistenceException;

@RestControllerAdvice
@Log4j2
public class PersistenceExceptionHandler {
    @ExceptionHandler(PersistenceException.class)
    protected ResponseEntity<?> handlePersistenceException(PersistenceException exception, WebRequest webRequest) {
        log.error(exception.getLocalizedMessage());

        CandicodeError error = CandicodeError.builder()
            .code(400)
            .message("Bad request")
            .exception(exception.getClass().getName())
            .reason(exception.getMessage())
            .path(webRequest.getDescription(false).substring(4))
            .build();

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
