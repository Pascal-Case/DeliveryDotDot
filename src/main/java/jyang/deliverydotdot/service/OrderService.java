package jyang.deliverydotdot.service;

import static jyang.deliverydotdot.type.ErrorCode.ADDRESS_NOT_FOUND;
import static jyang.deliverydotdot.type.ErrorCode.CART_NOT_FOUND;
import static jyang.deliverydotdot.type.ErrorCode.INVALID_QUANTITY;
import static jyang.deliverydotdot.type.ErrorCode.STORE_CLOSED;
import static jyang.deliverydotdot.type.OrderStatus.PENDING;

import java.time.LocalTime;
import java.util.ArrayList;
import jyang.deliverydotdot.domain.Cart;
import jyang.deliverydotdot.domain.CartItem;
import jyang.deliverydotdot.domain.PurchaseOrder;
import jyang.deliverydotdot.domain.Store;
import jyang.deliverydotdot.domain.User;
import jyang.deliverydotdot.domain.UserDeliveryAddress;
import jyang.deliverydotdot.dto.order.CreateOrder.Request;
import jyang.deliverydotdot.exception.RestApiException;
import jyang.deliverydotdot.repository.CartRepository;
import jyang.deliverydotdot.repository.OrderRepository;
import jyang.deliverydotdot.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class OrderService {

  private final UserService userService;

  private final LocationService locationService;

  private final OrderRepository orderRepository;

  private final CartRepository cartRepository;

  /**
   * 주문 생성
   *
   * @param user    주문자
   * @param request 주문 요청
   */
  @Transactional
  public void createOrder(User user, Request request) {
    if (user.getCart() == null) {
      throw new RestApiException(CART_NOT_FOUND);
    }

    Cart cart = getCart(user);

    Store store = cart.getStore();
    Point storeCoordinate = store.getCoordinates();
    validateStoreHours(store);

    String deliveryAddress = resolveDeliveryAddress(user, request);
    Point deliveryCoordinate = locationService.getCoordinatesFromAddress(deliveryAddress);
    String phone = resolvePhone(user, request);

    locationService.validateDeliveryArea(storeCoordinate, deliveryCoordinate);

    PurchaseOrder purchaseOrder = orderRepository.save(
        buildOrder(user, store, phone, deliveryAddress, deliveryCoordinate,
            request.getDeliveryRequest()));

    for (CartItem cartItem : cart.getCartItems()) {
      if (cartItem.getQuantity() <= 0) {
        throw new RestApiException(INVALID_QUANTITY);
      }
      if (cartItem.getMenu().getPrice() <= 0) {
        throw new RestApiException(ErrorCode.INVALID_PRICE);
      }
      purchaseOrder.addOrderItem(cartItem);
    }

    purchaseOrder.calculateTotalPrice();

    cartRepository.delete(cart);
  }


  private Cart getCart(User user) {
    Cart cart = cartRepository.findByIdWithStore(user.getCart().getCartId());
    if (cart == null) {
      throw new RestApiException(CART_NOT_FOUND);
    }
    return cart;
  }

  private void validateStoreHours(Store store) {
    LocalTime currentTime = LocalTime.now();
    if (currentTime.isBefore(store.getOpenTime()) || currentTime.isAfter(store.getCloseTime())) {
      throw new RestApiException(STORE_CLOSED);
    }
  }

  private String resolvePhone(User user, Request request) {
    return (request.getOptionalPhone() != null) ? request.getOptionalPhone() : user.getPhone();
  }

  private String resolveDeliveryAddress(User user, Request request) {
    if (request.getDeliveryAddressId() != null) {
      UserDeliveryAddress userDeliveryAddress =
          userService.getUserDeliveryAddress(user, request.getDeliveryAddressId());
      return userDeliveryAddress.getAddress();
    } else if (request.getOptionalAddress() != null) {
      return request.getOptionalAddress();
    } else {
      throw new RestApiException(ADDRESS_NOT_FOUND);
    }
  }

  private PurchaseOrder buildOrder(User user, Store store, String phone, String deliveryAddress,
      Point coordinate, String deliveryRequest) {
    return PurchaseOrder.builder()
        .user(user)
        .store(store)
        .orderStatus(PENDING)
        .orderNumber("OD" + System.currentTimeMillis())
        .deliveryAddress(deliveryAddress)
        .coordinate(coordinate)
        .phone(phone)
        .orderItems(new ArrayList<>())
        .deliveryRequest(deliveryRequest)
        .build();
  }
}
