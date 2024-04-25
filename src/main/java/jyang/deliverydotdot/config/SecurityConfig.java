package jyang.deliverydotdot.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  @Bean
  public BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable)
        .cors(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(request -> request
                .requestMatchers("/swagger-ui/*", "/v3/**").permitAll()
                .requestMatchers("/api/v1/common/*").permitAll()
                .requestMatchers("/api/v1/common/*/*").permitAll()
//            .requestMatchers("/api/v1/users").hasRole("USER")
//            .requestMatchers("/api/v1/partners").hasRole("PARTNER")
//            .requestMatchers("/api/v1/riders").hasRole("RIDER")
                .anyRequest().authenticated()
        );
    return http.build();
  }

}
