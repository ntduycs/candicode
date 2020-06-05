package vn.candicode.exceptions;

public class FileCannotReadException extends RuntimeException {
    public FileCannotReadException(String message) {
        super(message);
    }
}
