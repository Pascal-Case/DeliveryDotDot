package jyang.deliverydotdot.dto.store;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
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
public class StoreUpdateForm {

  @Schema(description = "가게 ID")
  @NotNull(message = "가게 ID를 입력해 주세요.")
  private Long storeId;

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
