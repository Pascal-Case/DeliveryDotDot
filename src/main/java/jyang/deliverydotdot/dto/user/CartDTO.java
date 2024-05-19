package jyang.deliverydotdot.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.Set;
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
public class CartDTO {

  @Schema(description = "장바구니 아이템 목록")
  private Set<CartItemDTO> cartItems;

  @Getter
  @Setter
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  public static class CartItemDTO {

    @Schema(description = "메뉴 ID", example = "1")
    @NotNull(message = "메뉴 ID를 입력해 주세요.")
    private Long menuId;

    @Schema(description = "수량", example = "2")
    @NotNull(message = "수량을 입력해 주세요.")
    private Integer quantity;
  }

}
