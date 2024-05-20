package jyang.deliverydotdot.type;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
  FORBIDDEN(HttpStatus.FORBIDDEN, "권한이 없습니다."),
  UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "내부 서부 오류가 발생했습니다."),
  INVALID_REQUEST(BAD_REQUEST, "잘못된 요청입니다."),
  UNPROCESSABLE_ENTITY(HttpStatus.UNPROCESSABLE_ENTITY, "처리할 수 없는 요청입니다."),
  EXTERNAL_API_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "외부 API 서버 오류가 발생했습니다."),

  ALREADY_REGISTERED_LOGIN_ID(BAD_REQUEST, "이미 등록된 로그인 id 입니다."),
  ALREADY_REGISTERED_EMAIL(BAD_REQUEST, "이미 등록된 이메일 입니다."),
  ALREADY_REGISTERED_PHONE(BAD_REQUEST, "이미 등록된 휴대전화 번호 입니다."),
  NO_COORDINATES_FOUND_FOR_ADDRESS(HttpStatus.UNPROCESSABLE_ENTITY, "주소에 대한 좌표를 찾을 수 없습니다."),
  USER_NOT_FOUND(NOT_FOUND, "유저를 찾을 수 없습니다."),
  INVALID_DELIVERY_METHOD(BAD_REQUEST, "유효하지 않은 배달 방법입니다."),
  PARTNER_NOT_FOUND(NOT_FOUND, "파트너를 찾을 수 없습니다."),
  RIDER_NOT_FOUND(NOT_FOUND, "라이더를 찾을 수 없습니다."),

  FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다."),
  FILE_NOT_FOUND(NOT_FOUND, "파일을 찾을 수 없습니다."),
  FILE_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 삭제에 실패했습니다."),

  ALREADY_REGISTERED_REGISTRATION_NUMBER(BAD_REQUEST, "이미 등록된 사업자 등록 번호 입니다."),
  INVALID_TIME_FORMAT(BAD_REQUEST, "유효하지 않은 시간 형식입니다."),
  INVALID_HOLIDAY_FORMAT(BAD_REQUEST, "유효하지 않은 휴일 형식입니다."),
  STORE_CATEGORY_NOT_FOUND(BAD_REQUEST, "존재하지 않는 가게 카테고리입니다."),
  NOT_STORE_OWNER(BAD_REQUEST, "가게 소유자가 아닙니다."),
  STORE_NOT_FOUND(BAD_REQUEST, "가게를 찾을 수 없습니다."),
  MENU_CATEGORY_NOT_FOUND(BAD_REQUEST, "메뉴 카테고리를 찾을 수 없습니다."),
  MENU_CATEGORY_HAS_MENUS(BAD_REQUEST, "메뉴 카테고리에 메뉴가 존재합니다. 메뉴를 먼저 삭제해주세요."),
  INVALID_MENU_CATEGORY_COUNT(BAD_REQUEST, "메뉴 카테고리는 1개 이상 5개 이하로 등록 가능합니다."),

  MENU_NOT_FOUND(BAD_REQUEST, "메뉴를 찾을 수 없습니다."),
  INVALID_CART_ITEM(BAD_REQUEST, "유효하지 않은 장바구니 아이템입니다."),
  CART_ITEM_NOT_SAME_STORE(BAD_REQUEST, "장바구니에는 같은 가게의 메뉴만 추가 가능합니다."),
  MENU_DOES_NOT_BELONG_TO_STORE(BAD_REQUEST, "메뉴가 가게에 속해 있지 않습니다."),
  MENU_CATEGORY_DOES_NOT_BELONG_TO_STORE(BAD_REQUEST, "메뉴 카테고리가 가게에 속해 있지 않습니다."),

  ADDRESS_NOT_FOUND(BAD_REQUEST, "배송지 정보를 찾을 수 없습니다."),
  ADDRESS_NOT_BOUND_TO_USER(BAD_REQUEST, "잘못된 배송지 정보입니다."),

  ONLY_USER_CAN_ORDER(BAD_REQUEST, "주문 요청을 할 수 있는 권한이 없습니다."),
  CART_NOT_FOUND(BAD_REQUEST, "장바구니를 찾을 수 없습니다."),
  STORE_CLOSED(BAD_REQUEST, "영업시간이 아닙니다. 주문을 생성할 수 없습니다."),
  INVALID_QUANTITY(BAD_REQUEST, "유효하지 않은 수량입니다."),
  INVALID_PRICE(BAD_REQUEST, "유효하지 않은 가격입니다."),
  OUT_OF_DELIVERY_AREA(BAD_REQUEST, "배달 가능 지역이 아닙니다."),
  ORDER_NOT_FOUND(BAD_REQUEST, "주문을 찾을 수 없습니다."),
  CAN_NOT_CHANGE_ORDER_STATUS(BAD_REQUEST, "주문 상태를 변경할 수 없습니다."),
  INVALID_LOCATION(BAD_REQUEST, "위치 정보가 유효하지 않습니다."),
  ALREADY_EXIST_DELIVERY(BAD_REQUEST, "이미 배달이 진행 중인 주문입니다."),
  NOT_FOUND_DELIVERY(BAD_REQUEST, "배달 정보를 찾을 수 없습니다."),
  NOT_OWNER_DELIVERY(BAD_REQUEST, "배달 담당자가 아닙니다."),
  INVALID_RATING(BAD_REQUEST, "유효하지 않은 평점입니다."),
  REVIEW_NOT_FOUND(BAD_REQUEST, "리뷰를 찾을 수 없습니다."),
  CAN_NOT_CHANGE_DELIVERY_STATUS(BAD_REQUEST, "배달의 상태를 변경할 수 없습니다.");

  private final HttpStatus httpStatus;
  private final String description;
}
