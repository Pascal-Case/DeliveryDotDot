package jyang.deliverydotdot.authentication;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import jyang.deliverydotdot.dto.CommonUserDetails;
import jyang.deliverydotdot.type.UserRole;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class UserLoginFilter extends UsernamePasswordAuthenticationFilter {

  private final AuthenticationManager authenticationManager;

  private final JwtTokenProvider jwtTokenProvider;

  public UserLoginFilter(AuthenticationManager authenticationManager,
      JwtTokenProvider jwtTokenProvider) {

    setFilterProcessesUrl("/api/v1/users/auth/login");
    this.authenticationManager = authenticationManager;
    this.jwtTokenProvider = jwtTokenProvider;
  }

  // 로그인 시도
  @Override
  public Authentication attemptAuthentication(HttpServletRequest request,
      HttpServletResponse response) throws AuthenticationException {
    String identifier = UserRole.ROLE_USER.name() + ":" + obtainUsername(request);
    String password = obtainPassword(request);

    UsernamePasswordAuthenticationToken authenticationToken =
        new UsernamePasswordAuthenticationToken(identifier, password);

    return authenticationManager.authenticate(authenticationToken);
  }

  // 로그인 성공
  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain, Authentication authResult) throws IOException, ServletException {

    CommonUserDetails user = (CommonUserDetails) authResult.getPrincipal();

    String username = user.getUsername();

    Collection<? extends GrantedAuthority> authorities = authResult.getAuthorities();
    GrantedAuthority auth = authorities.iterator().next();

    String role = auth.getAuthority();

    String token = jwtTokenProvider.createToken(username, role);

    response.addHeader("Authorization", "Bearer " + token);
  }

  // 로그인 실패
  @Override
  protected void unsuccessfulAuthentication(HttpServletRequest request,
      HttpServletResponse response, AuthenticationException failed)
      throws IOException, ServletException {

    response.setStatus(401);
  }
}
