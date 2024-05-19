package jyang.deliverydotdot.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jyang.deliverydotdot.dto.user.UserDeliveryAddressDTO.UpdateAddressRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDeliveryAddress {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @Column(nullable = false)
  private String addressName;

  @Column(nullable = false)
  private String address;

  @Column(nullable = false, columnDefinition = "POINT")
  private Point coordinates;

  @Column(nullable = false)
  private boolean isDefaultAddress;


  public void update(UpdateAddressRequest userDeliveryAddressDTO, Point coordinates) {
    this.addressName = userDeliveryAddressDTO.getAddressName();
    this.address = userDeliveryAddressDTO.getAddress();
    this.coordinates = coordinates;
    this.isDefaultAddress = userDeliveryAddressDTO.getIsDefault();
  }
}
