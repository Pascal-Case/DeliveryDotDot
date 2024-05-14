package jyang.deliverydotdot.controller;

import static org.springframework.http.HttpStatus.CREATED;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jyang.deliverydotdot.dto.response.SuccessResponse;
import jyang.deliverydotdot.dto.user.UserJoinForm;
import jyang.deliverydotdot.dto.user.UserUpdateForm;
import jyang.deliverydotdot.security.AuthenticationFacade;
import jyang.deliverydotdot.service.UserService;
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
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Tag(name = "User API", description = "유저 관련 API")
public class UserController {

  private final UserService userService;

  private final AuthenticationFacade authenticationFacade;

  @Operation(summary = "유저 회원가입", description = "유저 등록 폼으로 회원가입")
  @PostMapping("/auth/join")
  public ResponseEntity<SuccessResponse<?>> userJoinProcess(
      @RequestBody @Valid UserJoinForm joinForm
  ) {
    userService.registerUser(joinForm);

    return ResponseEntity.status(CREATED).body(
        SuccessResponse.of("유저를 성공적으로 생성했습니다.")
    );
  }

  @Operation(summary = "유저 정보 조회", description = "유저 정보 조회")
  @GetMapping()
  public ResponseEntity<SuccessResponse<?>> getUserInfo(
  ) {
    return ResponseEntity.ok(
        SuccessResponse.of(userService.getUserInfo(authenticationFacade.getUsername())));
  }

  @Operation(summary = "유저 정보 수정", description = "유저 정보 수정")
  @PutMapping()
  public ResponseEntity<SuccessResponse<?>> updateUserInfo(
      @RequestBody @Valid UserUpdateForm updateForm
  ) {
    userService.updateUserInfo(authenticationFacade.getUsername(), updateForm);
    return ResponseEntity.ok(SuccessResponse.of("유저 정보를 성공적으로 수정했습니다."));
  }

  @Operation(summary = "유저 삭제", description = "유저 삭제")
  @DeleteMapping()
  public ResponseEntity<SuccessResponse<?>> deleteUser(
  ) {
    userService.deleteByLoginId(authenticationFacade.getUsername());
    return ResponseEntity.ok(SuccessResponse.of("유저를 성공적으로 삭제했습니다."));
  }
}
