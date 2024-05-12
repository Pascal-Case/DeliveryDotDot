package jyang.deliverydotdot.repository;

import java.util.Optional;
import jyang.deliverydotdot.domain.Rider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RiderRepository extends JpaRepository<Rider, Long> {

  boolean existsByLoginId(String longinId);

  boolean existsByEmail(String email);

  boolean existsByPhone(String phone);

  Optional<Rider> findByLoginId(String loginId);
}
