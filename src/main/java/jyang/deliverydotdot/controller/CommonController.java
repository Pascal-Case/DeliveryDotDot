package jyang.deliverydotdot.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/common")
@Tag(name = "Common API", description = "공통 API")
public class CommonController {


  @GetMapping()
  public String common() {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().iterator()
        .next().getAuthority();

    return "Hello, " + role + " " + username;
  }
}
