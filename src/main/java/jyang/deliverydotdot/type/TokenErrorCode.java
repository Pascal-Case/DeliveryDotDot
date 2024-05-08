package jyang.deliverydotdot.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum TokenErrorCode {
  INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
  EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),
  UNSUPPORTED_TOKEN(HttpStatus.UNAUTHORIZED, "지원되지 않는 토큰입니다."),
  WRONG_TOKEN(HttpStatus.UNAUTHORIZED, "잘못된 토큰입니다."),
  UNKNOWN_TOKEN(HttpStatus.UNAUTHORIZED, "알 수 없는 토큰입니다.");

  private final HttpStatus httpStatus;
  private final String description;

}
