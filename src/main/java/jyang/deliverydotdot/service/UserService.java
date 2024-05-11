package jyang.deliverydotdot.service;

import static jyang.deliverydotdot.type.AuthType.LOCAL;
import static jyang.deliverydotdot.type.ErrorCode.ALREADY_REGISTERED_EMAIL;
import static jyang.deliverydotdot.type.ErrorCode.ALREADY_REGISTERED_LOGIN_ID;
import static jyang.deliverydotdot.type.ErrorCode.ALREADY_REGISTERED_PHONE;
import static jyang.deliverydotdot.type.ErrorCode.USER_NOT_FOUND;

import jyang.deliverydotdot.domain.User;
import jyang.deliverydotdot.domain.UserDeliveryAddress;
import jyang.deliverydotdot.dto.UserInfo;
import jyang.deliverydotdot.dto.UserJoinForm;
import jyang.deliverydotdot.exception.RestApiException;
import jyang.deliverydotdot.repository.UserDeliveryAddressRepository;
import jyang.deliverydotdot.repository.UserRepository;
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

  private void validateRegisterUser(UserJoinForm userJoinForm) {
    if (userRepository.existsByLoginId(userJoinForm.getLoginId())) {
      log.warn("Already registered loginId: " + userJoinForm.getLoginId());
      throw new RestApiException(ALREADY_REGISTERED_LOGIN_ID);
    }
    if (userRepository.existsByEmail(userJoinForm.getEmail())) {
      log.warn("Already registered email: " + userJoinForm.getEmail());
      throw new RestApiException(ALREADY_REGISTERED_EMAIL);
    }
    if (userRepository.existsByPhone(userJoinForm.getPhone())) {
      log.warn("Already registered phone: " + userJoinForm.getPhone());
      throw new RestApiException(ALREADY_REGISTERED_PHONE);
    }
  }

  public UserInfo getUserInfo(String username) {
    User user = userRepository.findByLoginId(username)
        .orElseThrow(() -> new RestApiException(USER_NOT_FOUND));

    return UserInfo.fromUser(user);
  }
}
