package vn.candicode.advices;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import vn.candicode.exceptions.CodeExecutionException;
import vn.candicode.payloads.GenericError;
import vn.candicode.utils.StatusCodeUtils;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestControllerAdvice
public class CodeExecutionExceptionHandler implements GenericExceptionHandler {
    @ExceptionHandler(CodeExecutionException.class)
    protected ResponseEntity<?> handleCodeExecutionException(CodeExecutionException exception, WebRequest webRequest) {
        GenericError error = GenericError.builder()
            .code(StatusCodeUtils.getCode(INTERNAL_SERVER_ERROR))
            .message(StatusCodeUtils.getMessage(INTERNAL_SERVER_ERROR))
            .exception(getExceptionName(exception))
            .reason(exception.getMessage())
            .path(getRequestUri(webRequest))
            .build();

        return new ResponseEntity<>(error, INTERNAL_SERVER_ERROR);
    }
}
