package jyang.deliverydotdot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jyang.deliverydotdot.dto.UserJoinForm;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@Tag(name = "User API", description = "유저 전용 API")
public class UserController {

  @Operation(summary = "유저 회원가입", description = "유저 등록 폼으로 회원가입")
  @PostMapping("/join")
  public ResponseEntity<?> joinProcess(
      @Valid @RequestBody UserJoinForm joinForm
  ) {
    System.out.println(joinForm);
    return ResponseEntity.ok().build();
  }

}
