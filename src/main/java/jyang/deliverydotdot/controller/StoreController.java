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
import jyang.deliverydotdot.service.OrderService;
import jyang.deliverydotdot.service.PartnerService;
import jyang.deliverydotdot.service.StoreService;
import jyang.deliverydotdot.type.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

  private final OrderService orderService;

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

  @Operation(summary = "주문 목록 조회", description = "주문 목록 조회")
  @GetMapping("/{storeId}/orders")
  public ResponseEntity<SuccessResponse<?>> getOrders(
      @PathVariable Long storeId,
      @PageableDefault Pageable pageable,
      @RequestParam(required = false) OrderStatus status,
      @RequestParam(required = false) String query
  ) {
    Partner partner = partnerService.getPartnerByLoginId(authenticationFacade.getUsername());
    return ResponseEntity.ok(
        SuccessResponse.of(
            "주문 목록을 성공적으로 조회했습니다.",
            orderService.getStoreOrders(partner, storeId, pageable, status, query)));
  }

  @Operation(summary = "주문 수락", description = "주문 수락")
  @PutMapping("/{storeId}/orders/{orderId}/approve")
  public ResponseEntity<SuccessResponse<?>> approveOrder(
      @PathVariable Long storeId,
      @PathVariable Long orderId
  ) {
    Partner partner = partnerService.getPartnerByLoginId(authenticationFacade.getUsername());
    orderService.approveOrder(partner, storeId, orderId);

    return ResponseEntity.ok(
        SuccessResponse.of("주문을 성공적으로 수락했습니다.")
    );
  }

  @Operation(summary = "주문 거절", description = "주문 거절")
  @PutMapping("/{storeId}/orders/{orderId}/reject")
  public ResponseEntity<SuccessResponse<?>> rejectOrder(
      @PathVariable Long storeId,
      @PathVariable Long orderId
  ) {
    Partner partner = partnerService.getPartnerByLoginId(authenticationFacade.getUsername());
    orderService.rejectOrder(partner, storeId, orderId);

    return ResponseEntity.ok(
        SuccessResponse.of("주문의 상태를 성공적으로 변경했습니다.")
    );
  }

  @Operation(summary = "주문 취소", description = "주문 취소")
  @PutMapping("/{storeId}/orders/{orderId}/cancel")
  public ResponseEntity<SuccessResponse<?>> cancelOrder(
      @PathVariable Long storeId,
      @PathVariable Long orderId
  ) {
    Partner partner = partnerService.getPartnerByLoginId(authenticationFacade.getUsername());
    orderService.cancelOrderByPartner(partner, storeId, orderId);

    return ResponseEntity.ok(
        SuccessResponse.of("주문을 성공적으로 취소했습니다.")
    );
  }

  @Operation(summary = "조리 시작", description = "조리 시작")
  @PutMapping("/{storeId}/orders/{orderId}/cook")
  public ResponseEntity<SuccessResponse<?>> cookOrder(
      @PathVariable Long storeId,
      @PathVariable Long orderId
  ) {
    Partner partner = partnerService.getPartnerByLoginId(authenticationFacade.getUsername());
    orderService.cookOrder(partner, storeId, orderId);

    return ResponseEntity.ok(
        SuccessResponse.of("주문의 상태를 성공적으로 변경했습니다.")
    );
  }

  @Operation(summary = "조리 완료", description = "주문 완료")
  @PutMapping("/{storeId}/orders/{orderId}/complete")
  public ResponseEntity<SuccessResponse<?>> completeOrder(
      @PathVariable Long storeId,
      @PathVariable Long orderId
  ) {
    Partner partner = partnerService.getPartnerByLoginId(authenticationFacade.getUsername());
    orderService.completeOrder(partner, storeId, orderId);

    return ResponseEntity.ok(
        SuccessResponse.of("주문의 상태를 성공적으로 변경했습니다.")
    );
  }


}
