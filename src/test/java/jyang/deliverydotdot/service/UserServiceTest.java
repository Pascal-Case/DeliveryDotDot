package jyang.deliverydotdot.service;

import static jyang.deliverydotdot.type.ErrorCode.ALREADY_REGISTERED_EMAIL;
import static jyang.deliverydotdot.type.ErrorCode.ALREADY_REGISTERED_LOGIN_ID;
import static jyang.deliverydotdot.type.ErrorCode.ALREADY_REGISTERED_PHONE;
import static jyang.deliverydotdot.type.ErrorCode.INVALID_REQUEST;
import static jyang.deliverydotdot.type.ErrorCode.NO_COORDINATES_FOUND_FOR_ADDRESS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jyang.deliverydotdot.domain.User;
import jyang.deliverydotdot.dto.user.UserJoinForm;
import jyang.deliverydotdot.exception.RestApiException;
import jyang.deliverydotdot.repository.UserDeliveryAddressRepository;
import jyang.deliverydotdot.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class UserServiceTest {

  @Mock
  private UserRepository userRepository;
  @Mock
  private UserDeliveryAddressRepository userDeliveryAddressRepository;
  @Mock
  private BCryptPasswordEncoder passwordEncoder;
  @Mock
  private LocationService locationService;

  @InjectMocks
  private UserService userService;

  private UserJoinForm validUserJoinForm;

  @BeforeEach
  void setUp() {
    validUserJoinForm = new UserJoinForm("userLoginId", "userPassword", "userName",
        "user@example.com", "010-1234-5678", "user location");
    when(passwordEncoder.encode("userPassword")).thenReturn("encodedPassword");
    when(userRepository.existsByLoginId("userLoginId")).thenReturn(false);
    when(userRepository.existsByEmail("user@example.com")).thenReturn(false);
    when(userRepository.existsByLoginId("010-1234-5678")).thenReturn(false);
    when(locationService.getCoordinatesFromAddress("user location")).thenReturn(null);
  }

  @Test
  void 유저생성_성공() {
    userService.registerUser(validUserJoinForm);
    verify(userRepository).save(any(User.class));
    verify(locationService).getCoordinatesFromAddress("user location");
  }

  @Test
  void 유저생성_실패_이미등록된아이디() {
    when(userRepository.existsByLoginId("userLoginId")).thenReturn(true);
    RestApiException exception = assertThrows(RestApiException.class,
        () -> userService.registerUser(validUserJoinForm));

    assertEquals(ALREADY_REGISTERED_LOGIN_ID, exception.getErrorCode());
  }

  @Test
  void 유저생성_실패_이미등록된이메일() {
    when(userRepository.existsByEmail("user@example.com")).thenReturn(true);
    RestApiException exception = assertThrows(RestApiException.class,
        () -> userService.registerUser(validUserJoinForm));

    assertEquals(ALREADY_REGISTERED_EMAIL, exception.getErrorCode());
  }

  @Test
  void 유저생성_실패_이미등록된휴대전화() {
    when(userRepository.existsByPhone("010-1234-5678")).thenReturn(true);
    RestApiException exception = assertThrows(RestApiException.class,
        () -> userService.registerUser(validUserJoinForm));

    assertEquals(ALREADY_REGISTERED_PHONE, exception.getErrorCode());
  }

  @Test
  void 유저생성_실패_좌표없음() {
    when(locationService.getCoordinatesFromAddress("user location")).thenThrow(
        new RestApiException(NO_COORDINATES_FOUND_FOR_ADDRESS));
    RestApiException exception = assertThrows(RestApiException.class,
        () -> userService.registerUser(validUserJoinForm));

    assertEquals(NO_COORDINATES_FOUND_FOR_ADDRESS, exception.getErrorCode());
  }

  @Test
  void 유저생성_실패_위치정보예외() {
    when(locationService.getCoordinatesFromAddress("user location")).thenThrow(
        new RestApiException(INVALID_REQUEST));
    RestApiException exception = assertThrows(RestApiException.class,
        () -> userService.registerUser(validUserJoinForm));

    assertEquals(INVALID_REQUEST, exception.getErrorCode());
  }
}
