package jyang.deliverydotdot.service;

import static jyang.deliverydotdot.type.ErrorCode.PARTNER_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Optional;
import jyang.deliverydotdot.domain.Partner;
import jyang.deliverydotdot.dto.partner.PartnerInfo;
import jyang.deliverydotdot.dto.partner.PartnerJoinForm;
import jyang.deliverydotdot.dto.partner.PartnerUpdateForm;
import jyang.deliverydotdot.exception.RestApiException;
import jyang.deliverydotdot.repository.PartnerRepository;
import jyang.deliverydotdot.type.ErrorCode;
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
class PartnerServiceTest {

  @Mock
  private PartnerRepository partnerRepository;
  @Mock
  private BCryptPasswordEncoder passwordEncoder;

  @InjectMocks
  private PartnerService partnerService;

  @Test
  void 파트너등록_성공() {
    //given
    PartnerJoinForm partnerJoinForm = PartnerJoinForm.builder()
        .loginId("loginId123")
        .password("password123")
        .name("김제로")
        .email("abc@deliverydotdot.com")
        .phone("010-1234-5678")
        .address("서울시 강남구 테헤란로 231")
        .build();

    given(partnerRepository.existsByLoginId("loginId123")).willReturn(false);
    given(partnerRepository.existsByEmail("abc@deliverydotdot.com")).willReturn(false);
    given(partnerRepository.existsByPhone("010-1234-5678")).willReturn(false);
    given(passwordEncoder.encode("password123")).willReturn("encodedPassword");

    //when
    partnerService.registerPartner(partnerJoinForm);

    //then
    verify(partnerRepository, times(1)).save(any(Partner.class));
  }

  @Test
  void 파트너등록_실패_이미등록된아이디() {
    //given
    PartnerJoinForm partnerJoinForm = PartnerJoinForm.builder()
        .loginId("loginId123")
        .password("password123")
        .name("김제로")
        .email("abc@deliverydotdot.com")
        .phone("010-1234-5678")
        .address("서울시 강남구 테헤란로 231")
        .build();

    given(partnerRepository.existsByLoginId("loginId123")).willReturn(true);
    given(partnerRepository.existsByEmail("abc@deliverydotdot.com")).willReturn(false);
    given(partnerRepository.existsByPhone("010-1234-5678")).willReturn(false);
    given(passwordEncoder.encode("password123")).willReturn("encodedPassword");

    //when & then
    RestApiException exception = assertThrows(RestApiException.class,
        () -> partnerService.registerPartner(partnerJoinForm));

    assertEquals(ErrorCode.ALREADY_REGISTERED_LOGIN_ID, exception.getErrorCode());
  }

  @Test
  void 파트너정보조회_성공() {
    //given
    Partner partner = Partner.builder()
        .loginId("loginId123")
        .name("김제로")
        .email("abc@deliverydotdot.com")
        .phone("010-1234-5678")
        .address("서울시 강남구 테헤란로 231")
        .build();

    given(partnerRepository.findByLoginId("loginId123")).willReturn(Optional.of(partner));

    //when
    PartnerInfo partnerInfo = partnerService.getPartnerInfo("loginId123");

    //then
    assertEquals("loginId123", partnerInfo.getLoginId());
    assertEquals("김제로", partnerInfo.getName());
    assertEquals("abc@deliverydotdot.com", partnerInfo.getEmail());
  }

  @Test
  void 파트너정보조회_실패_파트너없음() {
    //given
    given(partnerRepository.findByLoginId("loginId123")).willReturn(Optional.empty());

    //when & then
    RestApiException exception = assertThrows(RestApiException.class,
        () -> partnerService.getPartnerInfo("loginId123"));

    assertEquals(PARTNER_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void 파트너정보수정_성공() {
    //given
    PartnerUpdateForm updateForm = PartnerUpdateForm.builder()
        .password("newPassword")
        .email("newEmail@example.com")
        .phone("010-1234-5678")
        .address("서울시 강남구 테헤란로 231")
        .build();

    Partner partner = Partner.builder()
        .loginId("loginId123")
        .password("password")
        .name("김제로")
        .email("abc@deliverydotdot.com")
        .phone("010-1234-5678")
        .address("서울시 강남구 테헤란로 231")
        .build();

    given(partnerRepository.findByLoginId("loginId")).willReturn(Optional.of(partner));
    given(passwordEncoder.encode(any())).willReturn("encodedNewPassword");

    //when
    partnerService.updatePartnerInfo("loginId", updateForm);

    //then
    assertEquals("newEmail@example.com", partner.getEmail());
    assertEquals("010-1234-5678", partner.getPhone());
    assertEquals("서울시 강남구 테헤란로 231", partner.getAddress());
    assertEquals("encodedNewPassword", partner.getPassword());
  }

  @Test
  void 파트너삭제_성공() {
    //given
    Partner partner = Partner.builder()
        .loginId("loginId123")
        .password("password")
        .name("김제로")
        .email("abc@deliverydotdot.com")
        .phone("010-1234-5678")
        .address("서울시 강남구 테헤란로 231")
        .build();

    given(partnerRepository.findByLoginId("loginId123")).willReturn(Optional.of(partner));

    //when
    partnerService.deleteByLoginId("loginId123");

    //then
    verify(partnerRepository, times(1)).delete(partner);
  }
}