package jyang.deliverydotdot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RedisService {

  private final RedisTemplate<String, String> stringRedisTemplate;

  @Transactional
  public void addOrUpdateRiderLocation(Long riderId, double longitude, double latitude) {
    stringRedisTemplate.opsForGeo()
        .add("riderLocation", new Point(longitude, latitude), riderId.toString());
  }
}
