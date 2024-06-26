package jyang.deliverydotdot.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsMvcConfig implements WebMvcConfigurer {

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
        .allowedOrigins("/**") // TODO: 추후 수정 필요
        .allowedMethods("*")
        .allowedHeaders("Authorization", "Content-Type")
        .exposedHeaders("Authorization", "Set-Cookie")
        .allowCredentials(true)
        .maxAge(3600);

  }
}
