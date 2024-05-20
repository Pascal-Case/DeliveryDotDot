package jyang.deliverydotdot.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import jyang.deliverydotdot.type.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.locationtech.jts.geom.Point;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Inheritance(strategy = InheritanceType.JOINED)
public class PurchaseOrder extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long purchaseOrderId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "store_id")
  private Store store;

  @OneToMany(mappedBy = "purchaseOrder", fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
  private List<OrderItem> orderItems = new ArrayList<>();

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private OrderStatus orderStatus;

  private Integer totalPrice;

  @Column(nullable = false)
  private String orderNumber;

  @Column(nullable = false)
  private String deliveryAddress;

  @Column(nullable = false)
  private Point coordinate;

  @Column(nullable = false)
  private String phone;

  private String deliveryRequest;

  public void calculateTotalPrice() {
    this.totalPrice = orderItems.stream()
        .mapToInt(OrderItem::calculatePrice)
        .sum();
  }

  public void addOrderItem(CartItem cartItem) {
    OrderItem orderItem = OrderItem.builder()
        .purchaseOrder(this)
        .menu(cartItem.getMenu())
        .quantity(cartItem.getQuantity())
        .price(cartItem.getMenu().getPrice())
        .build();

    orderItems.add(orderItem);
  }
}
