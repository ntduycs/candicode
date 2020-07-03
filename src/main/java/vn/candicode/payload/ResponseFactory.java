package vn.candicode.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.OK;

public class ResponseFactory {
    @Getter
    @AllArgsConstructor
    public static final class Response {
        private final int code;
        private final String message;
        private final Object result;
    }

    public static Response build(Object result, HttpStatus status) {
        status = status == null ? OK : status;

        return new Response(status.value(), status.getReasonPhrase(), result);
    }

    public static Response build(Object result) {
        return build(result, OK);
    }

}
