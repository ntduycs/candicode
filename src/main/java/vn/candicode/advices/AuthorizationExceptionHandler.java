package vn.candicode.advices;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import vn.candicode.exceptions.TokenExpiredException;
import vn.candicode.exceptions.TokenInvalidException;
import vn.candicode.exceptions.TokenNotFoundException;
import vn.candicode.payloads.GenericError;
import vn.candicode.utils.StatusCodeUtils;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestControllerAdvice
public class AuthorizationExceptionHandler implements GenericExceptionHandler {
    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public ResponseEntity<?> handleInternalAuthenticationServiceException(
        InternalAuthenticationServiceException exception,
        WebRequest webRequest
    ) {
        GenericError error = GenericError.builder()
            .code(StatusCodeUtils.getCode(UNAUTHORIZED))
            .message(StatusCodeUtils.getMessage(UNAUTHORIZED))
            .exception(getExceptionName(exception))
            .reason(exception.getMessage())
            .path(getRequestUri(webRequest))
            .build();

        return new ResponseEntity<>(error, UNAUTHORIZED);
    }

    @ExceptionHandler({TokenExpiredException.class, TokenNotFoundException.class, TokenInvalidException.class})
    public ResponseEntity<?> handleTokenException(Exception exception, WebRequest webRequest) {
        GenericError error = GenericError.builder()
            .code(StatusCodeUtils.getCode(UNAUTHORIZED))
            .message(StatusCodeUtils.getMessage(UNAUTHORIZED))
            .exception(getExceptionName(exception))
            .reason(exception.getMessage())
            .path(getRequestUri(webRequest))
            .build();

        return new ResponseEntity<>(error, UNAUTHORIZED);
    }
}
