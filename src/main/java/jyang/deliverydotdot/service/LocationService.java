package jyang.deliverydotdot.service;

import static jyang.deliverydotdot.type.ErrorCode.EXTERNAL_API_ERROR;
import static jyang.deliverydotdot.type.ErrorCode.INVALID_REQUEST;
import static jyang.deliverydotdot.type.ErrorCode.NO_COORDINATES_FOUND_FOR_ADDRESS;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import jyang.deliverydotdot.dto.location.KakaoGeoResponse;
import jyang.deliverydotdot.exception.RestApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocationService {

  private final RestTemplate restTemplate;

  private final GeometryFactory geometryFactory;

  @Value("${kakao.api.key}")
  private String apiKey;

  public Point getCoordinatesFromAddress(String address) {
    URI uri = UriComponentsBuilder
        .fromHttpUrl("https://dapi.kakao.com/v2/local/search/address.json")
        .queryParam("query", address)
        .queryParam("analyze_type", "exact")
        .encode(StandardCharsets.UTF_8)
        .build()
        .toUri();

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "KakaoAK " + apiKey);
    HttpEntity<String> entity = new HttpEntity<>(headers);

    try {
      ResponseEntity<KakaoGeoResponse> response = restTemplate.exchange(uri, HttpMethod.GET, entity,
          KakaoGeoResponse.class);

      if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null
          && !response.getBody().getDocuments().isEmpty()) {
        KakaoGeoResponse.Document document = response.getBody().getDocuments().get(0);
        // 좌표 생성 및 반환
        return geometryFactory.createPoint(
            new Coordinate(Double.parseDouble(document.getX()),
                Double.parseDouble(document.getY())));
      } else {
        log.error("No coordinates found for address: {}", address);
        throw new RestApiException(NO_COORDINATES_FOUND_FOR_ADDRESS);
      }
    } catch (HttpClientErrorException ex) {
      log.error(
          "HttpClientErrorException occurred while fetching coordinates for address: {}, Status code: {}, Response body: {}",
          address, ex.getStatusCode(), ex.getResponseBodyAsString(), ex);
      throw new RestApiException(INVALID_REQUEST);
    } catch (RestClientException ex) {
      log.error("RestClientException occurred while fetching coordinates for address: {}",
          address, ex);
      throw new RestApiException(EXTERNAL_API_ERROR);
    }
  }
}
