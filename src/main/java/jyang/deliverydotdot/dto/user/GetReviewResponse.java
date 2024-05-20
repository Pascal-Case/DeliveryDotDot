package jyang.deliverydotdot.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import jyang.deliverydotdot.domain.Review;
import jyang.deliverydotdot.domain.ReviewImage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetReviewResponse {

  @Schema(description = "리뷰 ID", example = "1")
  private Long reviewId;

  @Schema(description = "주문 ID", example = "1")
  private Long orderId;

  @Schema(description = "평점", example = "5.0")
  private Double rating;

  @Schema(description = "리뷰 내용", example = "맛있어요")
  private String content;

  @Schema(description = "리뷰 이미지1")
  private String reviewImage1;

  @Schema(description = "리뷰 이미지2")
  private String reviewImage2;

  @Schema(description = "리뷰 이미지3")
  private String reviewImage3;

  public static GetReviewResponse fromEntity(Review review, List<ReviewImage> reviewImages) {

    return GetReviewResponse.builder()
        .reviewId(review.getReviewId())
        .orderId(review.getPurchaseOrder().getPurchaseOrderId())
        .rating(review.getRating())
        .reviewImage1(reviewImages.get(0).getImageUrl())
        .reviewImage2(reviewImages.get(1).getImageUrl())
        .reviewImage3(reviewImages.get(2).getImageUrl())
        .content(review.getContent())
        .build();
  }
}
