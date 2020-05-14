package vn.candicode.exceptions.handlers;

import com.google.common.base.Joiner;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.NonNull;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import vn.candicode.commons.rest.RestError;
import vn.candicode.commons.rest.RestError.SubError;
import vn.candicode.exceptions.BadRequestException;
import vn.candicode.exceptions.ResourceNotFoundException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNSUPPORTED_MEDIA_TYPE;

@RestControllerAdvice
@Log4j2
public class RequestExceptionHandler extends ResponseEntityExceptionHandler implements BaseHandler {
    private List<SubError> getSubErrors(BindingResult bindingResult) {
        List<SubError> subErrors = new ArrayList<>();

        for (FieldError error: bindingResult.getFieldErrors()) {
            subErrors.add(new SubError(
                error.getDefaultMessage(),
                error.getField() + " was given with value " + error.getRejectedValue()
            ));
        }

        for (ObjectError error: bindingResult.getGlobalErrors()) {
            subErrors.add(new SubError(
                error.getDefaultMessage(),
                error.getObjectName() + " was validated failed"
            ));
        }

        return subErrors;
    }

    @Override
    @NonNull
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  @NonNull HttpHeaders headers,
                                                                  @NonNull HttpStatus status,
                                                                  @NonNull WebRequest request) {
        final List<SubError> errors = getSubErrors(ex.getBindingResult());

        return ResponseEntity.badRequest().body(new RestError(
            status.value(),
            status.getReasonPhrase(),
            "Method argument not valid",
            getExceptionClassname(ex),
            getRequestURI(request),
            errors
        ));
    }

    @Override
    @NonNull
    protected ResponseEntity<Object> handleBindException(BindException ex,
                                                         @NonNull HttpHeaders headers,
                                                         @NonNull HttpStatus status,
                                                         @NonNull WebRequest request) {
        final List<SubError> errors = getSubErrors(ex.getBindingResult());

        return ResponseEntity.badRequest().body(new RestError(
            status.value(),
            status.getReasonPhrase(),
            "Binding error",
            getExceptionClassname(ex),
            getRequestURI(request),
            errors
        ));
    }

    @Override
    @NonNull
    protected ResponseEntity<Object> handleTypeMismatch(@NonNull TypeMismatchException ex,
                                                        @NonNull HttpHeaders headers,
                                                        @NonNull HttpStatus status,
                                                        @NonNull WebRequest request) {

        return ResponseEntity.badRequest().body(new RestError(
            status.value(),
            status.getReasonPhrase(),
            "Type mismatch",
            getExceptionClassname(ex),
            getRequestURI(request),
            List.of(new SubError(
                ex.getPropertyName() + " should be of type " + ex.getRequiredType(),
                ex.getPropertyName() + " was given with value " + ex.getValue()
            ))
        ));
    }


    @Override
    @NonNull
    protected ResponseEntity<Object> handleMissingServletRequestParameter(@NonNull MissingServletRequestParameterException ex,
                                                                          @NonNull HttpHeaders headers,
                                                                          @NonNull HttpStatus status,
                                                                          @NonNull WebRequest request) {

        return ResponseEntity.badRequest().body(new RestError(
            status.value(),
            status.getReasonPhrase(),
            "Missing servlet request parameter",
            getExceptionClassname(ex),
            getRequestURI(request),
            List.of(new SubError(
                ex.getParameterName() + " should be given with type " + ex.getParameterType(),
                ex.getParameterName() + "request parameter is missing"
            ))
        ));
    }

    @Override
    @NonNull
    protected ResponseEntity<Object> handleMissingServletRequestPart(@NonNull MissingServletRequestPartException ex,
                                                                     @NonNull HttpHeaders headers,
                                                                     @NonNull HttpStatus status,
                                                                     @NonNull WebRequest request) {
        return ResponseEntity.badRequest().body(new RestError(
            status.value(),
            status.getReasonPhrase(),
            "Missing servlet request part",
            getExceptionClassname(ex),
            getRequestURI(request),
            List.of(new SubError(
                ex.getRequestPartName() + " should be given",
                ex.getRequestPartName() + " request part is missing"
            ))
        ));
    }

    @Override
    @NonNull
    protected ResponseEntity<Object> handleMissingPathVariable(@NonNull MissingPathVariableException ex,
                                                               @NonNull HttpHeaders headers,
                                                               HttpStatus status,
                                                               @NonNull WebRequest request) {

        return ResponseEntity.badRequest().body(new RestError(
            status.value(),
            status.getReasonPhrase(),
            "Missing path variable",
            getExceptionClassname(ex),
            getRequestURI(request),
            List.of(new SubError(
                ex.getVariableName() + " should be given",
                ex.getVariableName() + " path variable is missing"
            ))
        ));
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    protected ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex,
                                                                      HttpStatus status,
                                                                      WebRequest request) {

        return ResponseEntity.badRequest().body(new RestError(
            status.value(),
            status.getReasonPhrase(),
            "Method argument type mismatch",
            getExceptionClassname(ex),
            getRequestURI(request),
            List.of(new SubError(
                ex.getName() + " should be type of " + ex.getRequiredType(),
                ex.getName() + " argument was given with " + ex.getValue()
            ))
        ));
    }

    @ExceptionHandler({ConstraintViolationException.class})
    protected ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex,
                                                               HttpStatus status,
                                                               WebRequest request) {
        final List<SubError> errors = new ArrayList<>();

        for (final ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            errors.add(new SubError(
                violation.getRootBeanClass().getName() + " " + violation.getPropertyPath(),
                violation.getMessage()));
        }

        return ResponseEntity.badRequest().body(new RestError(
            status.value(),
            status.getReasonPhrase(),
            "Request constraint violation",
            getExceptionClassname(ex),
            getRequestURI(request),
            errors
        ));
    }

    @Override
    @NonNull
    protected ResponseEntity<Object> handleHttpMessageNotReadable(@NonNull HttpMessageNotReadableException ex,
                                                                  @NonNull HttpHeaders headers,
                                                                  @NonNull HttpStatus status,
                                                                  @NonNull WebRequest request) {
        return ResponseEntity.badRequest().body(new RestError(
            status.value(),
            status.getReasonPhrase(),
            "Http message not readable",
            getExceptionClassname(ex),
            getRequestURI(request),
            List.of(new SubError(
                "Error when reading http message",
                ex.getLocalizedMessage().substring(0, ex.getLocalizedMessage().indexOf(":"))
            ))
        ));
    }

    @ExceptionHandler({BadRequestException.class})
    protected ResponseEntity<Object> handleBadRequestException(BadRequestException ex,
                                                               HttpStatus status,
                                                               WebRequest request) {
        final RestError RestError = new RestError(
            status.value(),
            status.getReasonPhrase(),
            ex.getMessage(),
            getExceptionClassname(ex),
            getRequestURI(request)
        );

        return ResponseEntity.badRequest().body(RestError);
    }

    // 404 - Not Found exception handlers ====================================================================================

    @Override
    @NonNull
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex,
                                                                   @NonNull HttpHeaders headers,
                                                                   @NonNull HttpStatus status,
                                                                   @NonNull WebRequest request) {

        final RestError RestError = new RestError(
            status.value(),
            status.getReasonPhrase(),
            "No handler found for " + ex.getHttpMethod() + ": " + ex.getRequestURL(),
            getExceptionClassname(ex),
            getRequestURI(request)
        );

        return new ResponseEntity<>(RestError, NOT_FOUND);
    }

    @Override
    @NonNull
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex,
                                                                     @NonNull HttpHeaders headers,
                                                                     @NonNull HttpStatus status,
                                                                     @NonNull WebRequest request) {
        final RestError RestError = new RestError(
            status.value(),
            status.getReasonPhrase(),
            "Supported media types are " + Joiner.on(", ").join(ex.getSupportedMediaTypes()),
            getExceptionClassname(ex),
            getRequestURI(request),
            List.of(new SubError(
                "Content-Type is not supported or not set correctly",
                "Content-Type header given with value - " + ex.getContentType()
            ))
        );

        return new ResponseEntity<>(RestError, UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler({ResourceNotFoundException.class})
    protected ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex,
                                                                     WebRequest request) {
        final RestError RestError = new RestError(
            NOT_FOUND.value(),
            NOT_FOUND.getReasonPhrase(),
            ex.getMessage(),
            getExceptionClassname(ex),
            getRequestURI(request)
        );

        return new ResponseEntity<>(RestError, NOT_FOUND);
    }

    @Override
    @NonNull
    protected ResponseEntity<Object> handleExceptionInternal(@NonNull Exception ex,
                                                             Object body,
                                                             @NonNull HttpHeaders headers,
                                                             @NonNull HttpStatus status,
                                                             @NonNull WebRequest request) {
        log.warn("\n\nThere is an exception that has not been handled correctly\n");
        log.warn("Request URI: " + request.getDescription(false) + "\n");
        log.warn("Message: " + ex.getLocalizedMessage() + "\n");

        final RestError response = new RestError(
            status.value(),
            status.getReasonPhrase(),
            ex.getCause().toString(),
            getExceptionClassname(ex),
            getRequestURI(request)
        );

        return ResponseEntity.badRequest().body(response);
    }
}
