package jyang.deliverydotdot.service;

import jyang.deliverydotdot.dto.CommonUserDetails;
import jyang.deliverydotdot.repository.UserRepository;
import jyang.deliverydotdot.type.UserRole;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommonUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  private final Logger logger = LoggerFactory.getLogger(UserRole.class);

  @Override
  public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
    if (!identifier.contains(":")) {
      logger.error("Invalid identifier format: {}", identifier);
      throw new UsernameNotFoundException("Identifier must be in the format 'UserType:loginId'");
    }

    String[] parts = identifier.split(":");
    if (parts.length < 2) {
      logger.error("Invalid identifier format: {}", identifier);
      throw new UsernameNotFoundException("Invalid identifier format");
    }

    UserRole type = UserRole.valueOf(parts[0].toUpperCase());
    String loginId = parts[1];

    return switch (type) {
      case ROLE_USER -> userRepository.findByLoginId(loginId)
          .map(CommonUserDetails::fromUser)
          .orElseGet(() -> {
            logger.error("User not found for ID: {}", loginId);
            throw new UsernameNotFoundException("User not found for ID: " + loginId);
          });

      case ROLE_PARTNER -> null; // TODO: 파트너 로직 추가

      case ROLE_RIDER -> null; // TODO: 라이더 로직 추가
    };

  }
}
