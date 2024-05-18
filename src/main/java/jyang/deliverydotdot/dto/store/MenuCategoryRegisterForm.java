package jyang.deliverydotdot.dto.store;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MenuCategoryRegisterForm {

  private List<MenuCategoryDTO> menuCategories;
  
  @Getter
  @Setter
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  public static class MenuCategoryDTO {

    @Schema(description = "메뉴 카테고리 이름", example = "메인메뉴")
    @NotBlank(message = "메뉴 카테고리 이름을 입력해 주세요.")
    private String categoryName;
  }
}
