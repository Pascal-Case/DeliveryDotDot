package jyang.deliverydotdot.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DeliveryMethod {

  ON_FOOT("도보"),
  BICYCLE("자전거"),
  MOTORCYCLE("오토바이"),
  CAR("자동차");

  private final String description;

}
