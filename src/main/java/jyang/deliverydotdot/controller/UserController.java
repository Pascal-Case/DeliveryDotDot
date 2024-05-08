package jyang.deliverydotdot.controller;

import static org.springframework.http.HttpStatus.CREATED;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jyang.deliverydotdot.dto.UserJoinForm;
import jyang.deliverydotdot.dto.response.SuccessResponse;
import jyang.deliverydotdot.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Tag(name = "User API", description = "유저 전용 API")
public class UserController {

  private final UserService userService;

  @GetMapping("/my")
  public String my() {
    String username = SecurityContextHolder
        .getContext()
        .getAuthentication()
        .getName();

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    GrantedAuthority auth = authentication.getAuthorities().iterator().next();

    String role = auth.getAuthority();

    return "Main Controller : " + username + " " + role;
  }

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
      Authentication authentication
  ) {
    String username = authentication.getName();
    return ResponseEntity.ok(SuccessResponse.of(userService.getUserInfo(username)));
  }
}
