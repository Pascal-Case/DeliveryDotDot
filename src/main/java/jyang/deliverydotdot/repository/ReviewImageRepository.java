package jyang.deliverydotdot.repository;

import java.util.List;
import jyang.deliverydotdot.domain.Review;
import jyang.deliverydotdot.domain.ReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long> {

  List<ReviewImage> findByReview(Review review);

  List<ReviewImage> findByReviewOrderByImageOrderDesc(Review review);
}
