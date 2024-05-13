package jyang.deliverydotdot.dto.rider;

import jyang.deliverydotdot.domain.Rider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RiderInfo {

  private String loginId;
  private String name;
  private String email;
  private String phone;
  private String address;
  private String deliveryMethod;
  private String deliveryRegion;

  public static RiderInfo fromRider(Rider rider) {
    return RiderInfo.builder()
        .loginId(rider.getLoginId())
        .name(rider.getName())
        .email(rider.getEmail())
        .phone(rider.getPhone())
        .address(rider.getAddress())
        .deliveryMethod(rider.getDeliveryMethod().name())
        .deliveryRegion(rider.getDeliveryRegion())
        .build();
  }
}
