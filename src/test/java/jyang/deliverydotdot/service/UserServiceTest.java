package jyang.deliverydotdot.service;

import static jyang.deliverydotdot.type.ErrorCode.ALREADY_REGISTERED_EMAIL;
import static jyang.deliverydotdot.type.ErrorCode.ALREADY_REGISTERED_LOGIN_ID;
import static jyang.deliverydotdot.type.ErrorCode.ALREADY_REGISTERED_PHONE;
import static jyang.deliverydotdot.type.ErrorCode.INVALID_REQUEST;
import static jyang.deliverydotdot.type.ErrorCode.NO_COORDINATES_FOUND_FOR_ADDRESS;
import static jyang.deliverydotdot.type.ErrorCode.USER_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Optional;
import jyang.deliverydotdot.domain.User;
import jyang.deliverydotdot.domain.UserDeliveryAddress;
import jyang.deliverydotdot.dto.user.UserInfo;
import jyang.deliverydotdot.dto.user.UserJoinForm;
import jyang.deliverydotdot.dto.user.UserUpdateForm;
import jyang.deliverydotdot.exception.RestApiException;
import jyang.deliverydotdot.repository.UserDeliveryAddressRepository;
import jyang.deliverydotdot.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
public class UserServiceTest {

  @Mock
  private UserRepository userRepository;
  @Mock
  private UserDeliveryAddressRepository addressRepository;
  @Mock
  private BCryptPasswordEncoder passwordEncoder;
  @Mock
  private LocationService locationService;

  @InjectMocks
  private UserService userService;

  @Test
  void 유저생성_성공() {
    //given
    UserJoinForm joinForm = UserJoinForm.builder()
        .loginId("loginId123")
        .password("password123")
        .name("김제로")
        .email("abc@deliverydotdot.com")
        .phone("010-1234-5678")
        .address("서울시 강남구 테헤란로 231")
        .build();

    given(userRepository.existsByLoginId("loginId123")).willReturn(false);
    given(userRepository.existsByEmail("abc@deliverydotdot.com")).willReturn(false);
    given(userRepository.existsByPhone("010-1234-5678")).willReturn(false);
    given(passwordEncoder.encode("password123")).willReturn("encodedPassword");
    given(locationService.getCoordinatesFromAddress("서울시 강남구 테헤란로 231"))
        .willReturn(new GeometryFactory().createPoint(new Coordinate(37.4979, 127.0276)));

    //when
    userService.registerUser(joinForm);

    //then
    verify(userRepository, times(1)).save(any(User.class));
    verify(addressRepository, times(1)).save(any(UserDeliveryAddress.class));

  }

  @Test
  void 유저생성_실패_이미등록된아이디() {
    //given
    UserJoinForm joinForm = UserJoinForm.builder()
        .loginId("loginId123")
        .password("password123")
        .name("김제로")
        .email("abc@deliverydotdot.com")
        .phone("010-1234-5678")
        .address("서울시 강남구 테헤란로 231")
        .build();

    given(userRepository.existsByLoginId("loginId123")).willReturn(true);

    // when & then
    RestApiException exception = assertThrows(RestApiException.class,
        () -> userService.registerUser(joinForm));
    assertEquals(ALREADY_REGISTERED_LOGIN_ID, exception.getErrorCode());
    verify(userRepository, times(0)).save(any(User.class));
  }

  @Test
  void 유저생성_실패_이미등록된이메일() {
    //given
    UserJoinForm joinForm = UserJoinForm.builder()
        .loginId("loginId123")
        .password("password123")
        .name("김제로")
        .email("abc@deliverydotdot.com")
        .phone("010-1234-5678")
        .address("서울시 강남구 테헤란로 231")
        .build();

    given(userRepository.existsByLoginId("loginId123")).willReturn(false);
    given(userRepository.existsByEmail("abc@deliverydotdot.com")).willReturn(true);
    given(userRepository.existsByPhone("010-1234-5678")).willReturn(false);
    given(locationService.getCoordinatesFromAddress("서울시 강남구 테헤란로 231"))
        .willReturn(new GeometryFactory().createPoint(new Coordinate(37.4979, 127.0276)));

    // when & then
    RestApiException exception = assertThrows(RestApiException.class,
        () -> userService.registerUser(joinForm));
    assertEquals(ALREADY_REGISTERED_EMAIL, exception.getErrorCode());
    verify(userRepository, times(0)).save(any(User.class));
  }

