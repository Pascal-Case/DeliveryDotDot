package jyang.deliverydotdot.controller;

import static org.springframework.http.HttpStatus.CREATED;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jyang.deliverydotdot.domain.Rider;
import jyang.deliverydotdot.dto.response.SuccessResponse;
import jyang.deliverydotdot.dto.rider.RiderJoinForm;
import jyang.deliverydotdot.dto.rider.RiderUpdateForm;
import jyang.deliverydotdot.dto.rider.RiderUpdateForm.UpdateCurrentLocation;
import jyang.deliverydotdot.security.AuthenticationFacade;
import jyang.deliverydotdot.service.RiderService;
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
@RequestMapping("/api/v1/riders")
@RequiredArgsConstructor
@Tag(name = "Rider API", description = "라이더 관련 API")
public class RiderController {

  private final RiderService riderService;

  private final AuthenticationFacade authenticationFacade;

  @Operation(summary = "라이더 등록", description = "라이더 등록 폼으로 라이더 등록")
  @PostMapping("/auth/join")
  public ResponseEntity<SuccessResponse<?>> userJoinProcess(
      @RequestBody @Valid RiderJoinForm joinForm
  ) {
    riderService.registerRider(joinForm);

    return ResponseEntity.status(CREATED).body(
        SuccessResponse.of("라이더를 성공적으로 등록했습니다.")
    );
  }

  @Operation(summary = "라이더 정보 조회", description = "라이더 정보 조회")
  @GetMapping()
  public ResponseEntity<SuccessResponse<?>> getRiderInfo(
  ) {
    return ResponseEntity.ok(
        SuccessResponse.of(riderService.getRiderInfo(authenticationFacade.getUsername())));
  }

  @Operation(summary = "라이더 정보 수정", description = "라이더 정보 수정")
  @PutMapping()
  public ResponseEntity<SuccessResponse<?>> updateRiderInfo(
      @RequestBody @Valid RiderUpdateForm updateForm
  ) {
    riderService.updateRiderInfo(authenticationFacade.getUsername(), updateForm);
    return ResponseEntity.ok(SuccessResponse.of("라이더 정보를 성공적으로 수정했습니다."));
  }

  @Operation(summary = "라이더 삭제", description = "라이더 삭제")
  @DeleteMapping()
  public ResponseEntity<SuccessResponse<?>> deleteRider(
  ) {
    riderService.deleteByLoginId(authenticationFacade.getUsername());
    return ResponseEntity.ok(SuccessResponse.of("라이더를 성공적으로 삭제했습니다."));
  }

  @Operation(summary = "라이더 위치 정보 업데이트", description = "라이더 위치 정보 업데이트")
  @PutMapping("/location")
  public ResponseEntity<SuccessResponse<?>> updateRiderLocation(
      @RequestBody UpdateCurrentLocation updateForm
  ) {
    Rider rider = riderService.getRiderByLoginId(authenticationFacade.getUsername());
    riderService.updateRiderLocation(rider, updateForm);
    return ResponseEntity.ok(SuccessResponse.of("라이더 위치 정보를 성공적으로 수정했습니다."));
  }

  @Operation(summary = "배달 가능 주문 목록 조회", description = "배달 가능 주문 목록 조회")
  @GetMapping("/deliverable-orders")
  public ResponseEntity<SuccessResponse<?>> getDeliverableOrders(
  ) {
    Rider rider = riderService.getRiderByLoginId(authenticationFacade.getUsername());
    return ResponseEntity.ok(
        SuccessResponse.of(riderService.getDeliverableOrders(rider)));
  }

}
