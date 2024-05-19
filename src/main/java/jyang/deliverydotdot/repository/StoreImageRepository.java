package jyang.deliverydotdot.repository;

import java.util.List;
import java.util.Optional;
import jyang.deliverydotdot.domain.Store;
import jyang.deliverydotdot.domain.StoreImage;
import org.springframework.data.jpa.repository.JpaRepository;


public interface StoreImageRepository extends JpaRepository<StoreImage, Long> {

  Optional<StoreImage> findByStoreAndImageOrder(Store store, int imageOrder);

  void deleteAllByStore(Store store);

  List<StoreImage> findAllByStore(Store store);
}
