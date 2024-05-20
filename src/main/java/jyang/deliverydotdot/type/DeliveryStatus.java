package jyang.deliverydotdot.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DeliveryStatus {
  ASSIGNED("배정 완료"),
  DELIVERING("배달 중"),
  DELIVERED("배달 완료"),
  FAILED("배달 실패"),
  ;

  private final String description;
}
