package jyang.deliverydotdot.controller;

import static jyang.deliverydotdot.type.ErrorCode.ALREADY_REGISTERED_LOGIN_ID;
import static jyang.deliverydotdot.type.ErrorCode.INVALID_DELIVERY_METHOD;
import static jyang.deliverydotdot.type.ErrorCode.PARTNER_NOT_FOUND;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import jyang.deliverydotdot.dto.rider.RiderInfo;
import jyang.deliverydotdot.dto.rider.RiderJoinForm;
import jyang.deliverydotdot.dto.rider.RiderUpdateForm;
import jyang.deliverydotdot.exception.RestApiException;
import jyang.deliverydotdot.security.AuthenticationFacade;
import jyang.deliverydotdot.service.RiderService;
import jyang.deliverydotdot.type.DeliveryMethod;
import jyang.deliverydotdot.type.ErrorCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class RiderControllerTest {

  @MockBean
  private RiderService riderService;

  @MockBean
  private AuthenticationFacade authenticationFacade;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  private static final String RIDER_API_URL = "/api/v1/riders";

  @Test
  void 라이더등록_성공() throws Exception {
    //given
    RiderJoinForm riderJoinForm = RiderJoinForm.builder()
        .loginId("loginId123")
        .password("password123")
        .name("김제로")
        .email("abc@deliverydotdot.com")
        .phone("010-1234-5678")
        .address("서울시 강남구 테헤란로 231")
        .deliveryMethod(DeliveryMethod.BICYCLE.name())
        .deliveryRegion("서울시 강남구")
        .build();

    doNothing().when(riderService).registerRider(any(RiderJoinForm.class));

    //when & then
    mockMvc.perform(post(RIDER_API_URL + "/auth/join")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(riderJoinForm)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.code").value("SUCCESS"))
        .andExpect(jsonPath("$.message").value("라이더를 성공적으로 등록했습니다."))
        .andDo(print());

  }

  @Test
  void 라이더등록_실패_이미등록된아이디() throws Exception {
    //given
    RiderJoinForm joinForm = RiderJoinForm.builder()
        .loginId("loginId123")
        .password("password123")
        .name("김제로")
        .email("abc@deliverydotdot.com")
        .phone("010-1234-5678")
        .address("서울시 강남구 테헤란로 231")
        .deliveryMethod(DeliveryMethod.BICYCLE.name())
        .deliveryRegion("서울시 강남구")
        .build();

    doThrow(new RestApiException(ALREADY_REGISTERED_LOGIN_ID)).when(riderService)
        .registerRider(any(RiderJoinForm.class));

    // when & then
    mockMvc.perform(post(RIDER_API_URL + "/auth/join")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(joinForm)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(ALREADY_REGISTERED_LOGIN_ID.name()))
        .andExpect(jsonPath("$.message").value(ALREADY_REGISTERED_LOGIN_ID.getDescription()))
        .andDo(print());
  }

  @Test
  void 라이더등록_실패_로그인아이디_미입력() throws Exception {
    //given
    RiderJoinForm joinForm = RiderJoinForm.builder()
        .password("password123")
        .name("김제로")
        .email("abc@deliverydotdot.com")
        .phone("010-1234-5678")
        .address("서울시 강남구 테헤란로 231")
        .deliveryMethod(DeliveryMethod.BICYCLE.name())
        .deliveryRegion("서울시 강남구")
        .build();

    //when & then
    mockMvc.perform(post(RIDER_API_URL + "/auth/join")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(joinForm)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(ErrorCode.INVALID_REQUEST.name()))
        .andExpect(jsonPath("$.errors").isArray())
        .andExpect(jsonPath("$.errors[0].field").value("loginId"))
        .andExpect(jsonPath("$.errors[0].message").value("아이디를 입력해 주세요."))
        .andDo(print());
  }

  @Test
  void 라이더등록_실패_배달수단_미입력() throws Exception {
    //given
    RiderJoinForm joinForm = RiderJoinForm.builder()
        .loginId("loginId123")
        .password("password123")
        .name("김제로")
        .email("abc@deliverydotdot.com")
        .phone("010-1234-5678")
        .address("서울시 강남구 테헤란로 231")
        .deliveryRegion("서울시 강남구")
        .build();

    //when & then
    mockMvc.perform(post(RIDER_API_URL + "/auth/join")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(joinForm)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(ErrorCode.INVALID_REQUEST.name()))
        .andExpect(jsonPath("$.errors").isArray())
        .andExpect(jsonPath("$.errors[0].field").value("deliveryMethod"))
        .andExpect(jsonPath("$.errors[0].message").value("배달 수단을 입력해 주세요."))
        .andDo(print());
  }

  @Test
  void 라이더등록_실패_배달수단_유효하지않은값() throws Exception {
    //given
    RiderJoinForm joinForm = RiderJoinForm.builder()
        .loginId("loginId123")
        .password("password123")
        .name("김제로")
        .email("abc@deliverydotdot.com")
        .phone("010-1234-5678")
        .address("서울시 강남구 테헤란로 231")
        .deliveryMethod("INVALID_DELIVERY_METHOD")
        .deliveryRegion("서울시 강남구")
        .build();

    doThrow(new RestApiException(INVALID_DELIVERY_METHOD)).when(riderService)
        .registerRider(any(RiderJoinForm.class));

    //when & then
    mockMvc.perform(post(RIDER_API_URL + "/auth/join")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(joinForm)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(INVALID_DELIVERY_METHOD.name()))
        .andExpect(jsonPath("$.message").value(INVALID_DELIVERY_METHOD.getDescription()))
        .andDo(print());
  }

  @Test
  @WithMockUser
  void 라이더정보조회_성공() throws Exception {
    //given
    RiderInfo riderInfo = RiderInfo.builder()
        .loginId("loginId123")
        .name("김제로")
        .email("abc@deliverydotdot.com")
        .phone("010-1234-5678")
        .address("서울시 강남구 테헤란로 231")
        .deliveryMethod(DeliveryMethod.BICYCLE.name())
        .deliveryRegion("서울시 강남구")
        .build();
    given(authenticationFacade.getUsername()).willReturn("loginId123");
    given(riderService.getRiderInfo("loginId123")).willReturn(riderInfo);

    //then
    mockMvc.perform(get(RIDER_API_URL)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.loginId").value("loginId123"))
        .andExpect(jsonPath("$.data.name").value("김제로"))
        .andExpect(jsonPath("$.data.email").value("abc@deliverydotdot.com"))
        .andExpect(jsonPath("$.data.deliveryMethod").value(DeliveryMethod.BICYCLE.name()))
        .andDo(print());
  }

  @Test
  @WithMockUser
  void 라이더정보수정_성공() throws Exception {
    //given
    RiderUpdateForm updateForm = RiderUpdateForm.builder()
        .email("abc@deliverydotdot.com")
        .phone("010-1234-5678")
        .address("서울시 강남구 테헤란로 231")
        .deliveryMethod(DeliveryMethod.BICYCLE.name())
        .deliveryRegion("서울시 강남구")
        .build();
    String updateFormJson = objectMapper.writeValueAsString(updateForm);

    given(authenticationFacade.getUsername()).willReturn("login123");

    doNothing().when(riderService)
        .updateRiderInfo(any(String.class), any(RiderUpdateForm.class));

    //when
    mockMvc.perform(put(RIDER_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(updateFormJson))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("라이더 정보를 성공적으로 수정했습니다."))
        .andDo(print());
  }

  @Test
  @WithMockUser
  void 라이더정보수정_실패_라이더없음() throws Exception {
    //given
    RiderUpdateForm updateForm = RiderUpdateForm.builder()
        .email("abc@deliverydotdot.com")
        .phone("010-1234-5678")
        .address("서울시 강남구 테헤란로 231")
        .deliveryMethod(DeliveryMethod.BICYCLE.name())
        .deliveryRegion("서울시 강남구")
        .build();
    String updateFormJson = objectMapper.writeValueAsString(updateForm);

    given(authenticationFacade.getUsername()).willReturn("login");

    doThrow(new RestApiException(PARTNER_NOT_FOUND)).when(riderService)
        .updateRiderInfo(any(String.class), any(RiderUpdateForm.class));

    //when
    mockMvc.perform(put(RIDER_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(updateFormJson))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value(PARTNER_NOT_FOUND.name()))
        .andExpect(jsonPath("$.message").value(PARTNER_NOT_FOUND.getDescription()))
        .andDo(print());
  }

  @Test
  @WithMockUser
  void 라이더삭제_성공() throws Exception {
    //given
    given(authenticationFacade.getUsername()).willReturn("login123");

    doNothing().when(riderService).deleteByLoginId("login123");

    //when
    mockMvc.perform(delete(RIDER_API_URL)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("라이더를 성공적으로 삭제했습니다."))
        .andDo(print());
  }

}