package jyang.deliverydotdot.repository;

import java.util.Optional;
import jyang.deliverydotdot.domain.StoreCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreCategoryRepository extends JpaRepository<StoreCategory, Long> {

  Optional<StoreCategory> findByCategoryName(String name);
}
