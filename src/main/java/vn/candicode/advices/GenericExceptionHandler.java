package vn.candicode.advices;

import org.springframework.web.context.request.WebRequest;

public interface GenericExceptionHandler {
    boolean includeClientInfo = false;

    default String getRequestUri(WebRequest webRequest) {
        return webRequest.getDescription(includeClientInfo).substring(4);
    }

    default String getExceptionName(Exception e) {
        return e.getClass().getSimpleName();
    }
}
