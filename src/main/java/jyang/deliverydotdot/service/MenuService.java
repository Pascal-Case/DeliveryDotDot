package jyang.deliverydotdot.service;

import jyang.deliverydotdot.domain.Menu;
import jyang.deliverydotdot.domain.MenuCategory;
import jyang.deliverydotdot.domain.Partner;
import jyang.deliverydotdot.domain.Store;
import jyang.deliverydotdot.dto.store.MenuRegisterForm;
import jyang.deliverydotdot.repository.MenuRepository;
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
  
}
