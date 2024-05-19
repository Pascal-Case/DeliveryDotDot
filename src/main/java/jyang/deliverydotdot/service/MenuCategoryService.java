package jyang.deliverydotdot.service;

import java.util.List;
import jyang.deliverydotdot.domain.MenuCategory;
import jyang.deliverydotdot.domain.Partner;
import jyang.deliverydotdot.domain.Store;
import jyang.deliverydotdot.dto.store.MenuCategoryDTO;
import jyang.deliverydotdot.dto.store.MenuCategoryRegisterForm;
import jyang.deliverydotdot.exception.RestApiException;
import jyang.deliverydotdot.repository.MenuCategoryRepository;
import jyang.deliverydotdot.type.ErrorCode;
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
    Store store = storeService.findStore(storeId);
    storeService.validateStoreOwner(partner, store);

    List<MenuCategoryRegisterForm.MenuCategoryDTO> menuCategories = menuCategoryRegisterForm.getMenuCategories();
    validateMenuCategoryCount(menuCategories);

    int displayOrder = 1;
    for (MenuCategoryRegisterForm.MenuCategoryDTO menuCategoryDTO : menuCategories) {
      MenuCategory menuCategory = MenuCategory.builder()
          .store(store)
          .categoryName(menuCategoryDTO.getCategoryName())
          .displayOrder(displayOrder++)
          .build();
      menuCategoryRepository.save(menuCategory);
    }
  }

  /**
   * 단일 메뉴 카테고리 추가
   *
   * @param partner         파트너
   * @param storeId         가게 ID
   * @param menuCategoryDTO 메뉴 카테고리 DTO
   */
  @Transactional
  public void addSingleMenuCategory(Partner partner, Long storeId,
      MenuCategoryDTO menuCategoryDTO) {

    Store store = storeService.findStore(storeId);

    storeService.validateStoreOwner(partner, store);

    // 메뉴 카테고리 개수 검증
    if (store.getMenuCategories().size() >= 5) {
      throw new RestApiException(ErrorCode.INVALID_MENU_CATEGORY_COUNT);
    }

    int displayOrder = menuCategoryDTO.getDisplayOrder();
    MenuCategory menuCategory = MenuCategory.builder()
        .store(store)
        .categoryName(menuCategoryDTO.getCategoryName())
        .displayOrder(displayOrder)
        .build();

    menuCategoryRepository.save(menuCategory);

    reorderMenuCategories(store, menuCategory, displayOrder);

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
    Store store = storeService.findStore(storeId);
    storeService.validateStoreOwner(partner, store);
    validateStoreHasMenuCategory(store, menuCategoryId);

    MenuCategory menuCategory = getMenuCategoryById(menuCategoryId);
    validateMenuCategoryHasNoMenus(menuCategory);

    store.getMenuCategories().remove(menuCategory);
    menuCategoryRepository.delete(menuCategory);

    // 삭제된 카테고리 이후의 카테고리들의 순서를 1씩 감소시킴
    store.getMenuCategories().stream()
        .filter(mc -> mc.getDisplayOrder() > menuCategory.getDisplayOrder())
        .forEach(mc -> mc.updateDisplayOrder(mc.getDisplayOrder() - 1));
  }

  /**
   * 메뉴 카테고리 수정
   *
   * @param partner         파트너
   * @param storeId         가게 ID
   * @param menuCategoryId  메뉴 카테고리 ID
   * @param menuCategoryDTO 메뉴 카테고리 수정 폼
   */
  @Transactional
  public void updateMenuCategory(Partner partner, Long storeId, Long menuCategoryId,
      MenuCategoryDTO menuCategoryDTO) {
    Store store = storeService.findStore(storeId);
    storeService.validateStoreOwner(partner, store);
    validateStoreHasMenuCategory(store, menuCategoryId);

    MenuCategory menuCategory = getMenuCategoryById(menuCategoryId);
    updateMenuCategoryDetails(menuCategory, menuCategoryDTO, store);
  }

  /**
   * 메뉴 카테고리 등록 시 메뉴 카테고리 개수 검증
   *
   * @param menuCategories 메뉴 카테고리 리스트
   */
  private void validateMenuCategoryCount(
      List<MenuCategoryRegisterForm.MenuCategoryDTO> menuCategories) {
    if (menuCategories.isEmpty() || menuCategories.size() > 5) {
      throw new RestApiException(ErrorCode.INVALID_MENU_CATEGORY_COUNT);
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
        .orElseThrow(() -> new RestApiException(ErrorCode.MENU_CATEGORY_NOT_FOUND));
  }


  /**
   * 가게에 메뉴 카테고리가 존재하는지 검증
   *
   * @param store          가게
   * @param menuCategoryId 메뉴 카테고리 ID
   */
  private void validateStoreHasMenuCategory(Store store, Long menuCategoryId) {
    boolean hasMenuCategory = store.getMenuCategories().stream()
        .anyMatch(menuCategory -> menuCategory.getMenuCategoryId().equals(menuCategoryId));
    if (!hasMenuCategory) {
      throw new RestApiException(ErrorCode.MENU_CATEGORY_NOT_FOUND);
    }
  }

  /**
   * 메뉴 카테고리에 메뉴가 존재하는지 검증
   *
   * @param menuCategory 메뉴 카테고리
   */

  private void validateMenuCategoryHasNoMenus(MenuCategory menuCategory) {
    if (!menuCategory.getMenus().isEmpty()) {
      throw new RestApiException(ErrorCode.MENU_CATEGORY_HAS_MENUS);
    }
  }


  /**
   * 메뉴 카테고리 정보 업데이트
   *
   * @param menuCategory 메뉴 카테고리
   * @param updateForm   업데이트 폼
   * @param store        가게
   */
  private void updateMenuCategoryDetails(MenuCategory menuCategory,
      MenuCategoryDTO updateForm, Store store) {

    // 메뉴 카테고리 이름 변경
    if (!menuCategory.getCategoryName().equals(updateForm.getCategoryName())) {
      menuCategory.updateCategoryName(updateForm.getCategoryName());
    }

    // 메뉴 카테고리 순서 변경
    if (!menuCategory.getDisplayOrder().equals(updateForm.getDisplayOrder())) {
      menuCategory.updateDisplayOrder(updateForm.getDisplayOrder());
      reorderMenuCategories(store, menuCategory, updateForm.getDisplayOrder());
    }
  }

  /**
   * 메뉴 카테고리 순서 재정렬
   *
   * @param store           가게
   * @param updatedCategory 업데이트된 카테고리
   * @param newOrder        새로운 순서
   */
  private void reorderMenuCategories(Store store, MenuCategory updatedCategory, int newOrder) {
    // 새로운 순서보다 크거나 같은 카테고리들의 순서를 1씩 증가시킴
    store.getMenuCategories().stream()
        .filter(mc -> !mc.getMenuCategoryId().equals(updatedCategory.getMenuCategoryId()))
        .filter(mc -> mc.getDisplayOrder() >= newOrder)
        .forEach(mc -> mc.updateDisplayOrder(mc.getDisplayOrder() + 1));
  }
}
