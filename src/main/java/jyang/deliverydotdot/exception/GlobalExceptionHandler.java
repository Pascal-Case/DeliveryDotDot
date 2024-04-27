package jyang.deliverydotdot.exception;

import java.util.List;
import java.util.stream.Collectors;
import jyang.deliverydotdot.dto.response.ErrorResponse;
import jyang.deliverydotdot.dto.response.ErrorResponse.ValidationError;
import jyang.deliverydotdot.type.ErrorCode;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  /**
   * handleCustomException 처리 -> 커스텀 에러 처리
   */
  @ExceptionHandler(RestApiException.class)
  public ResponseEntity<Object> handleCustomException(RestApiException e) {
    ErrorCode errorCode = e.getErrorCode();
    return handleExceptionInternal(errorCode);
  }

  /**
   * IllegalArgumentException 처리 -> 적절하지 않은 파라미터
   */
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Object> handleIllegalArgument() {
    ErrorCode errorCode = ErrorCode.INVALID_REQUEST;
    return handleExceptionInternal(errorCode);
  }

  /**
   * DataIntegrityViolationException 처리 -> DB 제약 조건 위반
   */
  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<Object> handleDataIntegrityViolation() {
    ErrorCode errorCode = ErrorCode.INVALID_REQUEST;
    return handleExceptionInternal(errorCode);
  }


  /**
   * MethodArgumentNotValidException 처리 -> @Valid로 걸러지는 예외
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
    ErrorCode errorCode = ErrorCode.INVALID_REQUEST;
    return handleExceptionInternal(e, errorCode);
  }

  /**
   * IllegalStateException 처리 -> 예상치 못한 상태
   */
  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<Object> handleIllegalState() {
    ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
    return handleExceptionInternal(errorCode);
  }

  /**
   * 나머지 모든 예외 처리
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<Object> handleException() {
    ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
    return ResponseEntity
        .status(errorCode.getHttpStatus())
        .body(makeErrorResponseBody(errorCode));
  }

  // ResponseEntity 생성
  private ResponseEntity<Object> handleExceptionInternal(ErrorCode errorCode) {
    return ResponseEntity
        .status(errorCode.getHttpStatus())
        .body(makeErrorResponseBody(errorCode));
  }

  private ResponseEntity<Object> handleExceptionInternal(BindException e, ErrorCode errorCode) {
    return ResponseEntity.status(errorCode.getHttpStatus())
        .body(makeErrorResponseBody(e, errorCode));
  }

  // ErrorResponseBody 생성
  private ErrorResponse makeErrorResponseBody(ErrorCode errorCode) {
    return ErrorResponse.builder()
        .code(errorCode.name())
        .message(errorCode.getDescription())
        .build();
  }

  private ErrorResponse makeErrorResponseBody(BindException e, ErrorCode errorCode) {
    List<ValidationError> validationErrorList = e.getBindingResult()
        .getFieldErrors()
        .stream()
        .map(ErrorResponse.ValidationError::of)
        .collect(Collectors.toList());

    return ErrorResponse.builder()
        .code(errorCode.name())
        .message(errorCode.getDescription())
        .errors(validationErrorList)
        .build();
  }

}
