package vn.candicode.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(Class<?> clazz, String field, Object value) {
        super(String.format("%s not found with %s - %s", clazz.getSimpleName(), field, value));
    }

    public ResourceNotFoundException(Class<?> clazz, String field1, Object value1, String field2, Object value2) {
        super(String.format("%s not found with %s - %s and %s - %s", clazz.getSimpleName(), field1, value1, field2, value2));
    }
}
