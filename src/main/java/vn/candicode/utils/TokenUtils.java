package vn.candicode.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import vn.candicode.configs.AppProperties;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
@Log4j2
public class TokenUtils {
    private static final String TOKEN_PREFIX = "Bearer ";

    private final AppProperties appProperties;

    @Autowired
    public TokenUtils(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    public String createToken(UserDetails user) {
        LocalDateTime issuedAt = LocalDateTime.now();
        LocalDateTime expiredAt = issuedAt.plus(Long.parseLong(appProperties.getJwt().get("max-age")), ChronoUnit.MILLIS);

        log.warn("\n\nGenerating JWT token for user " + user.getUsername() + "\n");

        return Jwts.builder()
            .setSubject(user.getUsername())
            .setIssuedAt(DatetimeUtils.asDate(issuedAt))
            .setExpiration(DatetimeUtils.asDate(expiredAt))
            .signWith(SignatureAlgorithm.HS512, appProperties.getJwt().get("secret"))
            .compact();
    }

    public String getEmailFromToken(final String token) {
        Claims claims = Jwts.parser()
            .setSigningKey(appProperties.getJwt().get("secret"))
            .parseClaimsJws(token)
            .getBody();

        return claims.getSubject();
    }

    public LocalDateTime getExpirationFromToken(final String token) {
        Claims claims = Jwts.parser()
            .setSigningKey(appProperties.getJwt().get("secret"))
            .parseClaimsJws(token)
            .getBody();

        return DatetimeUtils.asLocalDateTime(claims.getExpiration());
    }

    public boolean validateToken(final String token) {
        try {
            Jwts.parser().setSigningKey(appProperties.getJwt().get("secret")).parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("\n\nError when validating token - Message: {}\n", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("\n\nError when validating token - Message: Token not found\n");
        }
        return false;
    }

    @Nullable
    public String getTokenFromRequestHeader(HttpServletRequest request) {
        String header = request.getHeader("Authorization");

        if (StringUtils.hasText(header) && header.startsWith(TOKEN_PREFIX)) {
            return header.substring(TOKEN_PREFIX.length());
        }

        return null;
    }
}
