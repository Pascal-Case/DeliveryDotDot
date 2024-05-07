package jyang.deliverydotdot.authentication;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

  private final SecretKey secretKey;
  private final long expiration;

  public JwtTokenProvider(
      @Value("${spring.jwt.secret}") String secretKey,
      @Value("${spring.jwt.expiration}") long expiration
  ) {
    this.secretKey = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8),
        Jwts.SIG.HS256.key().build().getAlgorithm());
    this.expiration = expiration;
  }

  public String createToken(String username, String role) {
    long now = System.currentTimeMillis();
    return Jwts.builder()
        .claim("username", username)
        .claim("role", role)
        .issuedAt(new Date(now))
        .expiration(new Date(now + expiration))
        .signWith(secretKey)
        .compact();
  }

  public Claims getClaims(String token) {
    return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
  }

  public String getUsername(String token) {
    return getClaims(token).get("username", String.class);
  }

  public String getRole(String token) {
    return getClaims(token).get("role", String.class);
  }

  public Boolean isExpired(String token) {
    return getClaims(token).getExpiration().before(new Date());
  }

}
