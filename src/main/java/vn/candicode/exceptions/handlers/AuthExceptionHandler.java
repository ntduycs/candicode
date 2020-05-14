package vn.candicode.exceptions.handlers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import vn.candicode.commons.rest.RestError;

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
}
