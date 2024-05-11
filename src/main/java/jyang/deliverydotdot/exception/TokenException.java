package jyang.deliverydotdot.exception;

import javax.security.sasl.AuthenticationException;
import jyang.deliverydotdot.type.TokenErrorCode;
import lombok.Getter;

@Getter
public class TokenException extends AuthenticationException {

  private final TokenErrorCode errorCode;

  public TokenException(TokenErrorCode tokenErrorCode, Exception e) {
    super(tokenErrorCode.getDescription(), e);
    this.errorCode = tokenErrorCode;
  }
}
