package vn.candicode.exception.handler;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import vn.candicode.exception.TokenExpiredException;

@RestControllerAdvice
@Log4j2
public class SecurityExceptionHandler {
    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public ResponseEntity<?> handleInternalAuthenticationServiceException(
        InternalAuthenticationServiceException exception, WebRequest webRequest) {
        log.error(exception.getLocalizedMessage());

        CandicodeError error = CandicodeError.builder()
            .code(401)
            .message("Unauthorized")
            .exception(exception.getClass().getName())
            .reason(exception.getMessage())
            .path(webRequest.getDescription(false).substring(4))
            .build();

        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<?> handleExpiredJwtException(TokenExpiredException exception, WebRequest webRequest) {
        log.error(exception.getLocalizedMessage());

        CandicodeError error = CandicodeError.builder()
            .code(401)
            .message("Unauthorized")
            .exception(exception.getClass().getName())
            .reason(exception.getMessage())
            .path(webRequest.getDescription(false).substring(4))
            .build();

        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }
}
