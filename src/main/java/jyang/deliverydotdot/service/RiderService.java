package jyang.deliverydotdot.service;

import static jyang.deliverydotdot.type.ErrorCode.ALREADY_REGISTERED_EMAIL;
import static jyang.deliverydotdot.type.ErrorCode.ALREADY_REGISTERED_LOGIN_ID;
import static jyang.deliverydotdot.type.ErrorCode.ALREADY_REGISTERED_PHONE;
import static jyang.deliverydotdot.type.ErrorCode.INVALID_DELIVERY_METHOD;

import java.util.Arrays;
import jyang.deliverydotdot.domain.Rider;
import jyang.deliverydotdot.dto.rider.RiderJoinForm;
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

  private void validateRegisterRider(RiderJoinForm joinForm) {

    if (riderRepository.existsByLoginId(joinForm.getLoginId())) {
      throw new RestApiException(ALREADY_REGISTERED_LOGIN_ID);
    }

    if (riderRepository.existsByEmail(joinForm.getEmail())) {
      throw new RestApiException(ALREADY_REGISTERED_EMAIL);
    }

    if (riderRepository.existsByPhone(joinForm.getPhone())) {
      throw new RestApiException(ALREADY_REGISTERED_PHONE);
    }

    Arrays.stream(DeliveryMethod.values())
        .filter(deliveryMethod -> deliveryMethod.name().equals(joinForm.getDeliveryMethod()))
        .findAny()
        .orElseThrow(() -> new RestApiException(INVALID_DELIVERY_METHOD));

  }
}
