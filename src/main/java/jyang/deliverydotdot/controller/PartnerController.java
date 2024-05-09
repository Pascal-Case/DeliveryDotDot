package jyang.deliverydotdot.controller;

import static org.springframework.http.HttpStatus.CREATED;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jyang.deliverydotdot.dto.partner.PartnerJoinForm;
import jyang.deliverydotdot.dto.response.SuccessResponse;
import jyang.deliverydotdot.service.PartnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/partners")
@RequiredArgsConstructor
@Tag(name = "Partner API", description = "파트너 관련 API")
public class PartnerController {

  private final PartnerService partnerService;

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
}
