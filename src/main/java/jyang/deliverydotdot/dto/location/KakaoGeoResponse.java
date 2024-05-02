package jyang.deliverydotdot.dto.location;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KakaoGeoResponse {

  private List<Document> documents;


  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Document {

    private String address_name;
    private String x;
    private String y;

  }

}
