package jyang.deliverydotdot.controller;

import static org.springframework.http.HttpStatus.CREATED;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jyang.deliverydotdot.domain.User;
import jyang.deliverydotdot.dto.order.CreateOrder;
import jyang.deliverydotdot.dto.response.SuccessResponse;
import jyang.deliverydotdot.dto.user.CartDTO;
import jyang.deliverydotdot.dto.user.CartDTO.CartItemDTO;
import jyang.deliverydotdot.dto.user.ReviewDTO;
import jyang.deliverydotdot.dto.user.UserDeliveryAddressDTO.AddAddressRequest;
import jyang.deliverydotdot.dto.user.UserDeliveryAddressDTO.UpdateAddressRequest;
import jyang.deliverydotdot.dto.user.UserJoinForm;
import jyang.deliverydotdot.dto.user.UserUpdateForm;
import jyang.deliverydotdot.security.AuthenticationFacade;
import jyang.deliverydotdot.service.CartService;
import jyang.deliverydotdot.service.OrderService;
import jyang.deliverydotdot.service.ReviewService;
import jyang.deliverydotdot.service.UserService;
import jyang.deliverydotdot.type.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Tag(name = "User API", description = "유저 관련 API")
public class UserController {

  private final UserService userService;

  private final CartService cartService;

  private final OrderService orderService;

  private final AuthenticationFacade authenticationFacade;

  private final ReviewService reviewService;

  @Operation(summary = "유저 회원가입", description = "유저 등록 폼으로 회원가입")
  @PostMapping("/auth/join")
  public ResponseEntity<SuccessResponse<?>> userJoinProcess(
      @RequestBody @Valid UserJoinForm joinForm
  ) {
    userService.registerUser(joinForm);

    return ResponseEntity.status(CREATED).body(
        SuccessResponse.of("유저를 성공적으로 생성했습니다.")
    );
  }

  @Operation(summary = "유저 정보 조회", description = "유저 정보 조회")
  @GetMapping()
  public ResponseEntity<SuccessResponse<?>> getUserInfo(
  ) {
    return ResponseEntity.ok(
        SuccessResponse.of(userService.getUserInfo(authenticationFacade.getUsername())));
  }

  @Operation(summary = "유저 정보 수정", description = "유저 정보 수정")
  @PutMapping()
  public ResponseEntity<SuccessResponse<?>> updateUserInfo(
      @RequestBody @Valid UserUpdateForm updateForm
  ) {
    userService.updateUserInfo(authenticationFacade.getUsername(), updateForm);
    return ResponseEntity.ok(SuccessResponse.of("유저 정보를 성공적으로 수정했습니다."));
  }

  @Operation(summary = "유저 삭제", description = "유저 삭제")
  @DeleteMapping()
  public ResponseEntity<SuccessResponse<?>> deleteUser(
  ) {
    userService.deleteByLoginId(authenticationFacade.getUsername());
    return ResponseEntity.ok(SuccessResponse.of("유저를 성공적으로 삭제했습니다."));
  }

  @Operation(summary = "장바구니 추가", description = "장바구니에 메뉴 추가")
  @PostMapping("/cart")
  public ResponseEntity<SuccessResponse<?>> addCart(
      @RequestBody @Valid CartDTO cartDTO
  ) {
    User user = userService.getUserByLoginId(authenticationFacade.getUsername());
    cartService.addCart(user, cartDTO);

    return ResponseEntity.status(CREATED).body(
        SuccessResponse.of("장바구니에 메뉴를 성공적으로 추가했습니다.")
    );
  }

  @Operation(summary = "장바구니 조회", description = "장바구니 조회")
  @GetMapping("/cart")
  public ResponseEntity<SuccessResponse<?>> getCart(
  ) {
    User user = userService.getUserByLoginId(authenticationFacade.getUsername());
    return ResponseEntity.ok(SuccessResponse.of(
        "장바구니를 성공적으로 조회했습니다.", cartService.getCart(user)));
  }

