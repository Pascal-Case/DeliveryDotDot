package jyang.deliverydotdot.security;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@ExtendWith(SpringExtension.class)
@SpringBootTest
public class SecurityTest {

  @Autowired
  private JwtTokenProvider jwtTokenProvider;

  @Value("${spring.jwt.secret}")
  private String secretKey;

  @Value("${spring.jwt.expiration}")
  private long expiration;

  @BeforeEach
  void setUp() {
    jwtTokenProvider = new JwtTokenProvider(secretKey, expiration);
  }

  @Test
  void 토큰생성() {
    String username = "user";
    String role = "ROLE_USER";
    String token = jwtTokenProvider.createToken(username, role);

    assertNotNull(token);
    assertEquals(jwtTokenProvider.getUsername(token), username);
    assertEquals(jwtTokenProvider.getRole(token), role);
  }

  @Test
  void 토큰만료확인() {
    String username = "user";
    String role = "ROLE_USER";
    String token = jwtTokenProvider.createToken(username, role);

    assertNotNull(token);
    assertEquals(jwtTokenProvider.getUsername(token), username);
    assertEquals(jwtTokenProvider.getRole(token), role);
    assertEquals(jwtTokenProvider.isExpired(token), false);
  }

}
