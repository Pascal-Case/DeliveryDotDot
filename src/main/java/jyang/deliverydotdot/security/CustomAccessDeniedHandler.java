package jyang.deliverydotdot.security;

import com.nimbusds.jose.shaded.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import jyang.deliverydotdot.type.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response,
      AccessDeniedException accessDeniedException) throws IOException, ServletException {
    log.error("Access from {} is denied", request.getRemoteAddr());
    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    response.setContentType("application/json;charset=UTF-8");

    JsonObject errorResponse = new JsonObject();
    errorResponse.addProperty("code", ErrorCode.FORBIDDEN.name());
    errorResponse.addProperty("message", ErrorCode.FORBIDDEN.getDescription());

    response.getWriter().print(errorResponse);
  }
}
