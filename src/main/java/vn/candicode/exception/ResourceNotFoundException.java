package vn.candicode.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(Class<?> clazz, String field, Object value) {
        super(String.format("%s not found with %s - %s", clazz.getSimpleName(), field, value));
    }
}
