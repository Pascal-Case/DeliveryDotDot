package jyang.deliverydotdot.service;

import static jyang.deliverydotdot.type.ErrorCode.INVALID_REQUEST;
import static jyang.deliverydotdot.type.ErrorCode.NO_COORDINATES_FOUND_FOR_ADDRESS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.Collections;
import jyang.deliverydotdot.dto.location.KakaoGeoResponse;
import jyang.deliverydotdot.exception.RestApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class LocationServiceTest {

  @Mock
  private RestTemplate restTemplate;

  @Mock
  private GeometryFactory geometryFactory;

  @InjectMocks
  private LocationService locationService;

  @BeforeEach
  void setUp() {
    locationService = new LocationService(restTemplate, geometryFactory, "testApiKey");
  }

  @Test
  void getCoordinatesFromAddressSuccessTest() {
    KakaoGeoResponse.Document document = new KakaoGeoResponse.Document();
    document.setX("127.123456");
    document.setY("37.123456");

    KakaoGeoResponse kakaoGeoResponse = new KakaoGeoResponse();
    kakaoGeoResponse.setDocuments(Collections.singletonList(document));

    when(restTemplate.exchange(
        any(),
        eq(HttpMethod.GET),
        any(HttpEntity.class),
        eq(KakaoGeoResponse.class)))
        .thenReturn(new ResponseEntity<>(kakaoGeoResponse, HttpStatus.OK));

    // Mock GeometryFactory
    Point mockPoint = new GeometryFactory().createPoint(
        new Coordinate(127.123456, 37.123456));
    when(geometryFactory.createPoint(any(Coordinate.class))).thenReturn(
        mockPoint);

    Point result = locationService.getCoordinatesFromAddress("testAddress");

    assertEquals(mockPoint, result);
  }

  @Test
  void getCoordinatesFromAddressNotFoundTest() {
    KakaoGeoResponse kakaoGeoResponse = new KakaoGeoResponse();
    kakaoGeoResponse.setDocuments(Collections.emptyList());

    when(restTemplate.exchange(
        any(),
        eq(HttpMethod.GET),
        any(HttpEntity.class),
        eq(KakaoGeoResponse.class)))
        .thenReturn(new ResponseEntity<>(kakaoGeoResponse, HttpStatus.OK));

    assertThrows(RestApiException.class,
        () -> locationService.getCoordinatesFromAddress("invalidAddress"));
    assertEquals(NO_COORDINATES_FOUND_FOR_ADDRESS,
        assertThrows(RestApiException.class,
            () -> locationService.getCoordinatesFromAddress("invalidAddress")).getErrorCode());
  }

  @Test
  void httpClientErrorExceptionTest() {
    when(restTemplate.exchange(
        any(),
        eq(HttpMethod.GET),
        any(HttpEntity.class),
        eq(KakaoGeoResponse.class)))
        .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

    assertThrows(RestApiException.class,
        () -> locationService.getCoordinatesFromAddress("badRequest"));
    assertEquals(INVALID_REQUEST,
        assertThrows(RestApiException.class,
            () -> locationService.getCoordinatesFromAddress("badRequest")).getErrorCode());
  }
}
