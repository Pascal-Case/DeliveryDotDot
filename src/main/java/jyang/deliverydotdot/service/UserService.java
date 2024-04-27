package jyang.deliverydotdot.service;

import static jyang.deliverydotdot.type.AuthType.LOCAL;
import static jyang.deliverydotdot.type.ErrorCode.ALREADY_REGISTERED_EMAIL;
import static jyang.deliverydotdot.type.ErrorCode.ALREADY_REGISTERED_LOGIN_ID;
import static jyang.deliverydotdot.type.ErrorCode.ALREADY_REGISTERED_PHONE;

import jyang.deliverydotdot.domain.User;
import jyang.deliverydotdot.dto.UserJoinForm;
import jyang.deliverydotdot.exception.RestApiException;
import jyang.deliverydotdot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

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
  }

  private void validateRegisterUser(UserJoinForm userJoinForm) {
    if (userRepository.existsByLoginId(userJoinForm.getLoginId())) {
      throw new RestApiException(ALREADY_REGISTERED_LOGIN_ID);
    }
    if (userRepository.existsByEmail(userJoinForm.getEmail())) {
      throw new RestApiException(ALREADY_REGISTERED_EMAIL);
    }
    if (userRepository.existsByLoginId(userJoinForm.getPhone())) {
      throw new RestApiException(ALREADY_REGISTERED_PHONE);
    }
  }

}
