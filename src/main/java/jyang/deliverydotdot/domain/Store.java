package jyang.deliverydotdot.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import jyang.deliverydotdot.dto.store.StoreUpdateForm;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.locationtech.jts.geom.Point;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Inheritance(strategy = InheritanceType.JOINED)
@SQLDelete(sql = "UPDATE store SET deleted_at = now() WHERE store_id = ?")
@SQLRestriction("deleted_at is null")
public class Store extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long storeId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "store_category_id")
  private StoreCategory storeCategory;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "partner_id")
  private Partner partner;

  @OneToMany(mappedBy = "store", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private List<MenuCategory> menuCategories;

  @Column(nullable = false)
  private String storeName;

  @Column(nullable = false)
  private String storeAddress;

  @Column(nullable = false, columnDefinition = "POINT")
  private Point coordinates;

  @Column(nullable = false, unique = true)
  private String registrationNumber;

  private Integer holiday;

  private LocalTime openTime;

  private LocalTime closeTime;

  private LocalTime lastOrderTime;

  private String description;

  private Double averageRating;

  private Integer reviewCount;

  private LocalDateTime deletedAt;

  public void update(StoreUpdateForm storeUpdateForm) {
    this.holiday = storeUpdateForm.getHoliday();
    this.openTime = storeUpdateForm.getOpenTime();
    this.closeTime = storeUpdateForm.getCloseTime();
    this.lastOrderTime = storeUpdateForm.getLastOrderTime();
    this.description = storeUpdateForm.getDescription();
  }
}
