package jyang.deliverydotdot.service;

import jyang.deliverydotdot.dto.CommonUserDetails;
import jyang.deliverydotdot.repository.PartnerRepository;
import jyang.deliverydotdot.repository.RiderRepository;
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

  private final PartnerRepository partnerRepository;

  private final RiderRepository riderRepository;

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

      case ROLE_PARTNER -> partnerRepository.findByLoginId(loginId)
          .map(CommonUserDetails::fromPartner)
          .orElseThrow(() -> new UsernameNotFoundException("Partner not found for ID: " + loginId));

      case ROLE_RIDER -> riderRepository.findByLoginId(loginId)
          .map(CommonUserDetails::fromRider)
          .orElseThrow(() -> new UsernameNotFoundException("Rider not found for ID: " + loginId));
    };

  }
}
