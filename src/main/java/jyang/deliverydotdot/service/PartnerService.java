package jyang.deliverydotdot.service;

import static jyang.deliverydotdot.type.ErrorCode.ALREADY_REGISTERED_EMAIL;
import static jyang.deliverydotdot.type.ErrorCode.ALREADY_REGISTERED_LOGIN_ID;
import static jyang.deliverydotdot.type.ErrorCode.ALREADY_REGISTERED_PHONE;

import jyang.deliverydotdot.domain.Partner;
import jyang.deliverydotdot.dto.partner.PartnerJoinForm;
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

  private void validateRegisterPartner(PartnerJoinForm joinForm) {
    if (partnerRepository.existsByLoginId(joinForm.getLoginId())) {
      throw new RestApiException(ALREADY_REGISTERED_LOGIN_ID);
    }

    if (partnerRepository.existsByEmail(joinForm.getEmail())) {
      throw new RestApiException(ALREADY_REGISTERED_EMAIL);
    }

    if (partnerRepository.existsByPhone(joinForm.getPhone())) {
      throw new RestApiException(ALREADY_REGISTERED_PHONE);
    }
  }
}
