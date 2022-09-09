package com.workduo.configuration.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workduo.error.global.type.GlobalExceptionType;
import com.workduo.error.member.type.MemberErrorCode;
import com.workduo.member.member.dto.MemberLogin;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@WebAppConfiguration
@AutoConfigureMockMvc
class SecurityConfigurationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("권한 없이 모두 포스트 가능")
    void canAccessPageWithoutAuthorization() throws Exception {
        MemberLogin.Request reqeust = MemberLogin.Request.builder()
                .email("abc")
                .password("1q2w3e4r@")
                .build();

        mockMvc.perform(post("/api/v1/member/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                reqeust
                        ))
                )
                .andDo(print())
                .andExpect(status().is(MemberErrorCode.MEMBER_EMAIL_ERROR.getHttpStatus().value()))
                .andExpect(jsonPath("$.success")
                        .value("F"))
                .andExpect(jsonPath("$.errorCode")
                        .value(MemberErrorCode.MEMBER_EMAIL_ERROR.toString()))
                .andExpect(jsonPath("$.errorMessage")
                        .value(MemberErrorCode.MEMBER_EMAIL_ERROR.getMessage()));
    }

    @Test
    @DisplayName("Api 호출 실패 [로그인 안된 호출]")
    void failedAccessApiCallWithoutLogin() throws Exception {

        mockMvc.perform(get("/api/v1/auth"))
                .andDo(print())
                .andExpect(status().is
                        (GlobalExceptionType.LOGIN_ERROR.getHttpStatus().value())
                )
                .andExpect(jsonPath("$.success").value("F"))
                .andExpect(jsonPath("$.message").value(
                        GlobalExceptionType.LOGIN_ERROR.getMessage())
                );
    }

    @Test
    @DisplayName("Api 호출 실패 [로그인 했지만 권한 없음]")
    @WithMockUser(username="user",roles={})
    void failedAccessApiCallWithoutAuthorization() throws Exception {

        mockMvc.perform(get("/api/v1/auth"))
                .andDo(print())
                .andExpect(status().is
                        (GlobalExceptionType.AUTHORIZATION_ERROR.getHttpStatus().value())
                )
                .andExpect(jsonPath("$.success").value("F"))
                .andExpect(jsonPath("$.message").value(
                        GlobalExceptionType.AUTHORIZATION_ERROR.getMessage())
                );
    }
}