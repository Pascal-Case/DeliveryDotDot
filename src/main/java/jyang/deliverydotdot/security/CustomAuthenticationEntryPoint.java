package jyang.deliverydotdot.security;

import com.nimbusds.jose.shaded.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import jyang.deliverydotdot.type.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException authException) throws IOException, ServletException {
    log.error("Authentication failed from {}", request.getRemoteAddr());
    response.setContentType("application/json;charset=UTF-8");
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

    JsonObject errorResponse = new JsonObject();
    errorResponse.addProperty("code", ErrorCode.UNAUTHORIZED.name());
    errorResponse.addProperty("message", ErrorCode.UNAUTHORIZED.getDescription());
    
    response.getWriter().print(errorResponse);
  }
}
