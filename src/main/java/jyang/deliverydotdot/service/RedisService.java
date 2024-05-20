package jyang.deliverydotdot.service;

import java.util.List;
import java.util.Objects;
import jyang.deliverydotdot.dto.order.OrderDTO.RiderDeliverableOrders;
import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.connection.RedisGeoCommands.GeoRadiusCommandArgs;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RedisService {

  private final RedisTemplate<String, String> stringRedisTemplate;

  private static final int RADIUS = 5;

  @Transactional
  public void addOrUpdateRiderLocation(Long riderId, double longitude, double latitude) {
    stringRedisTemplate.opsForGeo()
        .add("riderLocation", new Point(longitude, latitude), riderId.toString());
  }

  @Transactional
  public void addOrUpdateOrderLocation(Long orderId, double longitude, double latitude) {
    stringRedisTemplate.opsForGeo()
        .add("orderLocation", new Point(longitude, latitude), orderId.toString());
  }

  @Transactional
  public void deleteRiderLocation(Long riderId) {
    stringRedisTemplate.opsForGeo().remove("riderLocation", riderId.toString());
  }

  @Transactional
  public void deleteOrderLocation(Long orderId) {
    stringRedisTemplate.opsForGeo().remove("orderLocation", orderId.toString());
  }

  @Transactional
  public List<RiderDeliverableOrders> getOrdersNearby(Long riderId) {
    GeoRadiusCommandArgs args =
        GeoRadiusCommandArgs.
            newGeoRadiusArgs().includeCoordinates().includeDistance();

    Point riderLocation = Objects.requireNonNull(stringRedisTemplate.opsForGeo()
        .position("riderLocation", riderId.toString())).get(0);

    if (riderLocation == null) {
      return List.of();
    }

    Distance radius = new Distance(RADIUS, Metrics.KILOMETERS);
    Circle within = new Circle(riderLocation, radius);

    int count = 20;
    GeoResults<RedisGeoCommands.GeoLocation<String>> geoResults = stringRedisTemplate.opsForGeo()
        .radius("orderLocation", within, RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs()
            .includeDistance().includeCoordinates().sortAscending().limit(count));

    if (geoResults == null) {
      return List.of();
    }

    return RiderDeliverableOrders.fromGeoResults(Objects.requireNonNull(geoResults));
  }
}
