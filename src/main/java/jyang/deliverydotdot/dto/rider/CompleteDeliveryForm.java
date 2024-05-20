package jyang.deliverydotdot.dto.rider;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompleteDeliveryForm {

  @Schema(description = "배달 완료 사진")
  private MultipartFile deliveryCompleteImage;

}
