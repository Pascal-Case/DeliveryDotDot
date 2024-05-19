package jyang.deliverydotdot.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuCategory {

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

  public void updateCategoryName(String categoryName) {
    this.categoryName = categoryName;
  }

  public void updateDisplayOrder(Integer displayOrder) {
    this.displayOrder = displayOrder;
  }

}
