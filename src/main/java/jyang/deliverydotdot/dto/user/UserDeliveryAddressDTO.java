package jyang.deliverydotdot.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jyang.deliverydotdot.domain.UserDeliveryAddress;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserDeliveryAddressDTO {

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class AddAddressRequest {

    @Schema(description = "배송지 주소", example = "서울시 강남구 테헤란로 231")
    @NotBlank(message = "배송지 주소를 입력해 주세요.")
    private String address;

    @Schema(description = "배송지 이름", example = "집")
    @NotBlank(message = "배송지 이름을 입력해 주세요.")
    private String addressName;

    @Schema(description = "기본 배송지 여부", example = "true")
    @NotNull(message = "기본 배송지 여부를 입력해 주세요.")
    private Boolean isDefault;

  }

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class UpdateAddressRequest {

    @Schema(description = "배송지 주소 ID", example = "1")
    @NotNull(message = "배송지 주소 ID를 입력해 주세요.")
    private Long addressId;

    @Schema(description = "배송지 주소", example = "서울시 강남구 테헤란로 231")
    @NotBlank(message = "배송지 주소를 입력해 주세요.")
    private String address;

    @Schema(description = "배송지 이름", example = "집")
    @NotBlank(message = "배송지 이름을 입력해 주세요.")
    private String addressName;

    @Schema(description = "기본 배송지 여부", example = "true")
    @NotNull(message = "기본 배송지 여부를 입력해 주세요.")
    private Boolean isDefault;
  }

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class AddressResponse {

    @Schema(description = "배송지 주소 ID", example = "1")
    private Long addressId;

    @Schema(description = "배송지 주소", example = "서울시 강남구 테헤란로 231")
    private String address;

    @Schema(description = "배송지 이름", example = "집")
    private String addressName;

    @Schema(description = "기본 배송지 여부", example = "true")
    private Boolean isDefault;

    public static AddressResponse fromEntity(UserDeliveryAddress userDeliveryAddress) {
      return AddressResponse.builder()
          .addressId(userDeliveryAddress.getId())
          .address(userDeliveryAddress.getAddress())
          .addressName(userDeliveryAddress.getAddressName())
          .isDefault(userDeliveryAddress.isDefaultAddress())
          .build();
    }
  }

}
