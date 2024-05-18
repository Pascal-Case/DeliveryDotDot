package jyang.deliverydotdot.dto.store;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MenuRegisterForm {

  @Schema(description = "메뉴 카테고리 ID", example = "1")
  @NotNull(message = "메뉴 카테고리 ID를 입력해 주세요.")
  private Long menuCategoryId;

  @Schema(description = "메뉴 이름", example = "떡볶이 세트")
  @NotBlank(message = "메뉴 이름을 입력해 주세요.")
  private String menuName;

  @Schema(description = "메뉴 가격", example = "10000")
  @NotNull(message = "메뉴 가격을 입력해 주세요.")
  private Integer price;

  @Schema(description = "메뉴 설명", example = "맛있는 메뉴")
  private String menuDescription;

  @Schema(description = "메뉴 사진")
  private MultipartFile menuImage;

}
