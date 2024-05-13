package jyang.deliverydotdot.dto.partner;

import jyang.deliverydotdot.domain.Partner;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PartnerInfo {

  private String loginId;
  private String name;
  private String email;
  private String phone;
  private String address;

  public static PartnerInfo fromPartner(Partner partner) {
    return PartnerInfo.builder()
        .loginId(partner.getLoginId())
        .name(partner.getName())
        .email(partner.getEmail())
        .phone(partner.getPhone())
        .address(partner.getAddress())
        .build();
  }
}
