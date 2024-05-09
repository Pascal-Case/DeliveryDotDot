package jyang.deliverydotdot.controller;

import static org.springframework.http.HttpStatus.CREATED;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jyang.deliverydotdot.dto.response.SuccessResponse;
import jyang.deliverydotdot.dto.rider.RiderJoinForm;
import jyang.deliverydotdot.service.RiderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/riders")
@RequiredArgsConstructor
@Tag(name = "Rider API", description = "라이더 관련 API")
public class RiderController {

  private final RiderService riderService;

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

}
