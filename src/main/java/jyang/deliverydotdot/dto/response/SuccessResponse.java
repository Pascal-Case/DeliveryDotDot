package jyang.deliverydotdot.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class SuccessResponse<T> {

  private final String code;
  private final String message;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private final T data;

  public static <T> SuccessResponse<T> of(String message, T data) {
    return SuccessResponse.<T>builder()
        .code("SUCCESS")
        .message(message)
        .data(data)
        .build();
  }

  public static <T> SuccessResponse<T> of(String message) {
    return SuccessResponse.<T>builder()
        .code("SUCCESS")
        .message(message)
        .build();
  }

  public static <T> SuccessResponse<T> of(T data) {
    return SuccessResponse.<T>builder()
        .code("SUCCESS")
        .message("")
        .data(data)
        .build();
  }
}