  @Operation(summary = "장바구니 삭제", description = "장바구니 삭제")
  @DeleteMapping("/cart")
  public ResponseEntity<SuccessResponse<?>> deleteCart(
  ) {
    User user = userService.getUserByLoginId(authenticationFacade.getUsername());
    cartService.deleteCart(user);
    return ResponseEntity.ok(SuccessResponse.of("장바구니를 성공적으로 삭제했습니다."));
  }

  @Operation(summary = "장바구니 메뉴 삭제", description = "장바구니에 담긴 메뉴 삭제")
  @DeleteMapping("/cart/menu/{menuId}")
  public ResponseEntity<SuccessResponse<?>> deleteCartMenu(
      @PathVariable Long menuId
  ) {
    User user = userService.getUserByLoginId(authenticationFacade.getUsername());
    cartService.deleteCartMenu(user, menuId);
    return ResponseEntity.ok(SuccessResponse.of("장바구니에 담긴 메뉴를 성공적으로 삭제했습니다."));
  }

  @Operation(summary = "장바구니 메뉴 수정", description = "장바구니에 담긴 메뉴 수정")
  @PutMapping("/cart/menu")
  public ResponseEntity<SuccessResponse<?>> updateCartMenu(
      @RequestBody @Valid CartItemDTO cartItemDTO
  ) {
    User user = userService.getUserByLoginId(authenticationFacade.getUsername());
    cartService.updateCartMenu(user, cartItemDTO);
    return ResponseEntity.ok(SuccessResponse.of("장바구니에 담긴 메뉴를 성공적으로 수정했습니다."));
  }

  @Operation(summary = "배송지 추가", description = "배송지 추가")
  @PostMapping("/address")
  public ResponseEntity<SuccessResponse<?>> addAddress(
      @RequestBody @Valid AddAddressRequest userDeliveryAddressDTO
  ) {
    User user = userService.getUserByLoginId(authenticationFacade.getUsername());
    userService.addAddress(user, userDeliveryAddressDTO);
    return ResponseEntity.status(CREATED).body(
        SuccessResponse.of("배송지를 성공적으로 추가했습니다.")
    );
  }

  @Operation(summary = "배송지 삭제", description = "배송지 삭제")
  @DeleteMapping("/address/{addressId}")
  public ResponseEntity<SuccessResponse<?>> deleteAddress(
      @PathVariable Long addressId
  ) {
    User user = userService.getUserByLoginId(authenticationFacade.getUsername());
    userService.deleteAddress(user, addressId);
    return ResponseEntity.ok(SuccessResponse.of("배송지를 성공적으로 삭제했습니다."));
  }

  @Operation(summary = "배송지 수정", description = "배송지 수정")
  @PutMapping("/address")
  public ResponseEntity<SuccessResponse<?>> updateAddress(
      @RequestBody @Valid UpdateAddressRequest userDeliveryAddressDTO
  ) {
    User user = userService.getUserByLoginId(authenticationFacade.getUsername());
    userService.updateAddress(user, userDeliveryAddressDTO);
    return ResponseEntity.ok(SuccessResponse.of("배송지를 성공적으로 수정했습니다."));
  }

  @Operation(summary = "배송지 조회", description = "배송지 조회")
  @GetMapping("/address")
  public ResponseEntity<SuccessResponse<?>> getAddress(
  ) {
    User user = userService.getUserByLoginId(authenticationFacade.getUsername());
    return ResponseEntity.ok(SuccessResponse.of(
        "배송지를 성공적으로 조회했습니다.", userService.getAddress(user)));
  }

  @Operation(summary = "주문 생성", description = "주문 생성")
  @PostMapping("/orders")
  public ResponseEntity<SuccessResponse<?>> createOrder(
      @RequestBody @Valid CreateOrder.Request request
  ) {
    User user = userService.getUserByLoginId(authenticationFacade.getUsername());
    orderService.createOrder(user, request);
    return ResponseEntity.ok(SuccessResponse.of("주문을 성공적으로 생성했습니다."));
  }

