package jyang.deliverydotdot.repository;

import java.util.Optional;
import jyang.deliverydotdot.domain.Partner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartnerRepository extends JpaRepository<Partner, Long> {

  boolean existsByLoginId(String longinId);

  boolean existsByEmail(String email);

  boolean existsByPhone(String phone);

  Optional<Partner> findByLoginId(String loginId);
}
