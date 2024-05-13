package jyang.deliverydotdot.service;

import static jyang.deliverydotdot.type.ErrorCode.ALREADY_REGISTERED_EMAIL;
import static jyang.deliverydotdot.type.ErrorCode.ALREADY_REGISTERED_LOGIN_ID;
import static jyang.deliverydotdot.type.ErrorCode.ALREADY_REGISTERED_PHONE;
import static jyang.deliverydotdot.type.ErrorCode.PARTNER_NOT_FOUND;

import jyang.deliverydotdot.domain.Partner;
import jyang.deliverydotdot.dto.partner.PartnerInfo;
import jyang.deliverydotdot.dto.partner.PartnerJoinForm;
import jyang.deliverydotdot.dto.partner.PartnerUpdateForm;
import jyang.deliverydotdot.exception.RestApiException;
import jyang.deliverydotdot.repository.PartnerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PartnerService {

  private final PartnerRepository partnerRepository;

  private final BCryptPasswordEncoder passwordEncoder;

  /**
   * 파트너 등록
   *
   * @param joinForm 파트너 등록 폼
   */
  @Transactional
  public void registerPartner(PartnerJoinForm joinForm) {
    validateRegisterPartner(joinForm);

    partnerRepository.save(
        Partner.builder()
            .loginId(joinForm.getLoginId())
            .password(passwordEncoder.encode(joinForm.getPassword()))
            .name(joinForm.getName())
            .email(joinForm.getEmail())
            .phone(joinForm.getPhone())
            .address(joinForm.getAddress())
            .build());
  }

  /**
   * 파트너 정보 조회
   *
   * @param loginId 로그인 아이디
   * @return 파트너 정보
   */
  public PartnerInfo getPartnerInfo(String loginId) {
    Partner partner = getPartnerByLoginId(loginId);
    return PartnerInfo.fromPartner(partner);
  }

  /**
   * 파트너 삭제
   *
   * @param loginId 로그인 아이디
   */
  @Transactional
  public void deleteByLoginId(String loginId) {
    Partner partner = getPartnerByLoginId(loginId);

    partnerRepository.delete(partner);
  }

  /**
   * 파트너 정보 수정
   *
   * @param loginId    로그인 아이디
   * @param updateForm 파트너 수정 폼
   */
  @Transactional
  public void updatePartnerInfo(String loginId, PartnerUpdateForm updateForm) {
    Partner partner = getPartnerByLoginId(loginId);

    if (!partner.getEmail().equals(updateForm.getEmail())) {
      isValidEmail(updateForm.getEmail());
    }

    if (!partner.getPhone().equals(updateForm.getPhone())) {
      isValidPhone(updateForm.getPhone());
    }

    if (updateForm.getPassword() != null) {
      updateForm.encodePassword(passwordEncoder.encode(updateForm.getPassword()));
    }

    partner.update(updateForm);
  }

  /**
   * 로그인 아이디로 파트너 조회
   *
   * @param loginId 로그인 아이디
   * @return 파트너
   */
  private Partner getPartnerByLoginId(String loginId) {
    return partnerRepository.findByLoginId(loginId)
        .orElseThrow(() -> new RestApiException(PARTNER_NOT_FOUND));
  }

  /**
   * 파트너 등록 유효성 검사
   *
   * @param joinForm 파트너 등록 폼
   */
  private void validateRegisterPartner(PartnerJoinForm joinForm) {
    isValidLoginId(joinForm.getLoginId());
    isValidEmail(joinForm.getEmail());
    isValidPhone(joinForm.getPhone());
  }

  /**
   * 로그인 아이디 중복 검사
   *
   * @param loginId 로그인 아이디
   */
  private void isValidLoginId(String loginId) {
    if (partnerRepository.existsByLoginId(loginId)) {
      log.warn("Already registered loginId : {} ", loginId);
      throw new RestApiException(ALREADY_REGISTERED_LOGIN_ID);
    }
  }

  /**
   * 이메일 중복 검사
   *
   * @param email 이메일
   */
  private void isValidEmail(String email) {
    if (partnerRepository.existsByEmail(email)) {
      log.warn("Already registered email : {} ", email);
      throw new RestApiException(ALREADY_REGISTERED_EMAIL);
    }
  }

  /**
   * 전화번호 중복 검사
   *
   * @param phone 전화번호
   */
  private void isValidPhone(String phone) {
    if (partnerRepository.existsByPhone(phone)) {
      log.warn("Already registered phone : {} ", phone);
      throw new RestApiException(ALREADY_REGISTERED_PHONE);
    }
  }
}
