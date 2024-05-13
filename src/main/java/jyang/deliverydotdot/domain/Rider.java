package jyang.deliverydotdot.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import java.time.LocalDateTime;
import jyang.deliverydotdot.dto.rider.RiderUpdateForm;
import jyang.deliverydotdot.type.DeliveryMethod;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Inheritance(strategy = InheritanceType.JOINED)
@SQLDelete(sql = "UPDATE partner SET deleted_at = now() WHERE partner_id = ?")
@SQLRestriction("deleted_at is null")
public class Rider extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long riderId;

  @Column(unique = true, nullable = false)
  private String loginId;

  private String password;

  @Column(unique = true, nullable = false)
  private String email;

  @Column(nullable = false)
  private String name;

  @Column(unique = true, nullable = false)
  private String phone;

  private String address;

  @Enumerated(EnumType.STRING)
  private DeliveryMethod deliveryMethod;

  private String deliveryRegion;

  private LocalDateTime deletedAt;

  public void update(RiderUpdateForm updateForm) {
    if (updateForm.getPassword() != null) {
      this.password = updateForm.getPassword();
    }
    this.email = updateForm.getEmail();
    this.phone = updateForm.getPhone();
    this.address = updateForm.getAddress();
    this.deliveryMethod = DeliveryMethod.valueOf(updateForm.getDeliveryMethod());
    this.deliveryRegion = updateForm.getDeliveryRegion();
  }
}
