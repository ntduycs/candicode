package vn.candicode.advices;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import vn.candicode.exceptions.PersistenceException;
import vn.candicode.payloads.GenericError;
import vn.candicode.utils.StatusCodeUtils;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

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
}
