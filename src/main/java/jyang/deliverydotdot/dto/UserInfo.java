package jyang.deliverydotdot.dto;

import jyang.deliverydotdot.domain.User;
import jyang.deliverydotdot.type.AuthType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserInfo {

  private String loginId;
  private String name;
  private String email;
  private String phone;
  private String address;
  private AuthType authType;


  public static UserInfo fromUser(User user) {
    return UserInfo.builder()
        .loginId(user.getLoginId())
        .name(user.getName())
        .email(user.getEmail())
        .phone(user.getPhone())
        .address(user.getAddress())
        .authType(user.getAuthType())
        .build();
  }
}
