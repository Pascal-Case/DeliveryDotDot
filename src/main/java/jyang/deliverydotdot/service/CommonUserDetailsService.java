package jyang.deliverydotdot.service;

import jyang.deliverydotdot.dto.CommonUserDetails;
import jyang.deliverydotdot.repository.UserRepository;
import jyang.deliverydotdot.type.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommonUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
    String[] parts = identifier.split(":");
    if (!identifier.contains(":") || parts.length < 2) {
      throw new UsernameNotFoundException("Invalid identifier format : " + identifier
          + ". Must be in the format 'UserType:loginId'");
    }

    UserRole type = UserRole.valueOf(parts[0].toUpperCase());
    String loginId = parts[1];

    return switch (type) {
      case ROLE_USER -> userRepository.findByLoginId(loginId)
          .map(CommonUserDetails::fromUser)
          .orElseThrow(() -> new UsernameNotFoundException("User not found for ID: " + loginId));

      case ROLE_PARTNER -> null; // TODO: 파트너 로직 추가

      case ROLE_RIDER -> null; // TODO: 라이더 로직 추가
    };

  }
}
