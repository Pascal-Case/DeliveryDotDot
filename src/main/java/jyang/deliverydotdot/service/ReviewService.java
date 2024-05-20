package jyang.deliverydotdot.service;

import java.util.List;
import jyang.deliverydotdot.domain.Partner;
import jyang.deliverydotdot.domain.PurchaseOrder;
import jyang.deliverydotdot.domain.Review;
import jyang.deliverydotdot.domain.ReviewImage;
import jyang.deliverydotdot.domain.User;
import jyang.deliverydotdot.dto.user.GetReviewResponse;
import jyang.deliverydotdot.dto.user.ReviewDTO;
import jyang.deliverydotdot.exception.RestApiException;
import jyang.deliverydotdot.repository.ReviewImageRepository;
import jyang.deliverydotdot.repository.ReviewRepository;
import jyang.deliverydotdot.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

  private final ReviewRepository reviewRepository;

  private final ReviewImageRepository reviewImageRepository;

  private final OrderService orderService;

  private final S3Service s3Service;

  private final StoreService storeService;

  @Transactional
  public void createReview(User user, Long orderId, ReviewDTO reviewDTO) {
    PurchaseOrder order = orderService.getOrderById(orderId);

    if (!order.getUser().getUserId().equals(user.getUserId())) {
      throw new RestApiException(ErrorCode.INVALID_REQUEST);
    }

    validateReview(reviewDTO);

    Review review = Review.builder()
        .purchaseOrder(order)
        .user(user)
        .rating(reviewDTO.getRating())
        .content(reviewDTO.getContent())
        .build();

    reviewRepository.save(review);

    uploadAndSaveReviewImages(review, reviewDTO.getReviewImage1(), reviewDTO.getReviewImage2(),
        reviewDTO.getReviewImage3());
  }

  private void uploadAndSaveReviewImages(Review review, MultipartFile... reviewImages) {
    int imageOrder = 1;
    for (MultipartFile reviewImage : reviewImages) {
      if (reviewImage != null) {
        String imageUrl = s3Service.uploadReviewImage(reviewImage);

        reviewImageRepository.save(ReviewImage.builder()
            .review(review)
            .imageUrl(imageUrl)
            .imageOrder(imageOrder++)
            .build());
      }
    }
  }


  public void validateReview(ReviewDTO reviewDTO) {
    Double rating = reviewDTO.getRating();
    if (rating < 1 || rating > 5) {
      throw new RestApiException(ErrorCode.INVALID_RATING);
    }
  }


  public GetReviewResponse getReviewByOrderId(User user, Long orderId) {
    PurchaseOrder order = orderService.getOrderById(orderId);

    Review review = reviewRepository.findByPurchaseOrder(order)
        .orElseThrow(() -> new RestApiException(ErrorCode.REVIEW_NOT_FOUND));

    if (!review.getUser().getUserId().equals(user.getUserId())) {
      throw new RestApiException(ErrorCode.INVALID_REQUEST);
    }

    List<ReviewImage> reviewImages = reviewImageRepository.findByReviewOrderByImageOrderDesc(
        review);

    return GetReviewResponse.fromEntity(review, reviewImages);
  }

  public GetReviewResponse getReviewByOrderId(Partner partner, Long storeId, Long orderId) {
    PurchaseOrder order = orderService.getOrderById(orderId);

    Review review = reviewRepository.findByPurchaseOrder(order)
        .orElseThrow(() -> new RestApiException(ErrorCode.REVIEW_NOT_FOUND));

    storeService.validateStoreOwner(partner, order.getStore());

    if (!order.getStore().getStoreId().equals(storeId)) {
      throw new RestApiException(ErrorCode.INVALID_REQUEST);
    }

    List<ReviewImage> reviewImages = reviewImageRepository.findByReviewOrderByImageOrderDesc(
        review);

    return GetReviewResponse.fromEntity(review, reviewImages);
  }

  @Transactional
  public void deleteReview(User user, Long orderId) {
    PurchaseOrder order = orderService.getOrderById(orderId);

    Review review = reviewRepository.findByPurchaseOrder(order)
        .orElseThrow(() -> new RestApiException(ErrorCode.REVIEW_NOT_FOUND));

    if (!review.getUser().getUserId().equals(user.getUserId())) {
      throw new RestApiException(ErrorCode.INVALID_REQUEST);
    }

    List<ReviewImage> reviewImages = reviewImageRepository.findByReviewOrderByImageOrderDesc(
        review);

    reviewImages.forEach(reviewImage -> s3Service.delete(reviewImage.getImageUrl()));

    reviewRepository.delete(review);
  }

  @Transactional
  public void deleteReview(Partner partner, Long storeId, Long orderId) {
    PurchaseOrder order = orderService.getOrderById(orderId);

    Review review = reviewRepository.findByPurchaseOrder(order)
        .orElseThrow(() -> new RestApiException(ErrorCode.REVIEW_NOT_FOUND));

    storeService.validateStoreOwner(partner, order.getStore());

    if (!order.getStore().getStoreId().equals(storeId)) {
      throw new RestApiException(ErrorCode.INVALID_REQUEST);
    }

    List<ReviewImage> reviewImages = reviewImageRepository.findByReviewOrderByImageOrderDesc(
        review);

    reviewImages.forEach(reviewImage -> s3Service.delete(reviewImage.getImageUrl()));

    reviewRepository.delete(review);
  }

  @Transactional
  public void updateReview(User user, Long orderId, ReviewDTO reviewDTO) {
    PurchaseOrder order = orderService.getOrderById(orderId);

    Review review = reviewRepository.findByPurchaseOrder(order)
        .orElseThrow(() -> new RestApiException(ErrorCode.REVIEW_NOT_FOUND));

    if (!review.getUser().getUserId().equals(user.getUserId())) {
      throw new RestApiException(ErrorCode.INVALID_REQUEST);
    }

    List<ReviewImage> reviewImages = reviewImageRepository.findByReviewOrderByImageOrderDesc(
        review);

    for (ReviewImage reviewImage : reviewImages) {
      s3Service.delete(reviewImage.getImageUrl());
    }

    reviewImageRepository.deleteAll(reviewImages);

    review.update(reviewDTO);

    uploadAndSaveReviewImages(review, reviewDTO.getReviewImage1(), reviewDTO.getReviewImage2(),
        reviewDTO.getReviewImage3());
  }


}