  @Operation(summary = "주문 조회", description = "주문 조회")
  @GetMapping("/orders")
  public ResponseEntity<SuccessResponse<?>> getOrders(
      @PageableDefault Pageable pageable,
      @RequestParam(required = false) OrderStatus status,
      @RequestParam(required = false) String query
  ) {
    User user = userService.getUserByLoginId(authenticationFacade.getUsername());
    return ResponseEntity.ok(
        SuccessResponse.of(
            "주문 목록을 성공적으로 조회했습니다.",
            orderService.getUserOrder(user, pageable, status, query)));
  }

  @Operation(summary = "주문 상세 조회", description = "주문 상세 조회")
  @GetMapping("/orders/{orderId}")
  public ResponseEntity<SuccessResponse<?>> getOrderDetail(
      @PathVariable Long orderId
  ) {
    User user = userService.getUserByLoginId(authenticationFacade.getUsername());
    return ResponseEntity.ok(
        SuccessResponse.of(
            "주문 상세 정보를 성공적으로 조회했습니다.",
            orderService.getUserOrderDetail(user, orderId)));
  }

  @Operation(summary = "주문 취소", description = "주문 취소")
  @PutMapping("/orders/{orderId}/cancel")
  public ResponseEntity<SuccessResponse<?>> cancelOrder(
      @PathVariable Long orderId
  ) {
    User user = userService.getUserByLoginId(authenticationFacade.getUsername());
    orderService.cancelOrderByUser(user, orderId);
    return ResponseEntity.ok(SuccessResponse.of("주문을 성공적으로 취소했습니다."));
  }

  @Operation(summary = "리뷰 작성", description = "리뷰 작성")
  @PostMapping("/orders/{orderId}/review")
  public ResponseEntity<SuccessResponse<?>> createReview(
      @PathVariable Long orderId,
      @ModelAttribute @Valid ReviewDTO reviewDTO
  ) {
    User user = userService.getUserByLoginId(authenticationFacade.getUsername());
    reviewService.createReview(user, orderId, reviewDTO);
    return ResponseEntity.ok(SuccessResponse.of("리뷰를 성공적으로 작성했습니다."));
  }

  @Operation(summary = "리뷰 조회", description = "리뷰 조회")
  @GetMapping("/orders/{orderId}/review")
  public ResponseEntity<SuccessResponse<?>> getReview(
      @PathVariable Long orderId
  ) {
    User user = userService.getUserByLoginId(authenticationFacade.getUsername());
    return ResponseEntity.ok(SuccessResponse.of(
        "리뷰를 성공적으로 조회했습니다.",
        reviewService.getReviewByOrderId(user, orderId)));
  }

  @Operation(summary = "리뷰 삭제", description = "리뷰 삭제")
  @DeleteMapping("/orders/{orderId}/review")
  public ResponseEntity<SuccessResponse<?>> deleteReview(
      @PathVariable Long orderId
  ) {
    User user = userService.getUserByLoginId(authenticationFacade.getUsername());
    reviewService.deleteReview(user, orderId);
    return ResponseEntity.ok(SuccessResponse.of("리뷰를 성공적으로 삭제했습니다."));
  }

  @Operation(summary = "리뷰 수정", description = "리뷰 수정")
  @PutMapping("/orders/{orderId}/review")
  public ResponseEntity<SuccessResponse<?>> updateReview(
      @PathVariable Long orderId,
      @RequestBody @Valid ReviewDTO reviewDTO
  ) {
    User user = userService.getUserByLoginId(authenticationFacade.getUsername());
    reviewService.updateReview(user, orderId, reviewDTO);
    return ResponseEntity.ok(SuccessResponse.of("리뷰를 성공적으로 수정했습니다."));
  }
}
