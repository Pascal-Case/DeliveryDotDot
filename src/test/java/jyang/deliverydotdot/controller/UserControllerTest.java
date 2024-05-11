package jyang.deliverydotdot.controller;

import static jyang.deliverydotdot.type.ErrorCode.NO_COORDINATES_FOUND_FOR_ADDRESS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import jyang.deliverydotdot.dto.UserJoinForm;
import jyang.deliverydotdot.exception.RestApiException;
import jyang.deliverydotdot.security.JwtTokenProvider;
import jyang.deliverydotdot.service.UserService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private UserService userService;

  @MockBean
  private JwtTokenProvider jwtTokenProvider;

  private static final String USER_API_URL = "/api/v1/users/";

  private String token;

  @BeforeEach
  public void setup() {
    token = "Bearer token";

    given(jwtTokenProvider.getUsername("token")).willReturn(token);
  }

  @Test
  void 고객생성_성공() throws Exception {
    //given
    UserJoinForm user = UserJoinForm.builder()
        .loginId("loginId123")
        .password("password123")
        .name("김제로")
        .email("abc@deliverydotdot.com")
        .phone("010-1234-5678")
        .address("서울시 강남구 테헤란로 231")
        .build();

    doNothing().when(userService).registerUser(any(UserJoinForm.class));

    //when
    //then
    mockMvc.perform(post(USER_API_URL + "auth/join")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(user)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.code").value("SUCCESS"))
    ;

  }

  @Test
  void 고객생성_실패_로그인아이디_미입력() throws Exception {
    //given
    UserJoinForm user = UserJoinForm.builder()
        .password("password123")
        .name("김제로")
        .email("abc@deliverydotdot.com")
        .phone("010-1234-5678")
        .address("서울시 강남구 테헤란로 231")
        .build();

    doNothing().when(userService).registerUser(any(UserJoinForm.class));

    //when
    //then
    mockMvc.perform(post(USER_API_URL + "auth/join")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(user)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
        .andExpect(jsonPath("$.errors").isArray())
        .andExpect(jsonPath("$.errors[0].field").value("loginId"))
        .andExpect(jsonPath("$.errors[0].message").value("아이디를 입력해 주세요."));
  }

  @Test
  void 고객생성_실패_로그인아이디_짧음() throws Exception {
    //given
    UserJoinForm user = UserJoinForm.builder()
        .loginId("loginId")
        .password("password123")
        .name("김제로")
        .email("abc@deliverydotdot.com")
        .phone("010-1234-5678")
        .address("서울시 강남구 테헤란로 231")
        .build();

    doNothing().when(userService).registerUser(any(UserJoinForm.class));

    //when
    //then
    mockMvc.perform(post(USER_API_URL + "auth/join")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(user)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
        .andExpect(jsonPath("$.errors").isArray())
        .andExpect(jsonPath("$.errors[0].field").value("loginId"))
        .andExpect(jsonPath("$.errors[0].message").value("아이디는 8자 이상 20자 이하로 입력해 주세요."));
  }

  @Test
  void 고객생성_실패_로그인아이디_길이초과() throws Exception {
    //given
    UserJoinForm user = UserJoinForm.builder()
        .loginId("loginId123123123123123")
        .password("password123")
        .name("김제로")
        .email("abc@deliverydotdot.com")
        .phone("010-1234-5678")
        .address("서울시 강남구 테헤란로 231")
        .build();

    doNothing().when(userService).registerUser(any(UserJoinForm.class));

    //when
    //then
    mockMvc.perform(post(USER_API_URL + "auth/join")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(user)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
        .andExpect(jsonPath("$.errors").isArray())
        .andExpect(jsonPath("$.errors[0].field").value("loginId"))
        .andExpect(jsonPath("$.errors[0].message").value("아이디는 8자 이상 20자 이하로 입력해 주세요."));
  }

  @Test
  void 고객생성_실패_이메일_미입력() throws Exception {
    //given
    UserJoinForm user = UserJoinForm.builder()
        .loginId("loginId123")
        .password("password123")
        .name("김제로")
        .phone("010-1234-5678")
        .address("서울시 강남구 테헤란로 231")
        .build();

    doNothing().when(userService).registerUser(any(UserJoinForm.class));

    //when
    //then
    mockMvc.perform(post(USER_API_URL + "auth/join")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(user)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
        .andExpect(jsonPath("$.errors").isArray())
        .andExpect(jsonPath("$.errors[0].field").value("email"))
        .andExpect(jsonPath("$.errors[0].message").value("이메일을 입력해 주세요."));
  }

  @Test
  void 고객생성_실패_이메일_유효하지않은이메일() throws Exception {
    //given
    UserJoinForm user = UserJoinForm.builder()
        .loginId("loginId123")
        .password("password123")
        .name("김제로")
        .email("invalidEmail")
        .phone("010-1234-5678")
        .address("서울시 강남구 테헤란로 231")
        .build();

    doNothing().when(userService).registerUser(any(UserJoinForm.class));

    //when
    //then
    mockMvc.perform(post(USER_API_URL + "auth/join")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(user)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
        .andExpect(jsonPath("$.errors").isArray())
        .andExpect(jsonPath("$.errors[0].field").value("email"))
        .andExpect(jsonPath("$.errors[0].message").value("유효하지 않은 이메일 형식 입니다."));
  }

  @Test
  void 고객생성_실패_휴대전화번호_미입력() throws Exception {
    //given
    UserJoinForm user = UserJoinForm.builder()
        .loginId("loginId123")
        .password("password123")
        .name("김제로")
        .email("abc@deliverydotdot.com")
        .address("서울시 강남구 테헤란로 231")
        .build();

    doNothing().when(userService).registerUser(any(UserJoinForm.class));

    //when
    //then
    mockMvc.perform(post(USER_API_URL + "auth/join")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(user)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
        .andExpect(jsonPath("$.errors").isArray())
        .andExpect(jsonPath("$.errors[0].field").value("phone"))
        .andExpect(jsonPath("$.errors[0].message").value("휴대전화 번호를 입력해 주세요."));
  }

  @Test
  void 고객생성_실패_휴대전화번호_유효하지않은번호() throws Exception {
    //given
    UserJoinForm user = UserJoinForm.builder()
        .loginId("loginId123")
        .password("password123")
        .name("김제로")
        .email("abc@deliverydotdot.com")
        .phone("1234-12345-12345")
        .address("서울시 강남구 테헤란로 231")
        .build();

    doNothing().when(userService).registerUser(any(UserJoinForm.class));

    //when
    //then
    mockMvc.perform(post(USER_API_URL + "auth/join")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(user)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
        .andExpect(jsonPath("$.errors").isArray())
        .andExpect(jsonPath("$.errors[0].field").value("phone"))
        .andExpect(jsonPath("$.errors[0].message").value("유효하지 않은 휴대전화 번호 입니다."));
  }

  @Test
  void 고객생성_실패_전체미입력() throws Exception {
    //given
    UserJoinForm user = UserJoinForm.builder()
        .build();

    doNothing().when(userService).registerUser(any(UserJoinForm.class));

    //when
    //then
    mockMvc.perform(post(USER_API_URL + "auth/join")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(user)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
        .andExpect(jsonPath("$.errors").isArray())
        .andExpect(jsonPath("$.errors[*].field").value(Matchers.containsInAnyOrder(
            "loginId", "password", "name", "email", "phone", "address")))
        .andExpect(jsonPath("$.errors[*].message").value(Matchers.containsInAnyOrder(
            "아이디를 입력해 주세요.",
            "비밀번호를 입력해 주세요.",
            "이름을 입력해 주세요.",
            "이메일을 입력해 주세요.",
            "휴대전화 번호를 입력해 주세요.",
            "주소를 입력해 주세요."
        )));
  }

  @Test
  void 고객생성_실패_주소좌표변환실패() throws Exception {
    //given
    UserJoinForm user = UserJoinForm.builder()
        .loginId("loginId123")
        .password("password123")
        .name("김제로")
        .email("abc@deliverydotdot.com")
        .phone("010-1234-5678")
        .address("유효하지 않은 주소")
        .build();

    doThrow(new RestApiException(NO_COORDINATES_FOUND_FOR_ADDRESS)).when(userService)
        .registerUser(any(UserJoinForm.class));

    //when
    //then
    mockMvc.perform(post(USER_API_URL + "auth/join")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(user)))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.code").value("NO_COORDINATES_FOUND_FOR_ADDRESS"))
        .andExpect(jsonPath("$.message").value("주소에 대한 좌표를 찾을 수 없습니다."));
  }
}