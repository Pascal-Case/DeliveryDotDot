package jyang.deliverydotdot.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import jyang.deliverydotdot.dto.partner.PartnerJoinForm;
import jyang.deliverydotdot.service.PartnerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class PartnerControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private PartnerService partnerService;

  @Autowired
  private ObjectMapper objectMapper;

  private static final String PARTNER_API_URL = "/api/v1/partners/";


  @Test
  void 파트너등록_성공() throws Exception {
    //given
    PartnerJoinForm partnerJoinForm = PartnerJoinForm.builder()
        .loginId("loginId123")
        .password("password123")
        .name("김제로")
        .email("abc@deliverydotdot.com")
        .phone("010-1234-5678")
        .address("서울시 강남구 테헤란로 231")
        .build();

    doNothing().when(partnerService).registerPartner(any(PartnerJoinForm.class));

    //when
    mockMvc.perform(post(PARTNER_API_URL + "/auth/join")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(partnerJoinForm)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.message").value("파트너를 성공적으로 등록했습니다."))
        .andDo(print());

    //then
  }

}