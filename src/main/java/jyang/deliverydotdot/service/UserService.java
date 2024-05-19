package jyang.deliverydotdot.service;

import static jyang.deliverydotdot.type.AuthType.LOCAL;
import static jyang.deliverydotdot.type.ErrorCode.ALREADY_REGISTERED_EMAIL;
import static jyang.deliverydotdot.type.ErrorCode.ALREADY_REGISTERED_LOGIN_ID;
import static jyang.deliverydotdot.type.ErrorCode.ALREADY_REGISTERED_PHONE;
import static jyang.deliverydotdot.type.ErrorCode.USER_NOT_FOUND;

import java.util.List;
import java.util.Objects;
import jyang.deliverydotdot.domain.User;
import jyang.deliverydotdot.domain.UserDeliveryAddress;
import jyang.deliverydotdot.dto.user.UserDeliveryAddressDTO.AddAddressRequest;
import jyang.deliverydotdot.dto.user.UserDeliveryAddressDTO.AddressResponse;
import jyang.deliverydotdot.dto.user.UserDeliveryAddressDTO.UpdateAddressRequest;
import jyang.deliverydotdot.dto.user.UserInfo;
import jyang.deliverydotdot.dto.user.UserJoinForm;
import jyang.deliverydotdot.dto.user.UserUpdateForm;
import jyang.deliverydotdot.exception.RestApiException;
import jyang.deliverydotdot.repository.UserDeliveryAddressRepository;
import jyang.deliverydotdot.repository.UserRepository;
import jyang.deliverydotdot.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Point;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

  private final LocationService locationService;

  private final UserDeliveryAddressRepository userDeliveryAddressRepository;

  private final UserRepository userRepository;

  private final BCryptPasswordEncoder passwordEncoder;

  /**
   * 사용자 등록
   *
   * @param userJoinForm 사용자 등록 폼
   */
  @Transactional
  public void registerUser(UserJoinForm userJoinForm) {

    validateRegisterUser(userJoinForm);

    User savedUser = User.builder()
        .loginId(userJoinForm.getLoginId())
        .password(passwordEncoder.encode(userJoinForm.getPassword()))
        .name(userJoinForm.getName())
        .email(userJoinForm.getEmail())
        .phone(userJoinForm.getPhone())
        .address(userJoinForm.getAddress())
        .authType(LOCAL)
        .build();

    userRepository.save(savedUser);

    Point coordinates = locationService.getCoordinatesFromAddress(userJoinForm.getAddress());

    UserDeliveryAddress deliveryAddress = UserDeliveryAddress.builder()
        .user(savedUser)
        .addressName("default")
        .address(userJoinForm.getAddress())
        .coordinates(coordinates)
        .isDefaultAddress(true)
        .build();

    userDeliveryAddressRepository.save(deliveryAddress);
  }

  /**
   * 사용자 정보 조회
   *
   * @param loginId 로그인 아이디
   * @return 사용자 정보
   */
  public UserInfo getUserInfo(String loginId) {
    User user = getUserByLoginId(loginId);

    return UserInfo.fromUser(user);
  }

  /**
   * 로그인 아이디로 사용자 조회
   *
   * @param loginId 로그인 아이디
   * @return 사용자
   */
  public User getUserByLoginId(String loginId) {
    return userRepository.findByLoginId(loginId)
        .orElseThrow(() -> new RestApiException(USER_NOT_FOUND));
  }

  /**
   * 사용자 삭제
   *
   * @param loginId 로그인 아이디
   */
  @Transactional
  public void deleteByLoginId(String loginId) {
    User user = getUserByLoginId(loginId);

    userRepository.delete(user);
  }

  /**
   * 사용자 정보 수정
   *
   * @param loginId    로그인 아이디
   * @param updateForm 사용자 수정 폼
   */
  @Transactional
  public void updateUserInfo(String loginId, UserUpdateForm updateForm) {
    User user = getUserByLoginId(loginId);

    if (!user.getEmail().equals(updateForm.getEmail())) {
      isValidEmail(updateForm.getEmail());
    }

    if (!user.getPhone().equals(updateForm.getPhone())) {
      isValidPhone(updateForm.getPhone());
    }

    if (updateForm.getPassword() != null) {
      updateForm.encodePassword(passwordEncoder.encode(updateForm.getPassword()));
    }

    user.update(updateForm);
  }

  /**
   * 회원가입 유효성 검사
   *
   * @param userJoinForm 회원가입 폼
   */
  private void validateRegisterUser(UserJoinForm userJoinForm) {
    isValidLoginId(userJoinForm.getLoginId());
    isValidEmail(userJoinForm.getEmail());
    isValidPhone(userJoinForm.getPhone());
  }

  /**
   * 로그인 아이디 중복 검사
   *
   * @param loginId 로그인 아이디
   */
  private void isValidLoginId(String loginId) {
    if (userRepository.existsByLoginId(loginId)) {
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
    if (userRepository.existsByEmail(email)) {
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
    if (userRepository.existsByPhone(phone)) {
      log.warn("Already registered phone : {} ", phone);
      throw new RestApiException(ALREADY_REGISTERED_PHONE);
    }
  }

  /**
   * 사용자 배송지 추가
   *
   * @param user                사용자
   * @param userDeliveryAddress 사용자 배송지 DTO
   */
  @Transactional
  public void addAddress(User user, AddAddressRequest userDeliveryAddress) {
    Point coordinates = locationService.getCoordinatesFromAddress(
        userDeliveryAddress.getAddress());

    if (userDeliveryAddress.getIsDefault() != null && userDeliveryAddress.getIsDefault()) {
      userDeliveryAddressRepository.clearDefaultAddress(user);
    }

    UserDeliveryAddress address = UserDeliveryAddress.builder()
        .user(user)
        .addressName(userDeliveryAddress.getAddressName())
        .address(userDeliveryAddress.getAddress())
        .coordinates(coordinates)
        .isDefaultAddress(userDeliveryAddress.getIsDefault())
        .build();

    userDeliveryAddressRepository.save(address);

  }

  /**
   * 사용자 배송지 삭제
   *
   * @param user      사용자
   * @param addressId 배송지 ID
   */
  @Transactional
  public void deleteAddress(User user, Long addressId) {
    UserDeliveryAddress address = findByAddressId(addressId);

    validateAddress(user, address);

    userDeliveryAddressRepository.delete(address);
  }

  public UserDeliveryAddress findByAddressId(Long addressId) {
    return userDeliveryAddressRepository.findById(addressId)
        .orElseThrow(() -> new RestApiException(ErrorCode.ADDRESS_NOT_FOUND));
  }

  public void validateAddress(User user, UserDeliveryAddress address) {
    if (!Objects.equals(address.getUser().getUserId(), user.getUserId())) {
      throw new RestApiException(ErrorCode.ADDRESS_NOT_BOUND_TO_USER);
    }
  }

  /**
   * 사용자 배송지 수정
   *
   * @param user                   사용자
   * @param userDeliveryAddressDTO 사용자 배송지 DTO
   */
  @Transactional
  public void updateAddress(User user, UpdateAddressRequest userDeliveryAddressDTO) {
    UserDeliveryAddress address = findByAddressId(userDeliveryAddressDTO.getAddressId());

    validateAddress(user, address);

    if (address.getAddress().equals(userDeliveryAddressDTO.getAddress())) {
      return;
    }

    if (userDeliveryAddressDTO.getIsDefault() != null && userDeliveryAddressDTO.getIsDefault()) {
      userDeliveryAddressRepository.clearDefaultAddress(user);
    }

    Point coordinates = locationService.getCoordinatesFromAddress(
        userDeliveryAddressDTO.getAddress());

    address.update(userDeliveryAddressDTO, coordinates);
  }

  /**
   * 사용자 배송지 조회
   *
   * @param user 사용자
   * @return 사용자 배송지 목록
   */
  public List<AddressResponse> getAddress(User user) {
    return userDeliveryAddressRepository.findByUserOrderByDefaultAddress(user)
        .stream()
        .map(AddressResponse::fromEntity)
        .toList();
  }
}
