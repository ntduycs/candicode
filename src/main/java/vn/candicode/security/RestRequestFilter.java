package vn.candicode.security;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import vn.candicode.utils.TokenUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Log4j2
public class RestRequestFilter extends OncePerRequestFilter {
    private final TokenUtils tokenUtils;

    private final RestUserDetailsService userDetailsService;

    @Autowired
    public RestRequestFilter(TokenUtils tokenUtils, RestUserDetailsService userDetailsService) {
        this.tokenUtils = tokenUtils;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        final String requestMethod = request.getMethod();
        final String requestUri = request.getRequestURI();

        if (requestMethod.equalsIgnoreCase("post")) {
            return requestUri.contains("/auth/login") || requestUri.contains("/coders");
        }

        return false; // indicate that the request should be filtered
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
        throws ServletException, IOException {
        final String token = tokenUtils.getTokenFromRequestHeader(request);

        try {
            if (StringUtils.hasText(token) && tokenUtils.validateToken(token)) {
                String email = tokenUtils.getEmailFromToken(token);

                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
                );

                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (Exception e) {
            log.warn("\n\nCould not set security authentication in the application context. Message - {}\n", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
