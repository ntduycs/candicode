package vn.candicode.utils;

import org.springframework.http.HttpStatus;

public class StatusCodeUtils {
    private StatusCodeUtils() {
    }

    public static int getCode(HttpStatus status) {
        return status.value();
    }

    public static String getMessage(HttpStatus status) {
        return status.getReasonPhrase();
    }
}
