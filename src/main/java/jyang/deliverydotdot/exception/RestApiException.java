package jyang.deliverydotdot.exception;

import jyang.deliverydotdot.type.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RestApiException extends RuntimeException {

  private final ErrorCode errorCode;
}
