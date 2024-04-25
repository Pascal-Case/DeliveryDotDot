package jyang.deliverydotdot.repository;

import jyang.deliverydotdot.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  boolean existsByLoginId(String longinId);

  boolean existsByEmail(String email);

  boolean existsByPhone(String phone);

}
