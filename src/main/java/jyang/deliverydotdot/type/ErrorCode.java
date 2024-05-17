package jyang.deliverydotdot.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
  FORBIDDEN(HttpStatus.FORBIDDEN, "권한이 없습니다."),
  UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "내부 서부 오류가 발생했습니다."),
  INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
  UNPROCESSABLE_ENTITY(HttpStatus.UNPROCESSABLE_ENTITY, "처리할 수 없는 요청입니다."),
  EXTERNAL_API_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "외부 API 서버 오류가 발생했습니다."),

  ALREADY_REGISTERED_LOGIN_ID(HttpStatus.BAD_REQUEST, "이미 등록된 로그인 id 입니다."),
  ALREADY_REGISTERED_EMAIL(HttpStatus.BAD_REQUEST, "이미 등록된 이메일 입니다."),
  ALREADY_REGISTERED_PHONE(HttpStatus.BAD_REQUEST, "이미 등록된 휴대전화 번호 입니다."),
  NO_COORDINATES_FOUND_FOR_ADDRESS(HttpStatus.UNPROCESSABLE_ENTITY, "주소에 대한 좌표를 찾을 수 없습니다."),
  USER_NOT_FOUND(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다."),
  INVALID_DELIVERY_METHOD(HttpStatus.BAD_REQUEST, "유효하지 않은 배달 방법입니다."),
  PARTNER_NOT_FOUND(HttpStatus.NOT_FOUND, "파트너를 찾을 수 없습니다."),
  RIDER_NOT_FOUND(HttpStatus.NOT_FOUND, "라이더를 찾을 수 없습니다."),

  FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다."),
  FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "파일을 찾을 수 없습니다."),
  FILE_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 삭제에 실패했습니다."),

  ALREADY_REGISTERED_REGISTRATION_NUMBER(HttpStatus.BAD_REQUEST, "이미 등록된 사업자 등록 번호 입니다."),
  INVALID_TIME_FORMAT(HttpStatus.BAD_REQUEST, "유효하지 않은 시간 형식입니다."),
  INVALID_HOLIDAY_FORMAT(HttpStatus.BAD_REQUEST, "유효하지 않은 휴일 형식입니다."),
  STORE_CATEGORY_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않는 가게 카테고리입니다."),
  NOT_STORE_OWNER(HttpStatus.BAD_REQUEST, "가게 소유자가 아닙니다."),
  STORE_NOT_FOUND(HttpStatus.BAD_REQUEST, "가게를 찾을 수 없습니다."),
  ;
  private final HttpStatus httpStatus;
  private final String description;
}
