package jyang.deliverydotdot.controller;

import static org.springframework.http.HttpStatus.CREATED;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jyang.deliverydotdot.domain.Partner;
import jyang.deliverydotdot.dto.response.SuccessResponse;
import jyang.deliverydotdot.dto.store.StoreRegisterForm;
import jyang.deliverydotdot.dto.store.StoreUpdateForm;
import jyang.deliverydotdot.security.AuthenticationFacade;
import jyang.deliverydotdot.service.PartnerService;
import jyang.deliverydotdot.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/stores")
@Tag(name = "Store API", description = "가게 관련 API")
public class StoreController {

  private final StoreService storeService;

  private final PartnerService partnerService;

  private final AuthenticationFacade authenticationFacade;

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


}
