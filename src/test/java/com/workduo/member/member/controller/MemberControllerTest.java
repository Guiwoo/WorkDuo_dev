package com.workduo.member.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workduo.common.CommonRequestContext;
import com.workduo.configuration.jwt.JwtAuthenticationFilter;
import com.workduo.configuration.jwt.TokenProvider;
import com.workduo.configuration.jwt.memberrefreshtoken.service.MemberRefreshService;
import com.workduo.error.member.exception.MemberException;
import com.workduo.error.member.type.MemberErrorCode;
import com.workduo.member.member.dto.MemberLoginDto;
import com.workduo.member.member.dto.auth.MemberAuthenticateDto;
import com.workduo.member.member.service.MemberService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberController.class)
@Import(
        {TokenProvider.class, CommonRequestContext.class, JwtAuthenticationFilter.class,MemberException.class}
)
@AutoConfigureMockMvc(addFilters = false)
class MemberControllerTest {

    @MockBean
    private MemberService memberService;

    @MockBean
    private TokenProvider tokenProvider;
    @MockBean
    private MemberRefreshService memberRefreshService;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    public static void init() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    public static void close() {
        factory.close();
    }

    @Test
    @DisplayName("로그인 실패 [비밀번호 데이터 없는경우]")
    void loginDtoWithoutPassword() throws Exception {
        //given
        MemberLoginDto.Request reqeust = MemberLoginDto.Request.builder()
                .email("something")
                .build();
        Set<ConstraintViolation<MemberLoginDto.Request>> violations
                = validator.validate(reqeust);
        violations
                .forEach(error -> {
                    assertThat(error.getMessage()).isEqualTo("비밀번호 는 필수 입력 사항 입니다.");
                });
    }

    @Test
    @DisplayName("로그인 실패 [이메일 데이터 없는경우]")
    void loginDtoWithoutEmail() throws Exception {
        //given
        MemberLoginDto.Request reqeust = MemberLoginDto.Request.builder()
                .password("something")
                .build();
        Set<ConstraintViolation<MemberLoginDto.Request>> violations
                = validator.validate(reqeust);
        violations
                .forEach(error -> {
                    assertThat(error.getMessage()).isEqualTo("이메일 은 필수 입력 사항 입니다.");
                });
    }

    @Test
    @DisplayName("로그인 [이메일 계정 을 찾을수 없는 경우]")
    void loginFailDoesNotExistEmail() throws Exception {
        MemberLoginDto.Request reqeust = MemberLoginDto.Request.builder()
                .email("abc")
                .password("something")
                .build();

        given(memberService.authenticateUser(any()))
                .willThrow(new MemberException(MemberErrorCode.MEMBER_EMAIL_ERROR));

        mockMvc.perform(post("/api/v1/member/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reqeust))
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
    @DisplayName("로그인 성공 후 토큰 생성,리프레시 토큰 함수 호출 확인 여부")
    void loginSuccessReturnToken() throws Exception {
        //given
        MemberLoginDto.Request reqeust = MemberLoginDto.Request.builder()
                .email("test@test.com")
                .password("1q2w3e4r@")
                .build();
        var auth =MemberAuthenticateDto.builder()
                .email("test@test.com")
                .roles(Collections.emptyList())
                .build();
        given(memberService.authenticateUser(any()))
                .willReturn(auth);

        //when
        mockMvc.perform(post("/api/v1/member/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reqeust))
        )
                .andExpect(status().isOk())
                .andDo(print());

        verify(tokenProvider,times(1)).generateToken(auth.getEmail(),auth.getRoles());
        verify(memberRefreshService,times(1)).validateRefreshToken(auth);
        //then
    }
}