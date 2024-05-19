package jyang.deliverydotdot.service;

import static jyang.deliverydotdot.type.ErrorCode.CART_ITEM_NOT_SAME_STORE;
import static jyang.deliverydotdot.type.ErrorCode.INVALID_CART_ITEM;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import jyang.deliverydotdot.domain.Cart;
import jyang.deliverydotdot.domain.CartItem;
import jyang.deliverydotdot.domain.Menu;
import jyang.deliverydotdot.domain.Store;
import jyang.deliverydotdot.domain.User;
import jyang.deliverydotdot.dto.user.CartDTO;
import jyang.deliverydotdot.dto.user.CartDTO.CartItemDTO;
import jyang.deliverydotdot.exception.RestApiException;
import jyang.deliverydotdot.repository.CartItemRepository;
import jyang.deliverydotdot.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartService {

  private final MenuService menuService;

  private final StoreService storeService;

  private final CartRepository cartRepository;

  private final CartItemRepository cartItemRepository;

  /**
   * 장바구니 추가
   *
   * @param user    사용자
   * @param cartDTO 장바구니 dto
   */
  @Transactional
  public void addCart(User user, CartDTO cartDTO) {

    validateCartDTO(cartDTO);

    Cart cart = getOrCreateCart(user);

    clearExistingCartItemsIfNeeded(cart, cartDTO);

    Long prevStoreId = null;
    for (CartItemDTO cartItemDTO : cartDTO.getCartItems()) {
      Menu menu = menuService.getMenuById(cartItemDTO.getMenuId());

      Long storeId = menu.getStore().getStoreId();

      validateStoreId(prevStoreId, storeId);
      prevStoreId = menu.getStore().getStoreId();

      addOrUpdateCartItem(cart, menu, cartItemDTO.getQuantity());
    }

    Store store = storeService.findStore(prevStoreId);
    cart.setStore(store);
  }

  /**
   * 유효한 장바구니인지 확인
   *
   * @param cartDTO 장바구니 dto
   */

  private void validateCartDTO(CartDTO cartDTO) {
    if (cartDTO == null || cartDTO.getCartItems() == null || cartDTO.getCartItems().isEmpty()) {
      throw new RestApiException(INVALID_CART_ITEM);
    }
  }

  /**
   * 기존 장바구니 가져오거나 새로운 장바구니 생성
   *
   * @param user 사용자
   * @return 장바구니
   */
  private Cart getOrCreateCart(User user) {
    return cartRepository.findByUser(user).orElseGet(() -> {
      Cart newCart = Cart.builder()
          .user(user)
          .build();
      cartRepository.save(newCart);
      return newCart;
    });
  }

  /**
   * 기존 장바구니 삭제가 필요한지 확인 후 삭제
   *
   * @param cart    장바구니
   * @param cartDTO 장바구니 dto
   */
  private void clearExistingCartItemsIfNeeded(Cart cart, CartDTO cartDTO) {
    Set<CartItem> cartItems = cart.getCartItems();
    if (cartItems != null && !cartItems.isEmpty()) {
      Long prevStoreId = cartItems.iterator().next().getMenu().getStore().getStoreId();
      Long currentStoreId = cartDTO.getCartItems().iterator().next().getMenuId();

      // 같은 가게가 아닌 경우 기존 장바구니 삭제
      if (!Objects.equals(prevStoreId, currentStoreId)) {
        cartItemRepository.deleteByCart(cart);
      }
    }
  }

  /**
   * 같은 가게의 메뉴인지 확인
   *
   * @param prevStoreId    이전 가게 id
   * @param currentStoreId 현재 가게 id
   */
  private void validateStoreId(Long prevStoreId, Long currentStoreId) {
    if (prevStoreId != null && !Objects.equals(prevStoreId, currentStoreId)) {
      throw new RestApiException(CART_ITEM_NOT_SAME_STORE);
    }
  }

  /**
   * 장바구니에 메뉴 추가 또는 업데이트
   *
   * @param cart     장바구니
   * @param menu     메뉴
   * @param quantity 수량
   */
  private void addOrUpdateCartItem(Cart cart, Menu menu, int quantity) {
    Optional<CartItem> existCartItem = cartItemRepository.findByCartAndMenu(cart, menu);

    if (existCartItem.isPresent()) {
      existCartItem.get().addQuantity(quantity);
    } else {
      CartItem cartItem = CartItem.builder()
          .cart(cart)
          .menu(menu)
          .quantity(quantity)
          .price(menu.getPrice())
          .build();

      cart.addCartItems(cartItem);

      cartItemRepository.save(cartItem);
    }
  }
}
