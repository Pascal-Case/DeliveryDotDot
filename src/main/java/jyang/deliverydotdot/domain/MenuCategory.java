package jyang.deliverydotdot.domain;

import jakarta.persistence.CascadeType;
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
import java.util.List;
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
@SQLDelete(sql = "UPDATE menu_category SET deleted_at = now() WHERE menu_category_id = ?")
@SQLRestriction("deleted_at is null")
public class MenuCategory extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long menuCategoryId;

  @JoinColumn(name = "store_id")
  @ManyToOne(fetch = FetchType.LAZY)
  private Store store;

  @OneToMany(mappedBy = "menuCategory", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Menu> menus;

  private String categoryName;

  private Integer displayOrder;

  private LocalDateTime deletedAt;

  public void updateCategoryName(String categoryName) {
    this.categoryName = categoryName;
  }

  public void updateDisplayOrder(Integer displayOrder) {
    this.displayOrder = displayOrder;
  }

}
