package jyang.deliverydotdot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jyang.deliverydotdot.domain.User;
import jyang.deliverydotdot.dto.order.CreateOrder;
import jyang.deliverydotdot.dto.response.SuccessResponse;
import jyang.deliverydotdot.security.AuthenticationFacade;
import jyang.deliverydotdot.service.OrderService;
import jyang.deliverydotdot.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
@Tag(name = "Order API", description = "주문 관련 API")
public class OrderController {

  private final AuthenticationFacade authenticationFacade;

  private final UserService userService;

  private final OrderService orderService;

  @Operation(summary = "주문 생성", description = "주문 생성")
  @PostMapping()
  public ResponseEntity<SuccessResponse<?>> createOrder(
      @RequestBody @Valid CreateOrder.Request request
  ) {
    User user = userService.getUserByLoginId(authenticationFacade.getUsername());
    orderService.createOrder(user, request);
    return ResponseEntity.ok(SuccessResponse.of("주문을 성공적으로 생성했습니다."));
  }

}
