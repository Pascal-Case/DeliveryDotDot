package jyang.deliverydotdot.service;

import static jyang.deliverydotdot.type.ErrorCode.CART_ITEM_NOT_SAME_STORE;
import static jyang.deliverydotdot.type.ErrorCode.INVALID_CART_ITEM;

import java.util.HashSet;
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
    Long newStoreId = getStoreIdFromCartDTO(cartDTO);
    clearExistingCartItemsIfNeeded(cart, newStoreId);

    for (CartItemDTO cartItemDTO : cartDTO.getCartItems()) {
      Menu menu = menuService.getMenuById(cartItemDTO.getMenuId());

      if (cart.getStore() == null) {
        cart.setStore(menu.getStore());
      } else {
        validateStoreId(cart.getStore(), menu.getStore());
      }

      addOrUpdateCartItem(cart, menu, cartItemDTO.getQuantity());
    }

    if (cart.getStore() == null || !Objects.equals(cart.getStore().getStoreId(), newStoreId)) {
      Store store = storeService.findStore(newStoreId);
      cart.setStore(store);
    }
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
          .cartItems(new HashSet<>())
          .build();
      cartRepository.save(newCart);
      return newCart;
    });
  }

  /**
   * 기존 장바구니 삭제가 필요한지 확인 후 삭제
   *
   * @param cart       장바구니
   * @param newStoreId 새로 추가된 메뉴의 가게 id
   */
  private void clearExistingCartItemsIfNeeded(Cart cart, Long newStoreId) {
    Set<CartItem> cartItems = cart.getCartItems();
    if (cartItems != null && !cartItems.isEmpty()) {
      Long prevStoreId = cartItems.iterator().next().getMenu().getStore().getStoreId();
      if (!Objects.equals(prevStoreId, newStoreId)) {
        cartItemRepository.deleteAll(cartItems);
        cart.clearCartItems();
        cart.setStore(null);
      }
    }
  }

  /**
   * 같은 가게의 메뉴인지 확인
   *
   * @param cartStore 장바구니의 가게
   * @param menuStore 메뉴의 가게
   */
  private void validateStoreId(Store cartStore, Store menuStore) {
    if (cartStore != null && !Objects.equals(cartStore.getStoreId(), menuStore.getStoreId())) {
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

  private Long getStoreIdFromCartDTO(CartDTO cartDTO) {
    return cartDTO.getCartItems().stream()
        .findFirst()
        .map(CartItemDTO::getMenuId)
        .map(menuService::getMenuById)
        .map(Menu::getStore)
        .map(Store::getStoreId)
        .orElseThrow(() -> new RestApiException(INVALID_CART_ITEM));
  }
}
