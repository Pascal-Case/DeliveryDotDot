package jyang.deliverydotdot.repository;

import java.util.Optional;
import jyang.deliverydotdot.domain.Cart;
import jyang.deliverydotdot.domain.CartItem;
import jyang.deliverydotdot.domain.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

  Optional<CartItem> findByCartAndMenu(Cart cart, Menu menu);

  void deleteByCart(Cart existCart);
}
