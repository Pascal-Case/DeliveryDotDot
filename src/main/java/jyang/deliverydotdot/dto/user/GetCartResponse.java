package jyang.deliverydotdot.dto.user;

import java.util.List;
import jyang.deliverydotdot.domain.Cart;
import jyang.deliverydotdot.domain.CartItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetCartResponse {

  private Long cartId;

  private Long storeId;

  private String storeName;

  private Integer totalPrice;

  private Integer totalQuantity;

  List<GetCartItemsResponse> cartItems;


  @Getter
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  public static class GetCartItemsResponse {

    private Long cartItemId;
    private Long itemId;
    private String itemName;
    private Integer price;
    private Integer quantity;
  }

  public static GetCartResponse fromCart(Cart cart) {
    int totalPrice = 0;
    int totalQuantity = 0;
    if (cart.getCartItems() != null && !cart.getCartItems().isEmpty()) {
      totalPrice += cart.getCartItems().stream()
          .mapToInt(CartItem::getPrice)
          .map(cartItemPrice -> cartItemPrice * cart.getCartItems().stream()
              .mapToInt(CartItem::getQuantity)
              .sum())
          .sum();
      totalQuantity += cart.getCartItems().stream()
          .mapToInt(CartItem::getQuantity)
          .sum();
    }
    return GetCartResponse.builder()
        .cartId(cart.getCartId())
        .storeId(cart.getStore().getStoreId())
        .storeName(cart.getStore().getStoreName())
        .totalPrice(totalPrice)
        .totalQuantity(totalQuantity)
        .cartItems(cart.getCartItems().stream()
            .map(cartItem -> GetCartItemsResponse.builder()
                .cartItemId(cartItem.getCartItemId())
                .itemId(cartItem.getMenu().getMenuId())
                .itemName(cartItem.getMenu().getMenuName())
                .price(cartItem.getPrice())
                .quantity(cartItem.getQuantity())
                .build())
            .toList())
        .build();
  }
}
