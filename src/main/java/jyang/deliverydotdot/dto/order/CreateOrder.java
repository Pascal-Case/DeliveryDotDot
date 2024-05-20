package jyang.deliverydotdot.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class CreateOrder {

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Request {

    @Schema(description = "배달 주소 ID", example = "1")
    private Long deliveryAddressId;

    @Schema(description = "배달 주소", example = "서울시 강남구 역삼동 123-456")
    private String optionalAddress;

    @Schema(description = "연락처", example = "010-1234-5678")
    @Pattern(regexp = "^010-\\d{4}-\\d{4}$", message = "유효하지 않은 휴대전화 번호 입니다.")
    private String optionalPhone;

    @Schema(description = "배달 요청 사항", example = "문 앞에 놓아주세요.")
    private String deliveryRequest;
  }

}
