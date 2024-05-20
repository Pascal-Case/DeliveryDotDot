package jyang.deliverydotdot.dto.store;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import jyang.deliverydotdot.domain.PurchaseOrder;
import jyang.deliverydotdot.type.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class StoreOrderDTO {

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class OrderListResponse {

    @Schema(description = "주문 ID")
    private Long purchaseOrderId;

    @Schema(description = "주문 번호")
    private String orderNumber;

    @Schema(description = "배송지 주소")
    private String deliveryAddress;

    @Schema(description = "연락처")
    private String phone;

    @Schema(description = "총 가격")
    private Integer totalPrice;

    @Schema(description = "주문 상품 목록")
    private List<OrderItemListResponse> orderItems;

    @Schema(description = "주문 상태")
    private OrderStatus orderStatus;

    @Schema(description = "요청 사항")
    private String deliveryRequest;


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItemListResponse {

      @Schema(description = "주문 상품 ID")
      private Long orderItemId;

      @Schema(description = "상품 명")
      private String menuName;

      @Schema(description = "수량")
      private Integer quantity;

      @Schema(description = "가격")
      private Integer price;
    }

    public static OrderListResponse fromEntity(PurchaseOrder purchaseOrder) {
      return OrderListResponse.builder()
          .purchaseOrderId(purchaseOrder.getPurchaseOrderId())
          .orderNumber(purchaseOrder.getOrderNumber())
          .deliveryAddress(purchaseOrder.getDeliveryAddress())
          .phone(purchaseOrder.getPhone())
          .totalPrice(purchaseOrder.getTotalPrice())
          .orderItems(purchaseOrder.getOrderItems().stream()
              .map(orderItem -> OrderItemListResponse.builder()
                  .orderItemId(orderItem.getOrderItemId())
                  .menuName(orderItem.getMenu().getMenuName())
                  .quantity(orderItem.getQuantity())
                  .price(orderItem.getPrice())
                  .build())
              .toList())
          .orderStatus(purchaseOrder.getOrderStatus())
          .deliveryRequest(purchaseOrder.getDeliveryRequest())
          .build();
    }

  }


}
