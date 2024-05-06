package jyang.deliverydotdot.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import java.time.LocalDateTime;
import jyang.deliverydotdot.dto.oauth2.OAuth2Response;
import jyang.deliverydotdot.type.AuthType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Inheritance(strategy = InheritanceType.JOINED)
public class User extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long userId;

  @Column(unique = true, nullable = false)
  private String loginId;

  private String password;

  @Column(unique = true, nullable = false)
  private String email;

  @Column(nullable = false)
  private String name;

  @Column(unique = true, nullable = false)
  private String phone;

  private String address;

  private LocalDateTime deletedAt;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private AuthType authType;

  private String provider;

  private String providerId;

  public void updateWithOAuth2Response(OAuth2Response oAuth2Response) {
    this.email = oAuth2Response.getEmail();
    this.name = oAuth2Response.getName();
    this.phone = oAuth2Response.getPhone();
    this.authType = AuthType.OAUTH;
    this.provider = oAuth2Response.getProvider();
    this.providerId = oAuth2Response.getProviderId();
  }
}
