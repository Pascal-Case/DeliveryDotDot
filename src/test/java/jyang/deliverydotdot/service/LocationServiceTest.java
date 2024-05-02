package jyang.deliverydotdot.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import jyang.deliverydotdot.dto.location.KakaoGeoResponse;
import jyang.deliverydotdot.exception.RestApiException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Point;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;

@ExtendWith(MockitoExtension.class)
class LocationServiceTest {

  @Mock
  private RestTemplate restTemplate;

  @InjectMocks
  private LocationService locationService;

  @Test
  void 주소로좌표가져오기_성공() {
    // given
    String address = "valid address";
    String url =
        "https://dapi.kakao.com/v2/local/search/address.json?query=" + UriUtils.encode(address,
            StandardCharsets.UTF_8);
    KakaoGeoResponse mockResponse = new KakaoGeoResponse();
    mockResponse.setDocuments(
        List.of(new KakaoGeoResponse.Document("valid address", "37.5665", "126.9780")));

    // when
    when(restTemplate.exchange(eq(url), eq(HttpMethod.GET), any(), eq(KakaoGeoResponse.class)))
        .thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));

    Point result = locationService.getCoordinatesFromAddress(address);

    // then
    assertNotNull(result);
    assertEquals(37.5665, result.getX());
    assertEquals(126.9780, result.getY());
  }

  @Test
  void 주소로좌표가져오기_실패_주소에대한좌표찾을수없음() {
    // given
    String address = "invalid address";
    String url =
        "https://dapi.kakao.com/v2/local/search/address.json?query=" + UriUtils.encode(address,
            StandardCharsets.UTF_8);

    KakaoGeoResponse emptyResponse = new KakaoGeoResponse();
    emptyResponse.setDocuments(Collections.emptyList());

    // when
    when(restTemplate.exchange(eq(url), eq(HttpMethod.GET), any(), eq(KakaoGeoResponse.class)))
        .thenReturn(ResponseEntity.ok(emptyResponse));

    // then
    assertThrows(RestApiException.class, () -> locationService.getCoordinatesFromAddress(address));
  }

  @Test
  void 주소로좌표가져오기_실패_클라이언트에러() {
    // Arrange
    String address = "some address";
    String url =
        "https://dapi.kakao.com/v2/local/search/address.json?query=" + UriUtils.encode(address,
            StandardCharsets.UTF_8);
    when(restTemplate.exchange(eq(url), eq(HttpMethod.GET), any(), eq(KakaoGeoResponse.class)))
        .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

    // then
    assertThrows(RestApiException.class, () -> locationService.getCoordinatesFromAddress(address));
  }

  @Test
  void 주소로좌표가져오기_실패_서버에러() {
    // Arrange
    String address = "some address";
    String url =
        "https://dapi.kakao.com/v2/local/search/address.json?query=" + UriUtils.encode(address,
            StandardCharsets.UTF_8);
    when(restTemplate.exchange(eq(url), eq(HttpMethod.GET), any(), eq(KakaoGeoResponse.class)))
        .thenThrow(new RestClientException("Service Unavailable"));

    // then
    assertThrows(RestApiException.class, () -> locationService.getCoordinatesFromAddress(address));
  }
}
