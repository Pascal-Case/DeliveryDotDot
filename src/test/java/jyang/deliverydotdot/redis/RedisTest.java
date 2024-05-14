package jyang.deliverydotdot.redis;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@SpringBootTest
public class RedisTest {

  @Autowired
  private RedisTemplate<String, Object> redisTemplate;

  @Autowired
  private RedisTemplate<String, String> stringRedisTemplate;

  @Test
  void test1() {
    redisTemplate.delete("hello");
    ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
    String key = "hello";
    String value = "redis1";
    valueOperations.set(key, value);

    String fetchedValue = valueOperations.get(key);
    assertThat(fetchedValue).isEqualTo(value);
  }

  @Test
  void test2() {
    redisTemplate.delete("orderStatus");
    HashOperations<String, Object, Object> stringObjectObjectHashOperations = redisTemplate.opsForHash();
    String orderId = "order1";
    String status = "pending";

    stringObjectObjectHashOperations.put("orderStatus", orderId, status);

    Object fetchedStatus = stringObjectObjectHashOperations.get("orderStatus", orderId);
    assertThat(fetchedStatus).isEqualTo(status);
  }

  @Test
  void test3() {
    stringRedisTemplate.delete("driverLocation");
    stringRedisTemplate.delete("orderLocation");

    String driverId = "driver1";
    double driverLongitude = 126.1;
    double driverLatitude = 37.4;
    stringRedisTemplate.opsForGeo()
        .add("driverLocation", new Point(driverLongitude, driverLatitude), driverId);

    for (int i = 0; i < 100; i++) {
      String orderId = "order" + (i + 1);
      double longitude = 126.10001 + Math.random() * Math.random();
      double latitude = 37.40001 + Math.random() * Math.random();
      stringRedisTemplate.opsForGeo()
          .add("orderLocation", new Point(longitude, latitude), orderId);
    }

    // find orders within 5km from driver1
    Distance radius = new Distance(5, org.springframework.data.geo.Metrics.KILOMETERS);
    Circle within = new Circle(new Point(driverLongitude, driverLatitude), radius);

    // find 20 orders
    int count = 20;
    GeoResults<RedisGeoCommands.GeoLocation<String>> results = stringRedisTemplate.opsForGeo()
        .radius("orderLocation", within, RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs()
            .includeDistance().includeCoordinates().sortAscending().limit(count));

    HashMap<String, Double> distances = new HashMap<>();
    for (GeoResult<RedisGeoCommands.GeoLocation<String>> result : results) {
      distances.put(result.getContent().getName(), result.getDistance().getValue());
    }

    System.out.println(distances);

  }
}
