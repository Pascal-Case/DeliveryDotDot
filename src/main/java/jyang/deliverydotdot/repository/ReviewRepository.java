package jyang.deliverydotdot.repository;

import java.util.Optional;
import jyang.deliverydotdot.domain.PurchaseOrder;
import jyang.deliverydotdot.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

  Optional<Review> findByPurchaseOrder(PurchaseOrder purchaseOrder);
}
