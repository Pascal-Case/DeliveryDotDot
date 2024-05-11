package jyang.deliverydotdot.dto.oauth2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import jyang.deliverydotdot.type.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

@RequiredArgsConstructor
public class CustomOAuth2User implements OAuth2User {

  private final OAuth2Response oAuth2Response;
  private final UserRole userRole;

  @Override
  public Map<String, Object> getAttributes() {
    return null;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    Collection<GrantedAuthority> collection = new ArrayList<>();
    collection.add(userRole::name);

    return collection;
  }

  public String getUsername() {
    return oAuth2Response.getProvider() + "_" + oAuth2Response.getProviderId();
  }

  @Override
  public String getName() {
    return oAuth2Response.getName();
  }

  public String getEmail() {
    return oAuth2Response.getEmail();
  }

  public String getPhone() {
    return oAuth2Response.getPhone();
  }

}
