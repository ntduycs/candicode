package vn.candicode.exception;

public class BadRequestException extends RuntimeException {
    private String reason;

    public BadRequestException(String message) {
        super(message);
    }
}