  @Test
  void 유저생성_실패_이미등록된휴대전화() {
    //given
    UserJoinForm joinForm = UserJoinForm.builder()
        .loginId("loginId123")
        .password("password123")
        .name("김제로")
        .email("abc@deliverydotdot.com")
        .phone("010-1234-5678")
        .address("서울시 강남구 테헤란로 231")
        .build();

    given(userRepository.existsByLoginId("loginId123")).willReturn(false);
    given(userRepository.existsByEmail("abc@deliverydotdot.com")).willReturn(false);
    given(userRepository.existsByPhone("010-1234-5678")).willReturn(true);
    given(locationService.getCoordinatesFromAddress("서울시 강남구 테헤란로 231"))
        .willReturn(new GeometryFactory().createPoint(new Coordinate(37.4979, 127.0276)));

    // when & then
    RestApiException exception = assertThrows(RestApiException.class,
        () -> userService.registerUser(joinForm));
    assertEquals(ALREADY_REGISTERED_PHONE, exception.getErrorCode());
    verify(userRepository, times(0)).save(any(User.class));
  }

  @Test
  void 유저생성_실패_좌표없음() {
    //given
    UserJoinForm joinForm = UserJoinForm.builder()
        .loginId("loginId123")
        .password("password123")
        .name("김제로")
        .email("abc@deliverydotdot.com")
        .phone("010-1234-5678")
        .address("서울시 강남구 테헤란로 231")
        .build();

    given(userRepository.existsByLoginId("loginId123")).willReturn(false);
    given(userRepository.existsByEmail("abc@deliverydotdot.com")).willReturn(false);
    given(userRepository.existsByPhone("010-1234-5678")).willReturn(false);
    given(locationService.getCoordinatesFromAddress("서울시 강남구 테헤란로 231"))
        .willThrow(new RestApiException(NO_COORDINATES_FOUND_FOR_ADDRESS) {
        });

    // when & then
    RestApiException exception = assertThrows(RestApiException.class,
        () -> userService.registerUser(joinForm));

    assertEquals(NO_COORDINATES_FOUND_FOR_ADDRESS, exception.getErrorCode());
    verify(userRepository, times(1)).save(any(User.class));
    verify(addressRepository, times(0)).save(any(UserDeliveryAddress.class));
  }

  @Test
  void 유저생성_실패_위치정보예외() {
    //given
    UserJoinForm joinForm = UserJoinForm.builder()
        .loginId("loginId123")
        .password("password123")
        .name("김제로")
        .email("abc@deliverydotdot.com")
        .phone("010-1234-5678")
        .address("서울시 강남구 테헤란로 231")
        .build();

    given(userRepository.existsByLoginId("loginId123")).willReturn(false);
    given(userRepository.existsByEmail("abc@deliverydotdot.com")).willReturn(false);
    given(userRepository.existsByPhone("010-1234-5678")).willReturn(false);
    given(locationService.getCoordinatesFromAddress("서울시 강남구 테헤란로 231"))
        .willThrow(new RestApiException(INVALID_REQUEST) {
        });

    // when & then
    RestApiException exception = assertThrows(RestApiException.class,
        () -> userService.registerUser(joinForm));

    assertEquals(INVALID_REQUEST, exception.getErrorCode());
    verify(userRepository, times(1)).save(any(User.class));
    verify(addressRepository, times(0)).save(any(UserDeliveryAddress.class));
  }

