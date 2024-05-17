package jyang.deliverydotdot.repository;

import jyang.deliverydotdot.domain.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {

  boolean existsByRegistrationNumber(String registrationNumber);
}
