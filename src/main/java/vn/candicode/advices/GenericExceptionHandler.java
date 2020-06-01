package vn.candicode.advices;

import org.springframework.web.context.request.WebRequest;

public interface GenericExceptionHandler {
    boolean includeClientInfo = false;

    default String getRequestUri(WebRequest webRequest) {
        return webRequest.getDescription(includeClientInfo);
    }

    default String getExceptionName(Exception e) {
        return e.getClass().getSimpleName();
    }
}