  @Test
  void 유저정보조회_성공() {
    //given
    User user = User.builder()
        .loginId("loginId123")
        .password("password123")
        .name("김제로")
        .email("abc@deliverydotdot.com")
        .phone("010-1234-5678")
        .address("서울시 강남구 테헤란로 231")
        .build();

    given(userRepository.findByLoginId("loginId123")).willReturn(Optional.ofNullable(user));

    //when
    UserInfo userInfo = userService.getUserInfo("loginId123");

    //then
    assertEquals("loginId123", userInfo.getLoginId());
    assertEquals("김제로", userInfo.getName());
    assertEquals("abc@deliverydotdot.com", userInfo.getEmail());
  }

  @Test
  void 유저정보조회_실패_유저없음() {
    //given
    given(userRepository.findByLoginId("login123")).willReturn(Optional.empty());

    //when & then
    RestApiException exception = assertThrows(RestApiException.class,
        () -> userService.getUserInfo("login123"));

    assertEquals(USER_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void 유저정보수정_성공() {
    //given
    User user = User.builder()
        .loginId("loginId123")
        .password("password123")
        .name("김제로")
        .email("abc@deliverydotdot.com")
        .phone("010-1234-5678")
        .address("서울시 강남구 테헤란로 231")
        .build();

    UserUpdateForm updateForm = UserUpdateForm.builder()
        .password("newPassword")
        .email("newEmail@example.com")
        .phone("010-1234-5678")
        .address("서울시 강남구 테헤란로 231")
        .build();

    given(userRepository.findByLoginId("loginId123")).willReturn(Optional.of(user));
    given(passwordEncoder.encode(any())).willReturn("encodedNewPassword");

    //when
    userService.updateUserInfo("loginId123", updateForm);

    //then
    assertEquals("newEmail@example.com", user.getEmail());
    assertEquals("encodedNewPassword", user.getPassword());
    assertEquals("010-1234-5678", user.getPhone());
    assertEquals("서울시 강남구 테헤란로 231", user.getAddress());

  }

  @Test
  void 유저정보수정_실패_유저없음() {
    //given
    UserUpdateForm updateForm = UserUpdateForm.builder()
        .password("newPassword")
        .email("abc@deliverydotdot.com")
        .phone("010-1234-5678")
        .address("서울시 강남구 테헤란로 231")
        .build();

    given(userRepository.findByLoginId("loginId123")).willReturn(Optional.empty());

    //when & then
    RestApiException exception = assertThrows(RestApiException.class,
        () -> userService.updateUserInfo("loginId123", updateForm));

    assertEquals(USER_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void 유저정보수정_실패_이메일중복() {
    //given
    User user = User.builder()
        .loginId("loginId123")
        .password("password123")
        .name("김제로")
        .email("abc@deliverydotdot.com")
        .phone("010-1234-5678")
        .address("서울시 강남구 테헤란로 231")
        .build();

    UserUpdateForm updateForm = UserUpdateForm.builder()
        .password("newPassword")
        .email("duplicatedEmail@mail.com")
        .phone("010-1234-5678")
        .address("서울시 강남구 테헤란로 231")
        .build();

    given(userRepository.findByLoginId("loginId123")).willReturn(Optional.of(user));
    given(userRepository.existsByEmail(anyString())).willReturn(true);

    //when & then
    RestApiException exception = assertThrows(RestApiException.class,
        () -> userService.updateUserInfo("loginId123", updateForm));

    assertEquals(ALREADY_REGISTERED_EMAIL, exception.getErrorCode());
  }

  @Test
  void 유저삭제_성공() {
    //given
    User user = User.builder()
        .loginId("loginId123")
        .password("password123")
        .name("김제로")
        .email("abc@deliverydotdot.com")
        .phone("010-1234-5678")
        .address("서울시 강남구 테헤란로 231")
        .build();

    given(userRepository.findByLoginId("loginId123")).willReturn(Optional.of(user));

    //when
    userService.deleteByLoginId("loginId123");

    //then
    verify(userRepository, times(1)).delete(user);
  }
}
