package jyang.deliverydotdot.repository;

import jyang.deliverydotdot.domain.MenuCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuCategoryRepository extends JpaRepository<MenuCategory, Long> {

}
