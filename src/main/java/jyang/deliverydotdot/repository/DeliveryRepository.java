package jyang.deliverydotdot.repository;

import java.util.Optional;
import jyang.deliverydotdot.domain.Delivery;
import jyang.deliverydotdot.domain.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

  boolean existsByPurchaseOrder(PurchaseOrder purchaseOrder);

  Optional<Delivery> findByPurchaseOrder(PurchaseOrder purchaseOrder);
}
