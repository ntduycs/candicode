package vn.candicode.security;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Log4j2
public class RestEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e)
        throws IOException, ServletException {
        log.warn("\n\nUnauthorized error. Message - {}\n", e.getMessage());

        response.setContentType("application/json; charset=UTF-8");
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
    }
}
