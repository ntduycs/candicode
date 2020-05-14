package vn.candicode.commons.rest;

import lombok.*;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.web.context.request.WebRequest;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class RestError implements Serializable {
    public static final long serialVersionUID = 1L;

    @NonNull
    private int code;

    @NonNull
    private String message;

    @NonNull
    private String reason;

    @NonNull
    private String exception;

    @NonNull
    private String path;

    private List<SubError> subErrors;

    public static Map<String, Object> transform(Map<String, Object> origin) {
        Map<String, Object> map = new LinkedHashMap<>();

        map.put("code", origin.get("status"));
        map.put("message", origin.getOrDefault("error", "No message available"));
        map.put("reason", origin.getOrDefault("message", "No reason available"));
        map.put("exception", origin.getOrDefault("exception", "No exception available"));
        map.put("path", map.getOrDefault("path", "No path available"));

        return map;
    }

    @Getter
    @AllArgsConstructor
    public static final class SubError {
        private final String message;
        private final String reason;
    }

    public static final class ErrorAttributes extends DefaultErrorAttributes {
        private static final boolean INCLUDE_EXCEPTION = true;
        private static final boolean INCLUDE_STACKTRACE = false;

        public ErrorAttributes() {
            super(INCLUDE_EXCEPTION);
        }

        @Override
        public Map<String, Object> getErrorAttributes(WebRequest webRequest, boolean includeStackTrace) {
            return transform(super.getErrorAttributes(webRequest, INCLUDE_STACKTRACE));
        }
    }
}
