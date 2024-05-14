package jyang.deliverydotdot.controller;

import static org.springframework.http.HttpStatus.CREATED;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jyang.deliverydotdot.dto.partner.PartnerJoinForm;
import jyang.deliverydotdot.dto.partner.PartnerUpdateForm;
import jyang.deliverydotdot.dto.response.SuccessResponse;
import jyang.deliverydotdot.security.AuthenticationFacade;
import jyang.deliverydotdot.service.PartnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/partners")
@RequiredArgsConstructor
@Tag(name = "Partner API", description = "파트너 관련 API")
public class PartnerController {

  private final PartnerService partnerService;

  private final AuthenticationFacade authenticationFacade;

  @Operation(summary = "파트너 등록", description = "파트너 등록 폼으로 파트너 등록")
  @PostMapping("/auth/join")
  public ResponseEntity<SuccessResponse<?>> userJoinProcess(
      @RequestBody @Valid PartnerJoinForm joinForm
  ) {
    partnerService.registerPartner(joinForm);

    return ResponseEntity.status(CREATED).body(
        SuccessResponse.of("파트너를 성공적으로 등록했습니다.")
    );
  }

  @Operation(summary = "파트너 정보 조회", description = "파트너 정보 조회")
  @GetMapping()
  public ResponseEntity<SuccessResponse<?>> getPartnerInfo(
  ) {
    return ResponseEntity.ok(
        SuccessResponse.of(partnerService.getPartnerInfo(authenticationFacade.getUsername())));
  }

  @Operation(summary = "파트너 정보 수정", description = "파트너 정보 수정")
  @PutMapping()
  public ResponseEntity<SuccessResponse<?>> updatePartnerInfo(
      @RequestBody @Valid PartnerUpdateForm updateForm
  ) {
    partnerService.updatePartnerInfo(authenticationFacade.getUsername(), updateForm);
    return ResponseEntity.ok(SuccessResponse.of("파트너 정보를 성공적으로 수정했습니다."));
  }

  @Operation(summary = "파트너 삭제", description = "파트너 삭제")
  @DeleteMapping()
  public ResponseEntity<SuccessResponse<?>> deletePartner(
  ) {
    partnerService.deleteByLoginId(authenticationFacade.getUsername());
    return ResponseEntity.ok(SuccessResponse.of("파트너를 성공적으로 삭제했습니다."));
  }
}
