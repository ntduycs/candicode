package vn.candicode.exceptions.handlers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import vn.candicode.commons.rest.RestError;
import vn.candicode.exceptions.ForbiddenException;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestControllerAdvice
public class AuthExceptionHandler implements BaseHandler {
    @ExceptionHandler({InternalAuthenticationServiceException.class})
    public ResponseEntity<?> handleInternalAuthenticationServiceException(InternalAuthenticationServiceException ex,
                                                                          WebRequest request) {
        RestError error = new RestError(
            UNAUTHORIZED.value(),
            UNAUTHORIZED.getReasonPhrase(),
            ex.getMessage(),
            getExceptionClassname(ex),
            getRequestURI(request)
        );

        return new ResponseEntity<>(error, UNAUTHORIZED);
    }

    @ExceptionHandler({ForbiddenException.class})
    public ResponseEntity<?> handleForbiddenException(ForbiddenException ex, WebRequest request) {
        RestError error = new RestError(
            FORBIDDEN.value(),
            FORBIDDEN.getReasonPhrase(),
            ex.getLocalizedMessage(),
            getExceptionClassname(ex),
            getRequestURI(request)
        );

        return new ResponseEntity<>(error, FORBIDDEN);
    }
}
