package jyang.deliverydotdot.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDTO {

  @Schema(description = "주문 ID", example = "1")
  @NotNull(message = "주문 ID를 입력해 주세요.")
  private Long orderId;

  @Schema(description = "평점", example = "5.0")
  @NotNull(message = "평점을 입력해 주세요.")
  private Double rating;

  @Schema(description = "리뷰 내용", example = "맛있어요")
  @Size(min = 10, max = 1000, message = "리뷰 내용은 10자 이상 1000자 이하로 입력해 주세요.")
  private String content;

  @Schema(description = "리뷰 이미지1")
  MultipartFile reviewImage1;

  @Schema(description = "리뷰 이미지2")
  MultipartFile reviewImage2;

  @Schema(description = "리뷰 이미지3")
  MultipartFile reviewImage3;
  
}
