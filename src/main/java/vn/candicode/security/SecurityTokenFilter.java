package vn.candicode.security;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import vn.candicode.exception.TokenExpiredException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Component
@Log4j2
public class SecurityTokenFilter extends OncePerRequestFilter {
    private final SecurityTokenProvider tokenProvider;
    private final UserPrincipalService userPrincipalService;

    private final List<String> URI_WHITELIST = List.of("/api/auth/login", "/api/students");

    public SecurityTokenFilter(SecurityTokenProvider tokenProvider, UserPrincipalService userPrincipalService) {
        this.tokenProvider = tokenProvider;
        this.userPrincipalService = userPrincipalService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        final String token = tokenProvider.getToken(httpServletRequest);

        try {
            if (StringUtils.hasText(token) && tokenProvider.validated(token)) {
                String tokenSubject = tokenProvider.getSubject(token);

                UserDetails details = userPrincipalService.loadUserByEmail(tokenSubject);

                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities());

                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));

                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (Exception e) {
            log.warn("Could not set up security authentication. Exception - {}", e.getClass().getSimpleName());

//            boolean shouldBypass = httpServletRequest.getMethod().equals("POST") && URI_WHITELIST.contains(httpServletRequest.getRequestURI());
//            if (!shouldBypass) {
//                if (e instanceof ExpiredJwtException) {
//                    throw new TokenExpiredException(e.getMessage());
//                }
//            }
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
