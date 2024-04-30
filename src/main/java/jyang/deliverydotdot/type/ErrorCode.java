package jyang.deliverydotdot.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "내부 서부 오류가 발생했습니다."),
  ALREADY_REGISTERED_LOGIN_ID(HttpStatus.BAD_REQUEST, "이미 등록된 로그인 id 입니다."),
  ALREADY_REGISTERED_EMAIL(HttpStatus.BAD_REQUEST, "이미 등록된 이메일 입니다."),
  ALREADY_REGISTERED_PHONE(HttpStatus.BAD_REQUEST, "이미 등록된 휴대전화 번호 입니다."),
  INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다.");
  private final HttpStatus httpStatus;
  private final String description;
}
