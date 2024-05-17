package jyang.deliverydotdot.dto.store;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StoreRegisterForm {

  @Schema(description = "가게 이름", maxLength = 50, example = "맛집")
  @NotBlank(message = "가게 이름을 입력해 주세요.")
  @Size(max = 50, message = "가게 이름은 50자 이하로 입력해 주세요.")
  private String storeName;

  @Schema(description = "가게 카테고리", example = "1")
  @NotNull(message = "가게 카테고리를 입력해 주세요.")
  private Long storeCategoryId;

  @Schema(description = "가게 주소", maxLength = 100, example = "서울시 강남구 테헤란로 231")
  @NotBlank(message = "가게 주소를 입력해 주세요.")
  @Size(max = 100, message = "가게 주소는 100자 이하로 입력해 주세요.")
  private String storeAddress;

  @Schema(description = "사업자 등록번호", maxLength = 50, example = "123-45-67890")
  @NotBlank(message = "사업자 등록번호를 입력해 주세요.")
  @Size(max = 50, message = "사업자 등록번호는 50자 이하로 입력해 주세요.")
  private String registrationNumber;

  @Schema(description = "휴일", example = "1", minimum = "1", maximum = "7")
  @NotNull(message = "휴일을 입력해 주세요.")
  private Integer holiday;

  @Schema(description = "영업시작 시간", example = "09:00")
  @DateTimeFormat(pattern = "HH:mm")
  private LocalTime openTime;

  @Schema(description = "영업종료 시간", example = "21:00")
  @DateTimeFormat(pattern = "HH:mm")
  private LocalTime closeTime;

  @Schema(description = "마지막 주문 시간", example = "20:30")
  @DateTimeFormat(pattern = "HH:mm")
  private LocalTime lastOrderTime;

  @Schema(description = "가게 사진1")
  private MultipartFile storeImage1;

  @Schema(description = "가게 사진2")
  private MultipartFile storeImage2;

  @Schema(description = "가게 사진3")
  private MultipartFile storeImage3;

  @Schema(description = "가게 소개 글", maxLength = 255, example = "맛있는 음식을 즐기세요.")
  private String description;
}
