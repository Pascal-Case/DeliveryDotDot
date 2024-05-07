package jyang.deliverydotdot.authentication;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import jyang.deliverydotdot.dto.CommonUserDetails;
import jyang.deliverydotdot.type.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenProvider jwtTokenProvider;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    // 헤더에서 토큰 추출
    String token = parseBearerTokenFromHeader(request);

    // 쿠키에서 토큰 추출
    if (token == null) {
      token = parseBearerTokenFromCookie(request);
    }

    // 토큰이 유효할 때
    if (token != null && !jwtTokenProvider.isExpired(token)) {
      String username = jwtTokenProvider.getUsername(token);
      String role = jwtTokenProvider.getRole(token);

      // 사용자 정보를 이용해 Authentication 객체 생성
      CommonUserDetails user =
          new CommonUserDetails(username, "", UserRole.valueOf(role.toUpperCase()));

      Authentication authentication =
          new UsernamePasswordAuthenticationToken(user, token, user.getAuthorities());
      SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    // 다음 필터로 이동
    filterChain.doFilter(request, response);
  }

  // 헤더에서 Bearer 토큰 추출
  private String parseBearerTokenFromHeader(HttpServletRequest request) {
    return Optional.ofNullable(request.getHeader(HttpHeaders.AUTHORIZATION))
        .filter(token -> token.substring(0, 7).equalsIgnoreCase("Bearer "))
        .map(token -> token.substring(7))
        .orElse(null);
  }

  // 쿠키에서 토큰 추출
  private String parseBearerTokenFromCookie(HttpServletRequest request) {
    Cookie[] cookies = request.getCookies();
    if (cookies == null) {
      return null;
    }
    for (Cookie cookie : cookies) {
      if (cookie.getName().equals("Authorization")) {
        return cookie.getValue();
      }
    }
    return null;
  }
}
