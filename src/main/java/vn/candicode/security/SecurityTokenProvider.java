package vn.candicode.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import vn.candicode.exception.TokenExpiredException;
import vn.candicode.util.DatetimeUtils;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Date;

@Component
@Log4j2
public class SecurityTokenProvider {
    private static final String TOKEN_PREFIX = "Bearer ";

    private static final String TOKEN_SECRET = "Candicode";

    /**
     * The token that is generated will have a 1 day long life before expired
     *
     * @param userDetails
     * @return JWT token
     */
    public String generateWebToken(UserDetails userDetails) {
        long now = System.currentTimeMillis();
        Date issuedAt = new Date(now);
        Date expiredAt = new Date(now + 3600 * 1000 * 24 * 10); // ten days

        return Jwts.builder()
            .setSubject(userDetails.getUsername())
            .setIssuedAt(issuedAt)
            .setExpiration(expiredAt)
            .signWith(SignatureAlgorithm.HS512, TOKEN_SECRET)
            .compact();
    }

    /**
     * @param token
     * @return the subject (user's email in this case) included in token
     */
    public String getSubject(String token) {
        return Jwts.parser()
            .setSigningKey(TOKEN_SECRET)
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
    }

    /**
     * @param token
     * @return the expiration of given token
     */
    public LocalDateTime getExpiration(String token) {
        Date expiration = Jwts.parser()
            .setSigningKey(TOKEN_SECRET)
            .parseClaimsJws(token)
            .getBody()
            .getExpiration();

        return DatetimeUtils.asLocalDateTime(expiration);
    }

    public String getTokenType() {
        return "Bearer";
    }

    /**
     * Try to parse to token to validate
     *
     * @param token
     * @return true in case of valid token
     */
    public boolean validated(String token) {
        try {
            Jwts.parser().setSigningKey(TOKEN_SECRET).parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            log.error("JWT error.Exception - {}. Message - {}", e.getClass().getSimpleName(), e.getLocalizedMessage());

            if (e instanceof ExpiredJwtException) {
                throw new TokenExpiredException(e.getMessage());
            }

            throw e;
        }
    }

    /**
     * @param request
     * @return Token extracted from Authorization header or null if not found or has invalid prefix
     */
    public String getToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");

        if (StringUtils.hasText(authorization) && authorization.startsWith(TOKEN_PREFIX)) {
            return authorization.substring(TOKEN_PREFIX.length());
        }

        return null;
    }
}
