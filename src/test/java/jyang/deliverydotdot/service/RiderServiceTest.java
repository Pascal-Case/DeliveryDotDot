package jyang.deliverydotdot.service;

import static jyang.deliverydotdot.type.DeliveryMethod.BICYCLE;
import static jyang.deliverydotdot.type.ErrorCode.ALREADY_REGISTERED_LOGIN_ID;
import static jyang.deliverydotdot.type.ErrorCode.INVALID_DELIVERY_METHOD;
import static jyang.deliverydotdot.type.ErrorCode.RIDER_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Objects;
import java.util.Optional;
import jyang.deliverydotdot.domain.Rider;
import jyang.deliverydotdot.dto.rider.RiderInfo;
import jyang.deliverydotdot.dto.rider.RiderJoinForm;
import jyang.deliverydotdot.dto.rider.RiderUpdateForm;
import jyang.deliverydotdot.exception.RestApiException;
import jyang.deliverydotdot.repository.RiderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class RiderServiceTest {

  @Mock
  private RiderRepository riderRepository;
  @Mock
  private BCryptPasswordEncoder passwordEncoder;

  @InjectMocks
  private RiderService riderService;

  @Test
  void 라이더등록_성공() {
    //given
    RiderJoinForm riderJoinForm = RiderJoinForm.builder()
        .loginId("loginId123")
        .password("password123")
        .name("김라이더")
        .email("abc@deliverydotdot.com")
        .phone("010-1234-5678")
        .address("서울시 강남구 테헤란로 231")
        .deliveryMethod("BICYCLE")
        .deliveryRegion("서울시 강남구")
        .build();

    given(riderRepository.existsByLoginId(anyString())).willReturn(false);
    given(riderRepository.existsByEmail(anyString())).willReturn(false);
    given(riderRepository.existsByPhone(anyString())).willReturn(false);
    given(passwordEncoder.encode(anyString())).willReturn("encodedPassword");

    //when
    riderService.registerRider(riderJoinForm);

    //then
    verify(riderRepository, times(1)).save(any(Rider.class));
  }

  @Test
  void 라이더등록_실패_이미등록된아이디() {
    //given
    RiderJoinForm riderJoinForm = RiderJoinForm.builder()
        .loginId("loginId123")
        .password("password123")
        .name("김라이더")
        .email("abc@deliverydotdot.com")
        .phone("010-1234-5678")
        .address("서울시 강남구 테헤란로 231")
        .deliveryMethod("BICYCLE")
        .deliveryRegion("서울시 강남구")
        .build();

    given(riderRepository.existsByLoginId(anyString())).willReturn(true);

    // when & then
    RestApiException exception = assertThrows(RestApiException.class,
        () -> riderService.registerRider(riderJoinForm));

    assertEquals(ALREADY_REGISTERED_LOGIN_ID, exception.getErrorCode());
    verify(riderRepository, times(0)).save(any(Rider.class));
  }

  @Test
  void 라이더등록_실패_배달수단_없음() {
    //given
    RiderJoinForm riderJoinForm = RiderJoinForm.builder()
        .loginId("loginId123")
        .password("password123")
        .name("김라이더")
        .email("abc@deliverydotdot.com")
        .phone("010-1234-5678")
        .address("서울시 강남구 테헤란로 231")
        .deliveryMethod(null)
        .deliveryRegion("서울시 강남구")
        .build();

    given(riderRepository.existsByLoginId(anyString())).willReturn(false);

    // when & then
    RestApiException exception = assertThrows(RestApiException.class,
        () -> riderService.registerRider(riderJoinForm));

    assertEquals(INVALID_DELIVERY_METHOD, exception.getErrorCode());
    verify(riderRepository, times(0)).save(any(Rider.class));
  }

  @Test
  void 라이더등록_실패_배달수단_유효하지않음() {
    //given
    RiderJoinForm riderJoinForm = RiderJoinForm.builder()
        .loginId("loginId123")
        .password("password123")
        .name("김라이더")
        .email("abc@deliverydotdot.com")
        .phone("010-1234-5678")
        .address("서울시 강남구 테헤란로 231")
        .deliveryMethod("INVALID_DELIVERY_METHOD")
        .deliveryRegion("서울시 강남구")
        .build();

    given(riderRepository.existsByLoginId(anyString())).willReturn(false);

    // when & then
    RestApiException exception = assertThrows(RestApiException.class,
        () -> riderService.registerRider(riderJoinForm));

    assertEquals(INVALID_DELIVERY_METHOD, exception.getErrorCode());
    verify(riderRepository, times(0)).save(any(Rider.class));
  }

  @Test
  void 라이더정보조회_성공() {
    //given
    Rider rider = Rider.builder()
        .loginId("loginId123")
        .name("김라이더")
        .email("abc@deliverydotdot.com")
        .phone("010-1234-5678")
        .address("서울시 강남구 테헤란로 231")
        .deliveryMethod(BICYCLE)
        .deliveryRegion("서울시 강남구")
        .build();

    given(riderRepository.findByLoginId(anyString())).willReturn(Optional.of(rider));

    //when
    RiderInfo riderInfo = riderService.getRiderInfo("loginId123");

    //then
    assertEquals(rider.getLoginId(), riderInfo.getLoginId());
    assertEquals(rider.getName(), riderInfo.getName());
    assertEquals(rider.getEmail(), riderInfo.getEmail());
  }

  @Test
  void 라이더정보조회_실패_존재하지않는아이디() {
    //given
    given(riderRepository.findByLoginId(anyString())).willReturn(Optional.empty());

    // when & then
    RestApiException exception = assertThrows(RestApiException.class,
        () -> riderService.getRiderInfo("loginId123"));

    assertEquals(RIDER_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void 라이더정보수정_성공() {
    //given
    RiderUpdateForm riderUpdateForm = RiderUpdateForm.builder()
        .password("newPassword")
        .email("newEmail@example.com")
        .phone("010-1234-5678")
        .address("서울시 강남구 테헤란로 231")
        .deliveryMethod("ON_FOOT")
        .deliveryRegion("서울시 관악구")
        .build();

    Rider rider = Rider.builder()
        .loginId("loginId123")
        .password("password")
        .name("김라이더")
        .email("abc@deliverydotdot.com")
        .phone("010-1234-5678")
        .address("서울시 강남구 테헤란로 231")
        .deliveryMethod(BICYCLE)
        .deliveryRegion("서울시 강남구")
        .build();

    given(riderRepository.findByLoginId("loginId123")).willReturn(Optional.of(rider));
    given(passwordEncoder.encode(any())).willReturn("encodedNewPassword");

    //when
    riderService.updateRiderInfo("loginId123", riderUpdateForm);

    //then
    assertEquals("encodedNewPassword", rider.getPassword());
    assertEquals(riderUpdateForm.getEmail(), rider.getEmail());
    assertEquals(riderUpdateForm.getPhone(), rider.getPhone());
    assertEquals(riderUpdateForm.getDeliveryMethod(), rider.getDeliveryMethod().name());
    assertEquals(riderUpdateForm.getDeliveryRegion(), rider.getDeliveryRegion());
  }

  @Test
  void 라이더삭제_성공() {
    //given
    Rider rider = Rider.builder()
        .loginId("loginId123")
        .password("password")
        .name("김라이더")
        .email("abc@deliverydotdot.com")
        .phone("010-1234-5678")
        .address("서울시 강남구 테헤란로 231")
        .deliveryMethod(BICYCLE)
        .deliveryRegion("서울시 강남구")
        .build();

    given(riderRepository.findByLoginId("loginId123")).willReturn(Optional.ofNullable(rider));

    //when
    riderService.deleteByLoginId("loginId123");

    //then
    verify(riderRepository, times(1)).delete(Objects.requireNonNull(rider));
  }
}