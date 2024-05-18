package jyang.deliverydotdot.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
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
@SQLDelete(sql = "UPDATE menu SET deleted_at = now() WHERE menu_id = ?")
@SQLRestriction("deleted_at is null")
public class Menu extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long menuId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "store_id")
  private Store store;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "menu_category_id")
  private MenuCategory menuCategory;

  private String menuName;

  private Integer price;

  private String menuDescription;

  private String menuImageUrl;

  private LocalDateTime deletedAt;

}
