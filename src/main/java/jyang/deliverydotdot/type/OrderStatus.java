package jyang.deliverydotdot.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStatus {
  PENDING("주문 대기 중"),
  APPROVED("주문 승인 완료"),
  REJECTED("주문 거절"),
  COOKING("조리 중"),
  COOKED("조리 완료"),
  CANCELED("주문 취소");

  private final String description;
}
