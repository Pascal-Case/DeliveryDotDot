package jyang.deliverydotdot.service;

import static jyang.deliverydotdot.type.ErrorCode.ALREADY_REGISTERED_EMAIL;
import static jyang.deliverydotdot.type.ErrorCode.ALREADY_REGISTERED_LOGIN_ID;
import static jyang.deliverydotdot.type.ErrorCode.ALREADY_REGISTERED_PHONE;
import static jyang.deliverydotdot.type.ErrorCode.INVALID_DELIVERY_METHOD;
import static jyang.deliverydotdot.type.ErrorCode.RIDER_NOT_FOUND;

import java.util.Arrays;
import jyang.deliverydotdot.domain.Rider;
import jyang.deliverydotdot.dto.rider.RiderInfo;
import jyang.deliverydotdot.dto.rider.RiderJoinForm;
import jyang.deliverydotdot.dto.rider.RiderUpdateForm;
import jyang.deliverydotdot.dto.rider.RiderUpdateForm.UpdateCurrentLocation;
import jyang.deliverydotdot.exception.RestApiException;
import jyang.deliverydotdot.repository.RiderRepository;
import jyang.deliverydotdot.type.DeliveryMethod;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RiderService {

  private final RiderRepository riderRepository;

  private final BCryptPasswordEncoder passwordEncoder;

  private final RedisService redisService;

  /**
   * 라이더 등록
   *
   * @param joinForm 라이더 등록 폼
   */
  @Transactional
  public void registerRider(RiderJoinForm joinForm) {

    validateRegisterRider(joinForm);

    riderRepository.save(
        Rider.builder()
            .loginId(joinForm.getLoginId())
            .password(passwordEncoder.encode(joinForm.getPassword()))
            .name(joinForm.getName())
            .email(joinForm.getEmail())
            .phone(joinForm.getPhone())
            .address(joinForm.getAddress())
            .deliveryMethod(DeliveryMethod.valueOf(joinForm.getDeliveryMethod()))
            .deliveryRegion(joinForm.getDeliveryRegion())
            .build());
  }

  /**
   * 라이더 정보 조회
   *
   * @param loginId 라이더 아이디
   * @return 라이더 정보
   */
  public RiderInfo getRiderInfo(String loginId) {
    Rider rider = getRiderByLoginId(loginId);

    return RiderInfo.fromRider(rider);
  }

  /**
   * 라이더 정보 수정
   *
   * @param loginId    라이더 아이디
   * @param updateForm 라이더 수정 폼
   */
  @Transactional
  public void updateRiderInfo(String loginId, RiderUpdateForm updateForm) {
    Rider rider = getRiderByLoginId(loginId);

    if (!rider.getEmail().equals(updateForm.getEmail())) {
      isValidEmail(updateForm.getEmail());
    }
    if (!rider.getPhone().equals(updateForm.getPhone())) {
      isValidPhone(updateForm.getPhone());
    }
    if (!rider.getDeliveryMethod().name().equals(updateForm.getDeliveryMethod())) {
      isValidateDeliveryMethod(updateForm.getDeliveryMethod());
    }

    if (updateForm.getPassword() != null) {
      updateForm.encodePassword(passwordEncoder.encode(updateForm.getPassword()));
    }

    rider.update(updateForm);
  }

  /**
   * 라이더 삭제
   *
   * @param loginId 라이더 아이디
   */
  @Transactional
  public void deleteByLoginId(String loginId) {
    Rider rider = getRiderByLoginId(loginId);

    System.out.println(rider.getLoginId());

    riderRepository.delete(rider);
  }

  /**
   * 라이더 아이디로 라이더 조회
   *
   * @param loginId 라이더 아이디
   * @return 라이더
   */
  public Rider getRiderByLoginId(String loginId) {
    return riderRepository.findByLoginId(loginId)
        .orElseThrow(() -> new RestApiException(RIDER_NOT_FOUND));
  }

  /**
   * 라이더 등록 유효성 검사
   *
   * @param joinForm 라이더 등록 폼
   */
  private void validateRegisterRider(RiderJoinForm joinForm) {
    isValidLoginId(joinForm.getLoginId());
    isValidEmail(joinForm.getEmail());
    isValidPhone(joinForm.getPhone());
    isValidateDeliveryMethod(joinForm.getDeliveryMethod());
  }

  /**
   * 로그인 아이디 중복 검사
   *
   * @param loginId 로그인 아이디
   */
  private void isValidLoginId(String loginId) {
    if (riderRepository.existsByLoginId(loginId)) {
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
    if (riderRepository.existsByEmail(email)) {
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
    if (riderRepository.existsByPhone(phone)) {
      log.warn("Already registered phone : {} ", phone);
      throw new RestApiException(ALREADY_REGISTERED_PHONE);
    }
  }

  /**
   * 배달 방식 유효성 검사
   *
   * @param deliveryMethodName 배달 방식 이름
   */
  private void isValidateDeliveryMethod(String deliveryMethodName) {
    Arrays.stream(DeliveryMethod.values())
        .filter(deliveryMethod -> deliveryMethod.name().equals(deliveryMethodName))
        .findAny()
        .orElseThrow(() -> new RestApiException(INVALID_DELIVERY_METHOD));
  }

  @Transactional
  public void updateRiderLocation(Rider rider, UpdateCurrentLocation updateForm) {
    redisService.addOrUpdateRiderLocation(rider.getRiderId(), updateForm.getLongitude(),
        updateForm.getLatitude());
  }
}
