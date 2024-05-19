package jyang.deliverydotdot.config;

import java.util.List;
import jyang.deliverydotdot.oauth2.CustomSuccessHandler;
import jyang.deliverydotdot.security.CustomAccessDeniedHandler;
import jyang.deliverydotdot.security.CustomAuthenticationEntryPoint;
import jyang.deliverydotdot.security.JwtAuthenticationFilter;
import jyang.deliverydotdot.security.JwtTokenProvider;
import jyang.deliverydotdot.security.PartnerLoginFilter;
import jyang.deliverydotdot.security.RiderLoginFilter;
import jyang.deliverydotdot.security.TokenExceptionFilter;
import jyang.deliverydotdot.security.UserLoginFilter;
import jyang.deliverydotdot.service.CustomOAuth2Service;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final AuthenticationConfiguration authenticationConfiguration;

  private final JwtTokenProvider jwtTokenProvider;

  private final CustomOAuth2Service customOAuth2Service;

  private final CustomSuccessHandler customSuccessHandler;

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
      throws Exception {
    return configuration.getAuthenticationManager();
  }

  @Bean
  public BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain commonFilterChain(HttpSecurity http) throws Exception {
    http = configureCommonSecuritySettings(http); // 공통 보안 설정 적용
    http = commonCorsConfig(http);

    http
        .securityMatchers(
            auth -> auth
                .requestMatchers("/api/v1/common/**", "/api/v1/stores", "/api/v1/stores/**",
                    "/api/v1/reviews/**")

        )

        .addFilterBefore(
            new JwtAuthenticationFilter(jwtTokenProvider),
            UsernamePasswordAuthenticationFilter.class)

        .authorizeHttpRequests(request -> request
            .requestMatchers("/api/v1/common/**").permitAll()
            .requestMatchers("/error", "/favicon.ico", "/swagger-ui/**", "/v3/**").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/v1/stores/**").hasRole("PARTNER")
            .requestMatchers(HttpMethod.PUT, "/api/v1/stores/**").hasRole("PARTNER")
            .requestMatchers(HttpMethod.DELETE, "/api/v1/stores", "/api/v1/stores/**")
            .hasRole("PARTNER")
            .requestMatchers(HttpMethod.GET, "/api/v1/stores/**").permitAll()
            .anyRequest().authenticated()

        )
        .exceptionHandling(exception -> exception
            .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
            .accessDeniedHandler(new CustomAccessDeniedHandler())
        )
    ;
    return http.build();
  }

  /**
   * 사용자 관련 API 보안 설정
   */
  @Bean
  public SecurityFilterChain userSecurityFilterChain(HttpSecurity http) throws Exception {
    http = configureCommonSecuritySettings(http); // 공통 보안 설정 적용
    http = commonCorsConfig(http);
    http
        .securityMatchers(
            auth -> auth.requestMatchers("/api/v1/users/**", "/oauth2/**", "/login/**"))

        .oauth2Login(oauth2 -> oauth2
            .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig
                .userService(customOAuth2Service))
            .successHandler(customSuccessHandler)
        )

        .addFilterBefore(
            new JwtAuthenticationFilter(jwtTokenProvider), UserLoginFilter.class) // jwt 검증 필터 추가
        .addFilterBefore(new TokenExceptionFilter(), JwtAuthenticationFilter.class)
        .addFilterBefore(new UserLoginFilter(authenticationManager(authenticationConfiguration),
            jwtTokenProvider), UsernamePasswordAuthenticationFilter.class) // 로그인 필터 추가

        .authorizeHttpRequests(request -> request
            .requestMatchers("/api/v1/users/auth/**").permitAll() // 로그인, 회원가입 허용
            .requestMatchers("/api/v1/users/").hasRole("USER")
            .anyRequest().authenticated()
        )

        .exceptionHandling(exception -> exception
            .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
            .accessDeniedHandler(new CustomAccessDeniedHandler())
        )
    ;
    return http.build();
  }

  /**
   * 파트너 관련 API 보안 설정
   */
  @Bean
  public SecurityFilterChain partnerSecurityFilterChain(HttpSecurity http) throws Exception {
    http = configureCommonSecuritySettings(http); // 공통 보안 설정 적용
    http = commonCorsConfig(http);

    http
        .securityMatchers(auth -> auth.requestMatchers("/api/v1/partners/**"))

        // JWT authentication filter -> PartnerLoginFilter -> UsernamePasswordAuthenticationFilter 순서로 필터 적용
        .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
            PartnerLoginFilter.class)
        .addFilterBefore(new PartnerLoginFilter(authenticationManager(authenticationConfiguration),
            jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)

        .authorizeHttpRequests(request -> request
            .requestMatchers("/api/v1/partners/auth/**").permitAll()
            .anyRequest().authenticated()

        );
    return http.build();
  }

  /**
   * 라이더 관련 API 보안 설정
   */
  @Bean
  public SecurityFilterChain riderSecurityFilterChain(HttpSecurity http) throws Exception {
    http = configureCommonSecuritySettings(http); // 공통 보안 설정 적용
    http = commonCorsConfig(http);

    http
        .securityMatchers(auth -> auth.requestMatchers("/api/v1/riders/**"))

        // JWT authentication filter -> RiderLoginFilter -> UsernamePasswordAuthenticationFilter 순서로 필터 적용
        .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
            RiderLoginFilter.class)
        .addFilterBefore(new RiderLoginFilter(authenticationManager(authenticationConfiguration),
            jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)

        .authorizeHttpRequests(request -> request
            .requestMatchers("/api/v1/riders/auth/**").permitAll()
            .anyRequest().authenticated()
        );
    return http.build();
  }

  // 공통 보안 설정
  private HttpSecurity configureCommonSecuritySettings(HttpSecurity http) throws Exception {
    return http
        .csrf(AbstractHttpConfigurer::disable) // csrf 비활성화
        .formLogin(AbstractHttpConfigurer::disable) // form 로그인 방식 비활성화
        .httpBasic(AbstractHttpConfigurer::disable) // http basic 인증 비활성화
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 비활성화
        ;
  }

  // 공통 CORS 설정
  public HttpSecurity commonCorsConfig(HttpSecurity http) throws Exception {
    return http
        .cors(corsCustomizer -> corsCustomizer.configurationSource(request -> {
          CorsConfiguration configuration = new CorsConfiguration();
          configuration.setAllowedOrigins(List.of("*"));
          configuration.setAllowedMethods(List.of("*"));
          configuration.setAllowedHeaders(List.of("*"));
          configuration.setExposedHeaders(List.of("Set-Cookie"));
          configuration.setExposedHeaders(List.of("Authorization"));
          configuration.setAllowCredentials(true);
          configuration.setMaxAge(3600L);
          return configuration;
        }));
  }
}
