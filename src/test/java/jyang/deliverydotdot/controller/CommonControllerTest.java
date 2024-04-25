package jyang.deliverydotdot.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import jyang.deliverydotdot.config.SecurityConfig;
import jyang.deliverydotdot.dto.UserJoinForm;
import jyang.deliverydotdot.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = CommonController.class, includeFilters = {
    @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
})
class CommonControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private UserService userService;

  @BeforeEach
  public void setup() {

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
    mockMvc.perform(post("/api/v1/common/users/join")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(user)))
        .andExpect(status().isOk());

  }

  @Test
  void 고객생성_실패_로그인아이디짧음() throws Exception {
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
    mockMvc.perform(post("/api/v1/common/users/join")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(user)))
        .andExpect(status().isBadRequest());
  }

}