package jyang.deliverydotdot.domain;

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
import jyang.deliverydotdot.type.DeliveryStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Inheritance(strategy = InheritanceType.JOINED)
public class Delivery extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long deliveryId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "rider_id")
  private Rider rider;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "purchase_order_id")
  private PurchaseOrder purchaseOrder;

  @Enumerated(EnumType.STRING)
  private DeliveryStatus deliveryStatus;

  private String deliveryImageUrl;

  public void start() {
    this.deliveryStatus = DeliveryStatus.DELIVERING;
  }

  public void complete(String imageUrl) {
    this.deliveryImageUrl = imageUrl;
    this.deliveryStatus = DeliveryStatus.DELIVERED;
  }

  public void fail() {
    this.deliveryStatus = DeliveryStatus.FAILED;
  }

}
