package vn.candicode.payloads;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.web.context.request.WebRequest;
import vn.candicode.utils.StatusCodeUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Getter
@Setter
@Builder
public class GenericError implements Serializable {
    public static final long serialVersionUID = 22041101L;

    private int code;
    private String message;
    private String reason;
    private String exception;
    private String path;
    private List<Error> subErrors;

    @Getter
    @AllArgsConstructor
    public static final class Error {
        private final String message;
        private final String reason;
    }

    public static final class Attributes extends DefaultErrorAttributes {
        private static final boolean INCLUDE_EXCEPTION = true;
        private static final boolean INCLUDE_STACKTRACE = false;

        public Attributes() {
            super(INCLUDE_EXCEPTION);
        }

        @Override
        public Map<String, Object> getErrorAttributes(WebRequest webRequest, boolean includeStackTrace) {
            Map<String, Object> defaultAttrs = super.getErrorAttributes(webRequest, INCLUDE_STACKTRACE);

            return Map.of(
                "code", defaultAttrs.getOrDefault("status", StatusCodeUtils.getCode(INTERNAL_SERVER_ERROR)),
                "message", defaultAttrs.getOrDefault("error", StatusCodeUtils.getMessage(INTERNAL_SERVER_ERROR)),
                "reason", defaultAttrs.getOrDefault("message", "Reason unknown"),
                "exception", defaultAttrs.getOrDefault("exception", "Exception unknown"),
                "path", defaultAttrs.getOrDefault("path", "Path unknown")
            );
        }
    }
}
