package statisticsservice.global.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtProcessor {

    @Value("${jwt.secret-key}")
    String secretKey;

    @Value("${jwt.prefix}")
    String prefix;

    @Value("${jwt.header}")
    String header;

    public Claims verifyJwtToken(String jwtToken) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .build()
                .parseClaimsJws(jwtToken)
                .getBody();
    }

    public String extractJwtToken(String jwtHeader) {

        int pos = jwtHeader.lastIndexOf(" ");
        return jwtHeader.substring(pos + 1);
    }

    public String getPrefix() {
        return prefix;
    }

    public String getHeader() {
        return header;
    }
}
