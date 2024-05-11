package jyang.deliverydotdot.oauth2;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import jyang.deliverydotdot.dto.oauth2.CustomOAuth2User;
import jyang.deliverydotdot.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  private final JwtTokenProvider jwtTokenProvider;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {

    CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();

    String username = customUserDetails.getUsername();

    Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
    GrantedAuthority auth = authorities.iterator().next();

    String role = auth.getAuthority();

    String token = jwtTokenProvider.createToken(username, role);

    response.addCookie(createCookie("Authorization", token));
    response.sendRedirect("http://localhost:8080/api/v1/users/my");
  }

  private Cookie createCookie(String key, String value) {
    Cookie cookie = new Cookie(key, value);
    cookie.setMaxAge(60 * 60 * 60);
    //cookie.setSecure(true); // https에서만 쿠키 전송 현재는 http이므로 주석처리
    cookie.setPath("/");
    cookie.setHttpOnly(true);

    return cookie;
  }
}
