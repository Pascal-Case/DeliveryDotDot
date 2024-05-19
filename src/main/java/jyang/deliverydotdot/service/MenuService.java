package jyang.deliverydotdot.service;

import jyang.deliverydotdot.domain.Menu;
import jyang.deliverydotdot.domain.MenuCategory;
import jyang.deliverydotdot.domain.Partner;
import jyang.deliverydotdot.domain.Store;
import jyang.deliverydotdot.dto.store.MenuRegisterForm;
import jyang.deliverydotdot.exception.RestApiException;
import jyang.deliverydotdot.repository.MenuRepository;
import jyang.deliverydotdot.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuService {


  private final MenuRepository menuRepository;

  private final StoreService storeService;

  private final MenuCategoryService menuCategoryService;

  private final S3Service s3Service;


  /**
   * 메뉴 등록
   *
   * @param partner          파트너
   * @param storeId          가게 ID
   * @param menuRegisterForm 메뉴 등록 폼
   */
  @Transactional
  public void registerMenu(Partner partner, Long storeId, MenuRegisterForm menuRegisterForm) {

    // 가게 조회
    Store store = storeService.findStore(storeId);

    // 가게 소유자인지 확인
    storeService.validateStoreOwner(partner, store);

    // 메뉴 카테고리 조회
    MenuCategory menuCategory =
        menuCategoryService.getMenuCategoryById(menuRegisterForm.getMenuCategoryId());

    // 메뉴 사진 업로드
    String menuImageURL = s3Service.uploadMenuImage(menuRegisterForm.getMenuImage());

    // 메뉴 등록
    menuRepository.save(Menu.builder()
        .store(store)
        .menuCategory(menuCategory)
        .menuName(menuRegisterForm.getMenuName())
        .price(menuRegisterForm.getPrice())
        .menuDescription(menuRegisterForm.getMenuDescription())
        .menuImageUrl(menuImageURL)
        .build());
  }

  /**
   * 메뉴 삭제
   *
   * @param partner 파트너
   * @param storeId 가게 ID
   * @param menuId  메뉴 ID
   */

  @Transactional
  public void deleteMenu(Partner partner, Long storeId, Long menuId) {

    // 가게 조회
    Store store = storeService.findStore(storeId);

    // 가게 소유자인지 확인
    storeService.validateStoreOwner(partner, store);

    // 메뉴 조회
    Menu menu = getMenuById(menuId);

    // 메뉴가 가게에 속해 있는지 확인
    if (!menu.getStore().getStoreId().equals(storeId)) {
      throw new RestApiException(ErrorCode.MENU_NOT_FOUND);
    }

    // 메뉴 삭제
    menuRepository.delete(menu);

  }

  /**
   * 메뉴 수정
   *
   * @param partner        파트너
   * @param storeId        가게 ID
   * @param menuId         메뉴 ID
   * @param menuUpdateForm 메뉴 수정 폼
   */
  @Transactional
  public void updateMenu(Partner partner, Long storeId, Long menuId,
      MenuRegisterForm menuUpdateForm) {

    // 가게 조회
    Store store = storeService.findStore(storeId);

    // 가게 소유자인지 확인
    storeService.validateStoreOwner(partner, store);

    // 메뉴 조회
    Menu menu = getMenuById(menuId);

    // 메뉴가 가게에 속해 있는지 확인
    if (!menu.getStore().getStoreId().equals(storeId)) {
      throw new RestApiException(ErrorCode.MENU_NOT_FOUND);
    }

    // 기존 메뉴 사진 삭제
    s3Service.delete(menu.getMenuImageUrl());

    // 메뉴 사진 업로드
    String menuImageURL = s3Service.uploadMenuImage(menuUpdateForm.getMenuImage());

    // 메뉴 수정
    menu.updateMenu(menuUpdateForm, menuImageURL);

  }

  /**
   * 메뉴 조회
   *
   * @param menuId 메뉴 ID
   * @return 메뉴
   */
  public Menu getMenuById(Long menuId) {
    return menuRepository.findById(menuId)
        .orElseThrow(() -> new RestApiException(ErrorCode.MENU_NOT_FOUND));
  }
}
