package jyang.deliverydotdot.service;

import static jyang.deliverydotdot.type.ErrorCode.MENU_CATEGORY_HAS_MENUS;
import static jyang.deliverydotdot.type.ErrorCode.MENU_CATEGORY_NOT_FOUND;

import jyang.deliverydotdot.domain.MenuCategory;
import jyang.deliverydotdot.domain.Partner;
import jyang.deliverydotdot.domain.Store;
import jyang.deliverydotdot.dto.store.MenuCategoryRegisterForm;
import jyang.deliverydotdot.dto.store.MenuCategoryRegisterForm.MenuCategoryDTO;
import jyang.deliverydotdot.exception.RestApiException;
import jyang.deliverydotdot.repository.MenuCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuCategoryService {

  private final MenuCategoryRepository menuCategoryRepository;

  private final StoreService storeService;


  /**
   * 메뉴 카테고리 등록
   *
   * @param partner                  파트너
   * @param storeId                  가게 ID
   * @param menuCategoryRegisterForm 메뉴 카테고리 등록 폼
   */

  @Transactional
  public void registerMenuCategory(Partner partner, Long storeId,
      MenuCategoryRegisterForm menuCategoryRegisterForm) {

    // 가게 조회
    Store store = storeService.findStore(storeId);

    // 가게 소유자 확인
    storeService.validateStoreOwner(partner, store);

    // 메뉴 카테고리 순서
    int menuCategoryOrder = 1;
    for (MenuCategoryDTO menuCategoryDTO : menuCategoryRegisterForm.getMenuCategories()) {
      // 메뉴 카테고리 등록
      menuCategoryRepository.save(MenuCategory.builder()
          .store(store)
          .categoryName(menuCategoryDTO.getCategoryName())
          .displayOrder(menuCategoryOrder++)
          .build());
    }
  }

  /**
   * 메뉴 카테고리 조회
   *
   * @param menuCategoryId 메뉴 카테고리 ID
   * @return 메뉴 카테고리
   */
  public MenuCategory getMenuCategoryById(Long menuCategoryId) {
    return menuCategoryRepository.findById(menuCategoryId)
        .orElseThrow(() -> new RestApiException(MENU_CATEGORY_NOT_FOUND));
  }

  /**
   * 메뉴 카테고리 삭제
   *
   * @param partner        파트너
   * @param storeId        가게 ID
   * @param menuCategoryId 메뉴 카테고리 ID
   */
  @Transactional
  public void deleteMenuCategory(Partner partner, Long storeId, Long menuCategoryId) {

    // 가게 조회
    Store store = storeService.findStore(storeId);

    // 가게 소유자 확인
    storeService.validateStoreOwner(partner, store);

    // 가게에 속한 메뉴 카테고리인지 확인
    if (store.getMenuCategories().stream()
        .noneMatch(menuCategory -> menuCategory.getMenuCategoryId().equals(menuCategoryId))) {
      throw new RestApiException(MENU_CATEGORY_NOT_FOUND);
    }

    // 메뉴 카테고리 조회
    MenuCategory menuCategory = getMenuCategoryById(menuCategoryId);

    // 메뉴 카테고리에 메뉴가 있는지 확인
    if (!menuCategory.getMenus().isEmpty()) {
      throw new RestApiException(MENU_CATEGORY_HAS_MENUS);
    }

    // 가게에서 메뉴 카테고리 삭제
    store.getMenuCategories().remove(menuCategory);

    // 메뉴 카테고리 삭제
    menuCategoryRepository.delete(menuCategory);
  }
}
