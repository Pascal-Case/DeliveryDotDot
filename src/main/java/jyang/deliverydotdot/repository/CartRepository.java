package jyang.deliverydotdot.repository;

import java.util.Optional;
import jyang.deliverydotdot.domain.Cart;
import jyang.deliverydotdot.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

  Optional<Cart> findByUser(User user);
}
