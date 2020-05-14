package vn.candicode.exceptions.handlers;

import org.springframework.web.context.request.WebRequest;

public interface BaseHandler {
    default String getRequestURI(WebRequest request) {
        return request.getDescription(false).substring(4);
    }

    default String getExceptionClassname(Exception e) {
        return e.getClass().getSimpleName();
    }
}
