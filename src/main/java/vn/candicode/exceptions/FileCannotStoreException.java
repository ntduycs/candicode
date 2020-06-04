package vn.candicode.exceptions;

public class FileCannotStoreException extends RuntimeException {
    public FileCannotStoreException(String message) {
        super(message);
    }
}
