package jyang.deliverydotdot.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jyang.deliverydotdot.type.AuthType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long userId;

  private String loginId;

  private String password;

  private String email;

  private String name;

  private String phone;

  private String address;

  private String deletedAt;

  private AuthType authType;

  private String provider;

  private String providerId;

}
