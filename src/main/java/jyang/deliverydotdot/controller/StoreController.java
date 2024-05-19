package jyang.deliverydotdot.controller;

import static org.springframework.http.HttpStatus.CREATED;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jyang.deliverydotdot.domain.Partner;
import jyang.deliverydotdot.dto.response.SuccessResponse;
import jyang.deliverydotdot.dto.store.MenuCategoryDTO;
import jyang.deliverydotdot.dto.store.MenuCategoryRegisterForm;
import jyang.deliverydotdot.dto.store.MenuRegisterForm;
import jyang.deliverydotdot.dto.store.StoreRegisterForm;
import jyang.deliverydotdot.dto.store.StoreUpdateForm;
import jyang.deliverydotdot.security.AuthenticationFacade;
import jyang.deliverydotdot.service.MenuCategoryService;
import jyang.deliverydotdot.service.MenuService;
import jyang.deliverydotdot.service.PartnerService;
import jyang.deliverydotdot.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/stores")
@Tag(name = "Store API", description = "가게 관련 API")
public class StoreController {

  private final StoreService storeService;

  private final PartnerService partnerService;

  private final MenuService menuService;

  private final MenuCategoryService menuCategoryService;

  private final AuthenticationFacade authenticationFacade;

  @Operation(summary = "가게 등록", description = "가게 등록 폼으로 가게 등록")
  @PostMapping
  public ResponseEntity<SuccessResponse<?>> registerStore(
      @ModelAttribute @Valid StoreRegisterForm storeRegisterForm
  ) {
    Partner partner = partnerService.getPartnerByLoginId(authenticationFacade.getUsername());
    storeService.registerStore(partner, storeRegisterForm);

    return ResponseEntity.status(CREATED).body(
        SuccessResponse.of("가게를 성공적으로 등록했습니다.")
    );
  }

  @Operation(summary = "가게 정보 수정", description = "가게 정보 수정")
  @PutMapping
  public ResponseEntity<SuccessResponse<?>> updateStore(
      @ModelAttribute @Valid StoreUpdateForm storeUpdateForm
  ) {
    Partner partner = partnerService.getPartnerByLoginId(authenticationFacade.getUsername());
    storeService.updateStore(partner, storeUpdateForm);

    return ResponseEntity.ok(
        SuccessResponse.of("가게 정보를 성공적으로 수정했습니다.")
    );
  }

  @Operation(summary = "가게 삭제", description = "가게 삭제")
  @DeleteMapping("/{storeId}")
  public ResponseEntity<SuccessResponse<?>> deleteStore(
      @PathVariable Long storeId
  ) {
    Partner partner = partnerService.getPartnerByLoginId(authenticationFacade.getUsername());
    storeService.deleteStore(partner, storeId);

    return ResponseEntity.ok(
        SuccessResponse.of("가게를 성공적으로 삭제했습니다.")
    );
  }

  @Operation(summary = "메뉴 카테고리 등록", description = "메뉴 카테고리 등록")
  @PostMapping("/{storeId}/menuCategories")
  public ResponseEntity<SuccessResponse<?>> registerMenuCategory(
      @PathVariable Long storeId,
      @RequestBody @Valid MenuCategoryRegisterForm menuCategoryRegisterForm
  ) {
    Partner partner = partnerService.getPartnerByLoginId(authenticationFacade.getUsername());
    menuCategoryService.registerMenuCategory(partner, storeId, menuCategoryRegisterForm);

    return ResponseEntity.status(CREATED).body(
        SuccessResponse.of("메뉴 카테고리를 성공적으로 등록했습니다.")
    );
  }

  @Operation(summary = "메뉴 카테고리 삭제", description = "메뉴 카테고리 삭제")
  @DeleteMapping("/{storeId}/menuCategories/{menuCategoryId}")
  public ResponseEntity<SuccessResponse<?>> deleteMenuCategory(
      @PathVariable Long storeId,
      @PathVariable Long menuCategoryId
  ) {
    Partner partner = partnerService.getPartnerByLoginId(authenticationFacade.getUsername());
    menuCategoryService.deleteMenuCategory(partner, storeId, menuCategoryId);

    return ResponseEntity.ok(
        SuccessResponse.of("메뉴 카테고리를 성공적으로 삭제했습니다.")
    );
  }

  @Operation(summary = "단일 메뉴 카테고리 추가", description = "단일 메뉴 카테고리 추가")
  @PostMapping("/{storeId}/menuCategories/add")
  public ResponseEntity<SuccessResponse<?>> addMenuCategory(
      @PathVariable Long storeId,
      @RequestBody @Valid MenuCategoryDTO menuCategoryDTO
  ) {
    Partner partner = partnerService.getPartnerByLoginId(authenticationFacade.getUsername());
    menuCategoryService.addSingleMenuCategory(partner, storeId, menuCategoryDTO);

    return ResponseEntity.status(CREATED).body(
        SuccessResponse.of("메뉴 카테고리를 성공적으로 등록했습니다.")
    );
  }

  @Operation(summary = "메뉴 카테고리 수정", description = "메뉴 카테고리 수정")
  @PutMapping("/{storeId}/menuCategories/{menuCategoryId}")
  public ResponseEntity<SuccessResponse<?>> updateMenuCategory(
      @PathVariable Long storeId,
      @PathVariable Long menuCategoryId,
      @RequestBody @Valid MenuCategoryDTO menuCategoryDTO
  ) {
    Partner partner = partnerService.getPartnerByLoginId(authenticationFacade.getUsername());
    menuCategoryService.updateMenuCategory(partner, storeId, menuCategoryId,
        menuCategoryDTO);

    return ResponseEntity.ok(
        SuccessResponse.of("메뉴 카테고리를 성공적으로 수정했습니다.")
    );
  }

  @Operation(summary = "메뉴 등록", description = "메뉴 등록 폼으로 메뉴 등록")
  @PostMapping("/{storeId}/menus")
  public ResponseEntity<SuccessResponse<?>> registerMenu(
      @PathVariable Long storeId,
      @ModelAttribute @Valid MenuRegisterForm menuRegisterForm
  ) {
    Partner partner = partnerService.getPartnerByLoginId(authenticationFacade.getUsername());
    menuService.registerMenu(partner, storeId, menuRegisterForm);

    return ResponseEntity.status(CREATED).body(
        SuccessResponse.of("메뉴를 성공적으로 등록했습니다.")
    );
  }

  @Operation(summary = "메뉴 삭제", description = "메뉴 삭제")
  @DeleteMapping("/{storeId}/menus/{menuId}")
  public ResponseEntity<SuccessResponse<?>> deleteMenu(
      @PathVariable Long storeId,
      @PathVariable Long menuId
  ) {
    Partner partner = partnerService.getPartnerByLoginId(authenticationFacade.getUsername());
    menuService.deleteMenu(partner, storeId, menuId);

    return ResponseEntity.ok(
        SuccessResponse.of("메뉴를 성공적으로 삭제했습니다.")
    );
  }

  @Operation(summary = "메뉴 수정", description = "메뉴 수정")
  @PutMapping("/{storeId}/menus/{menuId}")
  public ResponseEntity<SuccessResponse<?>> updateMenu(
      @PathVariable Long storeId,
      @PathVariable Long menuId,
      @ModelAttribute @Valid MenuRegisterForm menuUpdateForm
  ) {
    Partner partner = partnerService.getPartnerByLoginId(authenticationFacade.getUsername());
    menuService.updateMenu(partner, storeId, menuId, menuUpdateForm);

    return ResponseEntity.ok(
        SuccessResponse.of("메뉴를 성공적으로 수정했습니다.")
    );
  }


}
