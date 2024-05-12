package jyang.deliverydotdot.dto;

import static jyang.deliverydotdot.type.UserRole.ROLE_USER;

import java.util.ArrayList;
import java.util.Collection;
import jyang.deliverydotdot.domain.Partner;
import jyang.deliverydotdot.domain.Rider;
import jyang.deliverydotdot.domain.User;
import jyang.deliverydotdot.type.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


@Getter
@AllArgsConstructor
@Builder
public class CommonUserDetails implements UserDetails {

  private final String username;
  private final String password;
  private final UserRole userRole;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {

    Collection<GrantedAuthority> collection = new ArrayList<>();
    collection.add(userRole::name);

    return collection;
  }

  @Override
  public String getPassword() {
    return this.password;
  }

  @Override
  public String getUsername() {
    return this.username;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  public static CommonUserDetails fromUser(User user) {
    return CommonUserDetails.builder()
        .username(user.getLoginId())
        .password(user.getPassword())
        .userRole(ROLE_USER)
        .build();
  }

  public static CommonUserDetails fromPartner(Partner partner) {
    return CommonUserDetails.builder()
        .username(partner.getLoginId())
        .password(partner.getPassword())
        .userRole(UserRole.ROLE_PARTNER)
        .build();
  }

  public static CommonUserDetails fromRider(Rider rider) {
    return CommonUserDetails.builder()
        .username(rider.getLoginId())
        .password(rider.getPassword())
        .userRole(UserRole.ROLE_RIDER)
        .build();
  }
}
