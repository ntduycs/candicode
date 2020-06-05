package vn.candicode.exceptions;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String clazz, String searchField, Object searchValue) {
        super(String.format("%s not found with %s = %s", clazz, searchField, searchValue));
    }
}
