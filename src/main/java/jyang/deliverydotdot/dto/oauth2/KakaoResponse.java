package jyang.deliverydotdot.dto.oauth2;

import java.util.Map;
import jyang.deliverydotdot.type.OAuth2Type;

public class KakaoResponse implements OAuth2Response {

  private final Map<String, Object> attribute;

  private final Map<String, Object> kakao_account;

  public KakaoResponse(Map<String, Object> attribute) {
    this.attribute = attribute;
    this.kakao_account = (Map<String, Object>) attribute.get("kakao_account");
  }

  @Override
  public String getProvider() {
    return OAuth2Type.KAKAO.name();
  }

  @Override
  public String getProviderId() {
    return attribute.get("id").toString();
  }

  @Override
  public String getEmail() {
    return kakao_account.get("email").toString();
  }

  @Override
  public String getName() {
    return kakao_account.get("name").toString();
  }

  @Override
  public String getPhone() {
    String phoneNumber = kakao_account.get("phone_number").toString();
    return phoneNumber.replace("+82", "0").replaceAll("\\s+", "");
  }
}
