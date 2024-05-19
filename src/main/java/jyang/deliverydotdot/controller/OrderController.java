package jyang.deliverydotdot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
@Tag(name = "Order API", description = "주문 관련 API")
public class OrderController {

  @Operation(summary = "주문 생성", description = "주문 생성")
  @PostMapping()
  public void createOrder() {
    
  }

}
