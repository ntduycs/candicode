package vn.candicode.advices;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import vn.candicode.exceptions.FileCannotStoreException;
import vn.candicode.exceptions.FileNotFoundException;
import vn.candicode.payloads.GenericError;
import vn.candicode.utils.StatusCodeUtils;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice
public class StorageExceptionHandler implements GenericExceptionHandler {
    @ExceptionHandler(FileNotFoundException.class)
    protected ResponseEntity<?> handleFileNotFoundException(FileNotFoundException exception, WebRequest webRequest) {
        GenericError error = GenericError.builder()
            .code(StatusCodeUtils.getCode(NOT_FOUND))
            .message(StatusCodeUtils.getMessage(NOT_FOUND))
            .reason(exception.getMessage())
            .exception(getExceptionName(exception))
            .path(getRequestUri(webRequest))
            .build();

        return new ResponseEntity<>(error, NOT_FOUND);
    }

    @ExceptionHandler(FileCannotStoreException.class)
    protected ResponseEntity<?> handleFileCannotStoreException(FileCannotStoreException exception, WebRequest webRequest) {
        GenericError error = GenericError.builder()
            .code(StatusCodeUtils.getCode(INTERNAL_SERVER_ERROR))
            .message(StatusCodeUtils.getMessage(INTERNAL_SERVER_ERROR))
            .reason(exception.getMessage())
            .exception(getExceptionName(exception))
            .path(getRequestUri(webRequest))
            .build();

        return new ResponseEntity<>(error, INTERNAL_SERVER_ERROR);
    }
}
