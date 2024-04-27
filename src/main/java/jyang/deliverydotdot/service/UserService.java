package jyang.deliverydotdot.service;

import static jyang.deliverydotdot.type.AuthType.LOCAL;

import jyang.deliverydotdot.domain.User;
import jyang.deliverydotdot.dto.UserJoinForm;
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
      throw new RuntimeException("이미 등록된 로그인 id 입니다.");
    }
    if (userRepository.existsByEmail(userJoinForm.getEmail())) {
      throw new RuntimeException("이미 등록된 이메일 입니다.");
    }
    if (userRepository.existsByLoginId(userJoinForm.getPhone())) {
      throw new RuntimeException("이미 등록된 휴대전화 번호 입니다.");
    }
  }

}
