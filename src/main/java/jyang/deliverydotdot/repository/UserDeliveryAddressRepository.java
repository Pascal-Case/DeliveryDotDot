package jyang.deliverydotdot.repository;

import java.util.List;
import java.util.Optional;
import jyang.deliverydotdot.domain.User;
import jyang.deliverydotdot.domain.UserDeliveryAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserDeliveryAddressRepository extends JpaRepository<UserDeliveryAddress, Long> {

  @Modifying
  @Transactional
  @Query("update UserDeliveryAddress uda set uda.isDefaultAddress = false where uda.user = :user")
  void clearDefaultAddress(User user);

  @Query("select uda from UserDeliveryAddress uda where uda.user = :user and uda.isDefaultAddress = true")
  List<UserDeliveryAddress> findByUserOrderByDefaultAddress(User user);

  Optional<UserDeliveryAddress> findByUserAndId(User user, Long addressId);
}
