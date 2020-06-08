package vn.candicode.advices;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import vn.candicode.exceptions.RegexTemplateNotFoundException;
import vn.candicode.exceptions.UnsupportedFileTypeException;
import vn.candicode.payloads.GenericError;
import vn.candicode.utils.StatusCodeUtils;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestControllerAdvice
public class ValidationExceptionHandler implements GenericExceptionHandler {
    @ExceptionHandler(UnsupportedFileTypeException.class)
    protected ResponseEntity<?> handleUnsupportedFileTypeException(UnsupportedFileTypeException exception, WebRequest webRequest) {
        GenericError error = GenericError.builder()
            .code(StatusCodeUtils.getCode(BAD_REQUEST))
            .message(StatusCodeUtils.getMessage(BAD_REQUEST))
            .reason("File was recognized with MIME type '" + exception.getRejectedFileType() +
                "'. Supported file MIME types are " + exception.getSupportedFileTypes())
            .exception(getExceptionName(exception))
            .path(getRequestUri(webRequest))
            .build();

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(RegexTemplateNotFoundException.class)
    protected ResponseEntity<?> handleRegexTemplateNotFoundException(RegexTemplateNotFoundException exception, WebRequest webRequest) {
        GenericError error = GenericError.builder()
            .code(StatusCodeUtils.getCode(BAD_REQUEST))
            .message(StatusCodeUtils.getMessage(BAD_REQUEST))
            .reason(exception.getMessage())
            .exception(getExceptionName(exception))
            .path(getRequestUri(webRequest))
            .build();

        return ResponseEntity.badRequest().body(error);
    }
}
