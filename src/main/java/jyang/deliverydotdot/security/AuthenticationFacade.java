package jyang.deliverydotdot.security;

import jyang.deliverydotdot.dto.CommonUserDetails;
import jyang.deliverydotdot.dto.oauth2.CustomOAuth2User;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFacade {

  public Authentication getAuthentication() {
    return SecurityContextHolder.getContext().getAuthentication();
  }

  public Object getCurrentUser() {
    Authentication authentication = getAuthentication();
    if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
      return null;
    }
    return authentication.getPrincipal();
  }

  public String getUsername() {
    Object principal = getCurrentUser();
    if (principal instanceof CustomOAuth2User customOAuth2User) {
      return customOAuth2User.getUsername();
    } else if (principal instanceof CommonUserDetails userDetails) {
      return userDetails.getUsername();
    }
    return null;
  }
}
