# Trouble Shooting

프로젝트를 진행하면서 발생한 문제점들과 해결과정을 서술합니다.

## @Component 어노테이션으로 커스텀 필터 등록시 발생할 수 있는 문제점

### 문제 상황

유저, 파트너, 라이더 엔티티에 대해 다른 로그인 필터 등록을 위해 4개(공통, 유저, 파트너, 라이더)의 SecurityFilterChain을 생성. 이 중
commonFilterChain에 swagger-ui를 제외한 모든 요청에 대해 인증을 요구하는 필터를 등록하였다. 그러나, swagger-ui 등 정적 리소스에 대한 요청에 대해
인증을 모두 permitAll로 설정하였음에도 불구하고, swagger-ui에 대한 요청에 대해 JwtAuthenticationFilter에서 이전에 테스트를 위해 등록한 JWT
토큰이 만료 되었다는 에러를
발생시키는 문제가 발생하였다.

### 해결 과정

![image](https://github.com/Pascal-Case/DeliveryDotDot/assets/152583754/2c6da96d-b312-4798-9dbe-308f1c42f57a)

![image](https://github.com/Pascal-Case/DeliveryDotDot/assets/152583754/1b1783be-f568-4cfe-88fb-8d868eb25073)

![image](https://github.com/Pascal-Case/DeliveryDotDot/assets/152583754/0aad623c-c6fb-4ae6-a501-21add3b5aebf)

- DEBUG 모드로 설정하여 로그를 확인한 결과, 위의 이미지와 같이 swagger-ui에 대한 요청에 대해 JWT 인증 필터가 적용되는 것을 확인하였다.
- 또한 설정한 대로 commonFilterChain에는 JwtAuthenticationFilter가 적용되지 않는 것을 확인하였다.
- JWT 인증 필터는 OncePerRequestFilter를 상속받아 구현하였고, OncePerRequestFilter는 http 요청이 들어올 때 한 번만 필터가 적용되도록
  하는 필터이다.
- 따라서, 이러한 필터는 SecurityFilterChain에 상관없이 모든 요청에 대해 적용되는것은 아닌지 의심하였다. 공식 문서를 확인하던 중 shouldNotFilter라는
  메소드를 통해 필터가 적용되지 않아야 하는 요청을 설정할 수 있다는 것을 확인하였다.
- shouldNotFilter 메소드를 오버라이딩하여 swagger-ui에 대한 요청에 대해 필터가 적용되지 않도록 설정해 보았다.
    ```java
    @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
      String[] excludePath = {"/swagger-ui/**", "/v3/**"};
      String path = request.getRequestURI();
      return Arrays.stream(excludePath).anyMatch(path::startsWith);
  }
  ```
- 하지만, 여전히 JwtAuthenticationFilter에 의해 예외가 발생하였다.
- SecurityFilterChain은 DelegateFilterProxy를 통해 필터를 적용하는데, 혹시 JwtAuthenticationFilter가
  DispatcherServlet의 서블릿 필터로
  적용되어
  swagger-ui에 대한 요청에 대해 필터가 적용되는 것은 아닌지 의심하였다.
- 검색을 통해 서블릿 필터의 목록을 확인하는 방법을 찾아보았고, 다음과 같이 확인하였다.
    ```java
    @Bean
  public ApplicationRunner applicationRunner() {
      return new ApplicationRunner() {
        @Autowired
        private ServletContext servletContext;

        @Override
        public void run(ApplicationArguments args) {
          System.out.println("=== Registered Filters ===");
          servletContext.getFilterRegistrations().forEach((filterName, filterRegistration) -> {
            System.out.println("Filter name: " + filterName);
            System.out.println("Filter class: " + filterRegistration.getClassName());
            filterRegistration.getInitParameters().forEach((paramName, paramValue) -> {
              System.out.println("Init param - " + paramName + ": " + paramValue);
          });
          System.out.println("----------------------------");
        });
      }
    };
  }
  ```
- 위의 코드를 통해 확인한 결과, JwtAuthenticationFilter가 DispatcherServlet의 서블릿 필터로 등록되어 있었음을 확인하였다.
  ![image](https://github.com/Pascal-Case/DeliveryDotDot/assets/152583754/6e31b4a6-5ca6-45ee-8eae-2a1d4a1abad1)
- 이후 추가 검색을 통해 @Component 어노테이션을 통해 필터를 등록할 경우, DispatcherServlet의 서블릿 필터로 등록되는 것을 확인하였다.
- 내가 등록한 JwtAuthenticationFilter의 경우 @Component 어노테이션을 통해 빈 등록을 하였고 SecurityConfig 클래스에서 생성자 주입을 통해
  필터를 등록하였다. 따라서
  이러한 문제가 발생한 것으로 판단하였다.

### 해결 방법

- @Component 어노테이션을 통해 빈 등록을 하지 않고, 적용하고자 하는 필터 순서에 new 키워드를 통해 필터를 생성하여 등록하였다.
- 그러자 내가 의도한 대로 swagger-ui에 대한 요청에 대해 JwtAuthenticationFilter가 적용되지 않는 것을 확인하였다.

### 정리

- @Component 어노테이션 등 커스텀 필터를 빈으로 등록할 경우, DispatcherServlet의 서블릿 필터로 등록되어 모든 요청에 대해 필터가 적용될 수 있다.
- 따라서, 필터를 등록할 때는 new 키워드를 통해 필터를 생성하여 등록하도록 하자.
- 또한, shouldNotFilter 메소드를 통해 필터를 적용시키지 않을 요청을 설정할 수 있다.