package jyang.deliverydotdot.security;

import com.nimbusds.jose.shaded.gson.JsonObject;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import jyang.deliverydotdot.exception.TokenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
public class TokenExceptionFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    try {
      filterChain.doFilter(request, response);
    } catch (TokenException e) {
      log.error("Token exception occurred from {}", request.getRemoteAddr());
      response.setStatus(e.getErrorCode().getHttpStatus().value());
      response.setContentType("application/json;charset=UTF-8");

      JsonObject errorResponse = new JsonObject();
      errorResponse.addProperty("code", e.getErrorCode().name());
      errorResponse.addProperty("message", e.getErrorCode().getDescription());

      response.getWriter().print(errorResponse);
    }

  }
}
