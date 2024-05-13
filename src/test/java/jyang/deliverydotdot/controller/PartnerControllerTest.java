package jyang.deliverydotdot.controller;

import static jyang.deliverydotdot.type.ErrorCode.ALREADY_REGISTERED_LOGIN_ID;
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
import jyang.deliverydotdot.dto.partner.PartnerInfo;
import jyang.deliverydotdot.dto.partner.PartnerJoinForm;
import jyang.deliverydotdot.dto.partner.PartnerUpdateForm;
import jyang.deliverydotdot.exception.RestApiException;
import jyang.deliverydotdot.security.AuthenticationFacade;
import jyang.deliverydotdot.service.PartnerService;
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
class PartnerControllerTest {

  @MockBean
  private PartnerService partnerService;

  @MockBean
  private AuthenticationFacade authenticationFacade;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  private static final String PARTNER_API_URL = "/api/v1/partners";

  @Test
  void 파트너등록_성공() throws Exception {
    //given
    PartnerJoinForm joinForm = PartnerJoinForm.builder()
        .loginId("loginId123")
        .password("password123")
        .name("김제로")
        .email("abc@deliverydotdot.com")
        .phone("010-1234-5678")
        .address("서울시 강남구 테헤란로 231")
        .build();

    doNothing().when(partnerService).registerPartner(any(PartnerJoinForm.class));

    //when & then
    mockMvc.perform(post(PARTNER_API_URL + "/auth/join")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(joinForm)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.code").value("SUCCESS"))
        .andExpect(jsonPath("$.message").value("파트너를 성공적으로 등록했습니다."))
        .andDo(print());

  }

  @Test
  void 파트너등록_실패_이미등록된아이디() throws Exception {
    //given
    PartnerJoinForm joinForm = PartnerJoinForm.builder()
        .loginId("loginId123")
        .password("password123")
        .name("김제로")
        .email("abc@deliverydotdot.com")
        .phone("010-1234-5678")
        .address("서울시 강남구 테헤란로 231")
        .build();

    doThrow(new RestApiException(ALREADY_REGISTERED_LOGIN_ID)).when(partnerService)
        .registerPartner(any(PartnerJoinForm.class));

    //when & then
    mockMvc.perform(post(PARTNER_API_URL + "/auth/join")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(joinForm)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(ALREADY_REGISTERED_LOGIN_ID.name()))
        .andExpect(jsonPath("$.message").value(ALREADY_REGISTERED_LOGIN_ID.getDescription()))
        .andDo(print());
  }

  @Test
  void 파트너등록_실패_로그인아이디_미입력() throws Exception {
    //given
    PartnerJoinForm joinForm = PartnerJoinForm.builder()
        .password("password123")
        .name("김제로")
        .email("abc@deliverydotdot.com")
        .phone("010-1234-5678")
        .address("서울시 강남구 테헤란로 231")
        .build();

    //when & then
    mockMvc.perform(post(PARTNER_API_URL + "/auth/join")
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
  @WithMockUser
  void 파트너정보조회_성공() throws Exception {
    //given
    PartnerInfo partnerInfo = PartnerInfo.builder()
        .loginId("loginId123")
        .name("김제로")
        .email("abc@deliverydotdot.com")
        .phone("010-1234-5678")
        .address("서울시 강남구 테헤란로 231")
        .build();
    given(authenticationFacade.getUsername()).willReturn("loginId123");
    given(partnerService.getPartnerInfo("loginId123")).willReturn(partnerInfo);

    //then
    mockMvc.perform(get(PARTNER_API_URL)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.loginId").value("loginId123"))
        .andExpect(jsonPath("$.data.name").value("김제로"))
        .andExpect(jsonPath("$.data.email").value("abc@deliverydotdot.com"))
        .andDo(print());
  }

  @Test
  @WithMockUser
  void 파트너정보수정_성공() throws Exception {
    //given
    PartnerUpdateForm updateForm = PartnerUpdateForm.builder()
        .email("abc@deliverydotdot.com")
        .phone("010-1234-5678")
        .address("서울시 강남구 테헤란로 231")
        .build();
    String updateFormJson = objectMapper.writeValueAsString(updateForm);

    given(authenticationFacade.getUsername()).willReturn("login123");

    doNothing().when(partnerService)
        .updatePartnerInfo(any(String.class), any(PartnerUpdateForm.class));

    //when
    mockMvc.perform(put(PARTNER_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(updateFormJson))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("파트너 정보를 성공적으로 수정했습니다."))
        .andDo(print());
  }

  @Test
  @WithMockUser
  void 파트너정보수정_실패_파트너없음() throws Exception {
    //given
    PartnerUpdateForm updateForm = PartnerUpdateForm.builder()
        .email("abc@deliverydotdot.com")
        .phone("010-1234-5678")
        .address("서울시 강남구 테헤란로 231")
        .build();
    String updateFormJson = objectMapper.writeValueAsString(updateForm);

    given(authenticationFacade.getUsername()).willReturn("login");

    doThrow(new RestApiException(PARTNER_NOT_FOUND)).when(partnerService)
        .updatePartnerInfo(any(String.class), any(PartnerUpdateForm.class));

    //when
    mockMvc.perform(put(PARTNER_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(updateFormJson))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value(PARTNER_NOT_FOUND.name()))
        .andExpect(jsonPath("$.message").value(PARTNER_NOT_FOUND.getDescription()))
        .andDo(print());
  }

  @Test
  @WithMockUser
  void 파트너삭제_성공() throws Exception {
    //given
    given(authenticationFacade.getUsername()).willReturn("login123");

    doNothing().when(partnerService).deleteByLoginId("login123");

    //when
    mockMvc.perform(delete(PARTNER_API_URL)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("파트너를 성공적으로 삭제했습니다."))
        .andDo(print());
  }

}