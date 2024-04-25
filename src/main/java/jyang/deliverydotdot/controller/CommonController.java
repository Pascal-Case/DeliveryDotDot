package jyang.deliverydotdot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jyang.deliverydotdot.dto.UserJoinForm;
import jyang.deliverydotdot.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/common")
@Tag(name = "Common API", description = "공통 API")
public class CommonController {

  private final UserService userService;

  @Operation(summary = "유저 회원가입", description = "유저 등록 폼으로 회원가입")
  @PostMapping("/users/join")
  public ResponseEntity<?> userJoinProcess(
      @RequestBody @Valid UserJoinForm joinForm
  ) {
    System.out.println(joinForm);
    userService.registerUser(joinForm);
    return ResponseEntity.ok().build();
  }
}
