package jyang.deliverydotdot.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI api() {
    return new OpenAPI()
        .info(
            new Info()
                .title("DeliveryDotDot")
                .description("배달 어플리케이션의 백엔드 API 입니다.")
        );
  }
}
