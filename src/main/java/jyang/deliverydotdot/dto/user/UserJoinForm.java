package jyang.deliverydotdot.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class UserJoinForm {

  @Schema(description = "로그인 아이디", minLength = 8, maxLength = 20, example = "loginId123")
  @NotBlank(message = "아이디를 입력해 주세요.")
  @Size(min = 8, max = 20, message = "아이디는 8자 이상 20자 이하로 입력해 주세요.")
  private String loginId;

  @Schema(description = "비밀번호", minLength = 8, maxLength = 20, example = "password123")
  @NotBlank(message = "비밀번호를 입력해 주세요.")
  @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해 주세요.")
  private String password;

  @Schema(description = "이름", maxLength = 20, example = "김제로")
  @NotBlank(message = "이름을 입력해 주세요.")
  @Size(max = 20, message = "이름은 20자 이하로 입력해 주세요.")
  private String name;

  @Schema(description = "이메일", example = "abc@delverydotdot.com")
  @NotBlank(message = "이메일을 입력해 주세요.")
  @Email(message = "유효하지 않은 이메일 형식 입니다.")
  private String email;

  @Schema(description = "휴대전화 번호", example = "010-1234-5678")
  @NotBlank(message = "휴대전화 번호를 입력해 주세요.")
  @Pattern(regexp = "^010-\\d{4}-\\d{4}$", message = "유효하지 않은 휴대전화 번호 입니다.")
  private String phone;

  @Schema(description = "주소", example = "서울시 강남구 테헤란로 231")
  @NotBlank(message = "주소를 입력해 주세요.")
  private String address;
}
