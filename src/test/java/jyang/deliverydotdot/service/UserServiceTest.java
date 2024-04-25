package jyang.deliverydotdot.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jyang.deliverydotdot.domain.User;
import jyang.deliverydotdot.dto.UserJoinForm;
import jyang.deliverydotdot.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class UserServiceTest {

  @Autowired
  private UserService userService;

  @MockBean
  private UserRepository userRepository;

  @MockBean
  private BCryptPasswordEncoder passwordEncoder;

  private UserJoinForm validUserJoinForm;

  @BeforeEach
  void setUp() {
    validUserJoinForm = new UserJoinForm("userLoginId", "userPassword", "userName",
        "user@example.com", "010-1234-5678", "user address");
    when(passwordEncoder.encode("userPassword")).thenReturn("encodedPassword");
    when(userRepository.existsByLoginId("userLoginId")).thenReturn(false);
    when(userRepository.existsByEmail("user@example.com")).thenReturn(false);
    when(userRepository.existsByLoginId("010-1234-5678")).thenReturn(false);
  }

  @Test
  void 유저생성_성공() {
    userService.registerUser(validUserJoinForm);
    verify(userRepository).save(any(User.class));
  }

  @Test
  void 유저생성_실패_이미등록된아이디() {
    when(userRepository.existsByLoginId("userLoginId")).thenReturn(true);
    Exception exception = assertThrows(RuntimeException.class, () -> {
      userService.registerUser(validUserJoinForm);
    });

    assertEquals("이미 등록된 로그인 id 입니다.", exception.getMessage());
  }

  @Test
  void 유저생성_실패_이미등록된이메일() {
    when(userRepository.existsByEmail("user@example.com")).thenReturn(true);
    Exception exception = assertThrows(RuntimeException.class, () -> {
      userService.registerUser(validUserJoinForm);
    });

    assertEquals("이미 등록된 이메일 입니다.", exception.getMessage());
  }

  @Test
  void 유저생성_실패_이미등록된휴대전화() {
    when(userRepository.existsByLoginId("010-1234-5678")).thenReturn(true);
    Exception exception = assertThrows(RuntimeException.class, () -> {
      userService.registerUser(validUserJoinForm);
    });

    assertEquals("이미 등록된 휴대전화 번호 입니다.", exception.getMessage());
  }
}
