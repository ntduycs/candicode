package vn.candicode.exception.handler;

import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import vn.candicode.exception.BadRequestException;
import vn.candicode.exception.handler.CandicodeError.Details;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestControllerAdvice
public class BadRequestExceptionHandler extends ResponseEntityExceptionHandler {
    @Override
    @NonNull
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
        @NonNull MethodArgumentNotValidException exception, @NonNull HttpHeaders headers,
        @NonNull HttpStatus status, @NonNull WebRequest request) {

        final List<Details> detailErrors = new ArrayList<>();
        for (final FieldError error : exception.getBindingResult().getFieldErrors()) {
            detailErrors.add(new Details(
                error.getDefaultMessage(),
                error.getField() + " was given with value " + error.getRejectedValue()
            ));
        }
        for (final ObjectError error : exception.getBindingResult().getGlobalErrors()) {
            detailErrors.add(new Details(
                error.getDefaultMessage(),
                error.getDefaultMessage()
            ));
        }

        CandicodeError error = CandicodeError.builder()
            .code(400)
            .message("Bad request")
            .exception(exception.getClass().getName())
            .reason("Some parameters was failed to validate")
            .path(request.getDescription(false).substring(4))
            .errors(detailErrors)
            .build();

        return new ResponseEntity<>(error, BAD_REQUEST);
    }

    @Override
    @NonNull
    protected ResponseEntity<Object> handleBindException(@NonNull BindException ex, @NonNull HttpHeaders headers, @NonNull HttpStatus status, @NonNull WebRequest request) {
        final List<Details> detailErrors = new ArrayList<>();
        for (final FieldError error : ex.getBindingResult().getFieldErrors()) {
            String reason;

            if (error.getRejectedValue() == null) {
                reason = error.getField() + " was given with value " + error.getRejectedValue();
            } else if (error.getRejectedValue().getClass().isPrimitive() || error.getRejectedValue() instanceof String) {
                reason = error.getField() + " was given with value " + error.getRejectedValue();
            } else {
                reason = error.getField() + " was given with invalid value";
            }

            detailErrors.add(new Details(error.getDefaultMessage(), reason));
        }
        for (final ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            detailErrors.add(new Details(
                error.getDefaultMessage(),
                error.getObjectName() + " was given with value that cannot be bound"
            ));
        }

        CandicodeError error = CandicodeError.builder()
            .code(400)
            .message("Bad request")
            .exception(ex.getClass().getName())
            .reason("Failed to validate request body")
            .path(request.getDescription(false).substring(4))
            .errors(detailErrors)
            .build();

        return new ResponseEntity<>(error, BAD_REQUEST);
    }

    @Override
    @NonNull
    protected ResponseEntity<Object> handleTypeMismatch(
        TypeMismatchException ex, @NonNull HttpHeaders headers, @NonNull HttpStatus status, WebRequest request) {

        CandicodeError error = CandicodeError.builder()
            .code(400)
            .message("Bad request")
            .exception(ex.getClass().getName())
            .reason(ex.getMostSpecificCause().getMessage())
            .path(request.getDescription(false).substring(4))
            .errors(List.of(new Details(
                ex.getPropertyName() + " should be of type " + ex.getRequiredType(),
                ex.getPropertyName() + " was given with value " + ex.getValue()
            )))
            .build();

        return new ResponseEntity<>(error, BAD_REQUEST);
    }


    @Override
    @NonNull
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
        MissingServletRequestParameterException ex, @NonNull HttpHeaders headers, @NonNull HttpStatus status, WebRequest request) {

        CandicodeError error = CandicodeError.builder()
            .code(400)
            .message("Bad request")
            .exception(ex.getClass().getName())
            .reason("Servlet request parameter(s) is missing")
            .path(request.getDescription(false).substring(4))
            .errors(List.of(new Details(
                ex.getParameterName() + " should be of type " + ex.getParameterType(),
                ex.getParameterName() + " is missing"
            )))
            .build();

        return new ResponseEntity<>(error, BAD_REQUEST);
    }

    @Override
    @NonNull
    protected ResponseEntity<Object> handleMissingServletRequestPart(
        MissingServletRequestPartException ex, @NonNull HttpHeaders headers, @NonNull HttpStatus status, WebRequest request) {

        CandicodeError error = CandicodeError.builder()
            .code(400)
            .message("Bad request")
            .exception(ex.getClass().getName())
            .reason("Servlet request part(s) is missing")
            .path(request.getDescription(false).substring(4))
            .errors(List.of(new Details(
                ex.getRootCause().getMessage(),
                ex.getRequestPartName() + " is missing"
            )))
            .build();

        return new ResponseEntity<>(error, BAD_REQUEST);
    }

    @Override
    @NonNull
    protected ResponseEntity<Object> handleMissingPathVariable(
        MissingPathVariableException ex, @NonNull HttpHeaders headers, @NonNull HttpStatus status, WebRequest request) {

        CandicodeError error = CandicodeError.builder()
            .code(400)
            .message("Bad request")
            .exception(ex.getClass().getName())
            .reason("Path variable(s) is missing")
            .path(request.getDescription(false).substring(4))
            .errors(List.of(new Details(
                ex.getRootCause().getMessage(),
                ex.getVariableName() + " is missing"
            )))
            .build();

        return new ResponseEntity<>(error, BAD_REQUEST);
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    protected ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex, WebRequest request) {

        CandicodeError error = CandicodeError.builder()
            .code(400)
            .message("Bad request")
            .exception(ex.getClass().getName())
            .reason("Types of method arguments are currently mismatch")
            .path(request.getDescription(false).substring(4))
            .errors(List.of(new Details(
                "'" + ex.getName() + "' should be type of " + ex.getRequiredType(),
                "'" + ex.getName() + "' parameter was given with " + ex.getValue()
            )))
            .build();

        return new ResponseEntity<>(error, BAD_REQUEST);
    }

    @ExceptionHandler({ConstraintViolationException.class})
    protected ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {

        final List<Details> errors = new ArrayList<>();
        for (final ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            errors.add(new Details(
                violation.getMessage(),
                violation.getRootBeanClass().getName() + " " + violation.getPropertyPath()
            ));
        }

        CandicodeError error = CandicodeError.builder()
            .code(400)
            .message("Bad request")
            .exception(ex.getClass().getName())
            .reason("Failed to validate request body")
            .path(request.getDescription(false).substring(4))
            .errors(errors)
            .build();

        return new ResponseEntity<>(error, BAD_REQUEST);
    }

    @ExceptionHandler(BadRequestException.class)
    protected ResponseEntity<?> handlePersistenceException(BadRequestException exception, WebRequest webRequest) {
        CandicodeError error = CandicodeError.builder()
            .code(400)
            .message("Bad request")
            .exception(exception.getClass().getName())
            .reason(exception.getMessage())
            .path(webRequest.getDescription(false).substring(4))
            .build();

        return new ResponseEntity<>(error, BAD_REQUEST);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    protected ResponseEntity<?> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex, WebRequest request) {
        CandicodeError error = CandicodeError.builder()
            .code(400)
            .message("Bad request")
            .exception(ex.getClass().getName())
            .reason(ex.getMostSpecificCause().getMessage())
            .path(request.getDescription(false).substring(4))
            .build();

        return new ResponseEntity<>(error, BAD_REQUEST);
    }
}
