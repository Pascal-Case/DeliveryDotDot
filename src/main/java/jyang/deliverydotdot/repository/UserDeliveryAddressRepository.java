package jyang.deliverydotdot.repository;

import jyang.deliverydotdot.domain.UserDeliveryAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDeliveryAddressRepository extends JpaRepository<UserDeliveryAddress, Long> {
  
}
