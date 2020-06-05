package vn.candicode.advices;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import vn.candicode.exceptions.EntityNotFoundException;
import vn.candicode.exceptions.PersistenceException;
import vn.candicode.payloads.GenericError;
import vn.candicode.utils.StatusCodeUtils;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice
public class PersistenceExceptionHandler implements GenericExceptionHandler {
    @ExceptionHandler(PersistenceException.class)
    protected ResponseEntity<?> handlePersistenceException(PersistenceException exception, WebRequest webRequest) {
        GenericError error = GenericError.builder()
            .code(StatusCodeUtils.getCode(BAD_REQUEST))
            .message(StatusCodeUtils.getMessage(BAD_REQUEST))
            .reason(exception.getMessage())
            .exception(getExceptionName(exception))
            .path(getRequestUri(webRequest))
            .build();

        return new ResponseEntity<>(error, BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<?> handleEntityNotFoundException(EntityNotFoundException exception, WebRequest webRequest) {
        GenericError error = GenericError.builder()
            .code(StatusCodeUtils.getCode(NOT_FOUND))
            .message(StatusCodeUtils.getMessage(NOT_FOUND))
            .reason(exception.getMessage())
            .exception(getExceptionName(exception))
            .path(getRequestUri(webRequest))
            .build();

        return new ResponseEntity<>(error, NOT_FOUND);
    }
}
