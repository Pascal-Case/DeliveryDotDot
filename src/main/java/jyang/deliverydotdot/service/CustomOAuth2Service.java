package jyang.deliverydotdot.service;

import static jyang.deliverydotdot.type.ErrorCode.ALREADY_REGISTERED_EMAIL;
import static jyang.deliverydotdot.type.ErrorCode.ALREADY_REGISTERED_PHONE;

import java.util.Map;
import jyang.deliverydotdot.domain.User;
import jyang.deliverydotdot.dto.oauth2.CustomOAuth2User;
import jyang.deliverydotdot.dto.oauth2.KakaoResponse;
import jyang.deliverydotdot.dto.oauth2.NaverResponse;
import jyang.deliverydotdot.dto.oauth2.OAuth2Response;
import jyang.deliverydotdot.exception.RestApiException;
import jyang.deliverydotdot.repository.UserRepository;
import jyang.deliverydotdot.type.AuthType;
import jyang.deliverydotdot.type.ErrorCode;
import jyang.deliverydotdot.type.OAuth2Type;
import jyang.deliverydotdot.type.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2Service extends DefaultOAuth2UserService {
  
  private final UserRepository userRepository;

  @Override
  @Transactional
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    OAuth2User oAuth2User = super.loadUser(userRequest);

    String registrationId = userRequest.getClientRegistration().getRegistrationId().toUpperCase();

    OAuth2Response oAuth2Response = getOAuth2Response(registrationId, oAuth2User.getAttributes());

    CustomOAuth2User customOAuth2User = new CustomOAuth2User(oAuth2Response, UserRole.ROLE_USER);

    boolean isRegisteredUser = userRepository.existsByLoginId(customOAuth2User.getUsername());

    if (isRegisteredUser) {
      // 이미 등록된 사용자인 경우
      User user = userRepository.findByLoginId(customOAuth2User.getUsername())
          .orElseThrow(() -> new RestApiException(ErrorCode.USER_NOT_FOUND));

      // 등록하고자 하는 이메일이 이미 존재하는 경우
      userRepository.findByEmail(customOAuth2User.getEmail())
          .ifPresent(
              existingUser -> {
                if (!existingUser.getLoginId().equals(user.getLoginId())) {
                  log.warn("Already registered email: " + customOAuth2User.getEmail());
                  throw new RestApiException(ALREADY_REGISTERED_EMAIL);
                }
              });

      // 등록하고자 하는 전화번호가 이미 존재하는 경우
      userRepository.findByPhone(customOAuth2User.getPhone())
          .ifPresent(
              existingUser -> {
                if (!existingUser.getLoginId().equals(user.getLoginId())) {
                  log.warn("Already registered phone: " + customOAuth2User.getPhone());
                  throw new RestApiException(ALREADY_REGISTERED_PHONE);
                }
              });

      user.updateWithOAuth2Response(oAuth2Response);

    } else {
      // 새로운 사용자인 경우
      if (userRepository.existsByEmail(customOAuth2User.getEmail())) {
        log.warn("Already registered email: " + customOAuth2User.getEmail());
        throw new RestApiException(ALREADY_REGISTERED_EMAIL);
      }
      if (userRepository.existsByPhone(customOAuth2User.getPhone())) {
        log.warn("Already registered phone: " + customOAuth2User.getPhone());
        throw new RestApiException(ALREADY_REGISTERED_PHONE);
      }

      User user = registerNewUser(customOAuth2User, registrationId);
      userRepository.save(user);

    }

    return customOAuth2User;
  }

  private User registerNewUser(CustomOAuth2User customOAuth2User, String registrationId) {
    return User.builder()
        .loginId(customOAuth2User.getUsername())
        .name(customOAuth2User.getName())
        .email(customOAuth2User.getEmail())
        .phone(customOAuth2User.getPhone())
        .authType(AuthType.OAUTH)
        .build();
  }

  private OAuth2Response getOAuth2Response(String registrationId, Map<String, Object> attributes) {

    if (OAuth2Type.KAKAO.name().equals(registrationId)) {
      return new KakaoResponse(attributes);
    } else if (OAuth2Type.NAVER.name().equals(registrationId)) {
      return new NaverResponse(attributes);
    } else {
      throw new IllegalArgumentException("지원하지 않는 OAuth2 인증입니다.");
    }

  }
}
