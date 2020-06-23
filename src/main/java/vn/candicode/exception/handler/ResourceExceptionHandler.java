package vn.candicode.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import vn.candicode.exception.ResourceNotFoundException;

@RestControllerAdvice
public class ResourceExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    protected ResponseEntity<?> handlePersistenceException(ResourceNotFoundException exception, WebRequest webRequest) {
        CandicodeError error = CandicodeError.builder()
            .code(404)
            .message("Not found")
            .exception(exception.getClass().getName())
            .reason(exception.getMessage())
            .path(webRequest.getDescription(false).substring(4))
            .build();

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
}
