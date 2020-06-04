package vn.candicode.security;

import lombok.extern.log4j.Log4j2;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Log4j2
public class WebTokenFilter extends OncePerRequestFilter {
    final WebTokenProvider tokenProvider;
    final UserPrincipalService userDetailsService;

    public WebTokenFilter(WebTokenProvider tokenProvider, UserPrincipalService userDetailsService) {
        this.tokenProvider = tokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
        throws ServletException, IOException {
        String token = tokenProvider.getToken(request);

        try {
            if (StringUtils.hasText(token) && tokenProvider.validate(token)) {
                String subject = tokenProvider.getSubject(token);

                UserDetails userDetails = userDetailsService.loadUserByUsername(subject);

                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
                );

                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        } catch (Exception exception) {
            log.warn("Could not set security authentication in the application context. Message - {}", exception.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
