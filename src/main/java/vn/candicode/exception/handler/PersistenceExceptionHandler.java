package vn.candicode.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import vn.candicode.exception.PersistenceException;

@RestControllerAdvice
public class PersistenceExceptionHandler {
    @ExceptionHandler(PersistenceException.class)
    protected ResponseEntity<?> handlePersistenceException(PersistenceException exception, WebRequest webRequest) {
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
