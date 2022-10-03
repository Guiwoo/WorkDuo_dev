package com.workduo.member.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workduo.common.CommonRequestContext;
import com.workduo.configuration.jwt.JwtAuthenticationFilter;
import com.workduo.configuration.jwt.TokenProvider;
import com.workduo.configuration.jwt.memberrefreshtoken.service.MemberRefreshService;
import com.workduo.error.member.exception.MemberException;
import com.workduo.group.group.service.GroupService;
import com.workduo.group.group.service.impl.GroupServiceImpl;
import com.workduo.member.history.service.LoginHistoryService;
import com.workduo.member.member.dto.MemberChangePassword;
import com.workduo.member.member.dto.MemberCreate;
import com.workduo.member.member.dto.MemberEdit;
import com.workduo.member.member.dto.MemberLogin;
import com.workduo.member.member.dto.auth.MemberAuthenticateDto;
import com.workduo.member.member.repository.MemberRepository;
import com.workduo.member.member.service.MemberService;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberController.class)
@Import(
        {
                TokenProvider.class,
                CommonRequestContext.class,
                MemberRepository.class,
                JwtAuthenticationFilter.class,
                MemberException.class
        }
)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("MEMBER API 테스트")
class MemberControllerTest {

    @MockBean
    private MemberService memberService;
    @MockBean
    private GroupService groupService;
    @MockBean
    private MemberRepository memberRepository;
    @MockBean
    private TokenProvider tokenProvider;
    @MockBean
    private MemberRefreshService memberRefreshService;
    @MockBean
    private LoginHistoryService loginHistoryService;

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

    @Nested
    @DisplayName("로그인 API 테스트")
    class Login {
        @Test
        @DisplayName("로그인 실패 [리퀘스트 검증 테스트]")
        void loginDtoWithoutPassword() throws Exception {
            List<String> errors = new ArrayList<>(List.of(
                    "이메일 은 필수 입력 사항 입니다.",
                    "비밀번호 는 필수 입력 사항 입니다."
            ));
            //given
            MemberLogin.Request reqeust = MemberLogin.Request.builder()
                    .build();
            Set<ConstraintViolation<MemberLogin.Request>> violations
                    = validator.validate(reqeust);
            violations
                    .forEach(error -> {
                        assertThat(error.getMessage()).isIn(errors);
                    });
        }

        @Test
        @DisplayName("로그인 성공 [토큰 생성],[리프레시 토큰 함수 호출],[로그인 히스토리 저장]")
        void loginSuccessReturnToken() throws Exception {
            //given
            MemberLogin.Request reqeust = MemberLogin.Request.builder()
                    .email("test@test.com")
                    .password("1q2w3e4r@")
                    .build();
            var auth = MemberAuthenticateDto.builder()
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

            verify(tokenProvider, times(1)).generateToken(any(), any());
            verify(memberRefreshService, times(1)).validateRefreshToken(any());
            verify(loginHistoryService, times(1)).saveLoginHistory(any(), any());
        }
    }

    @Nested
    @DisplayName("회원가입 API 테스트")
    class SiginIn {
        @Test
        @DisplayName("계정 생성 실패[리퀘스트 검증 테스트]")
        void createFailDoesNotExistEmail() throws Exception {
            List<String> errors = new ArrayList<>(
                    List.of("이메일 은 필수 입력 사항 입니다.",
                            "유저이름 은 필수 입력 사항 입니다.",
                            "핸드폰 번호 는 필수 입력 사항 입니다.",
                            "지역 은 최소 1개 이상 선택해야 합니다.",
                            "스포츠 는 최소 1개 이상 선택해야 합니다.",
                            "닉네임 은 필수 입력 사항 입니다.",
                            "비밀번호 는 필수 입력 사항 입니다."));
            //given
            MemberCreate.Request reqeust = MemberCreate.Request
                    .builder()
                    .build();
            Set<ConstraintViolation<MemberCreate.Request>> violations
                    = validator.validate(reqeust);
            violations.forEach(
                    (error) -> {
                        System.out.println(error.getMessage());
                        assertThat(error.getMessage()).isIn(errors);
                    }
            );

            assertThat(violations.size()).isEqualTo(7);
        }

        @Test
        @DisplayName("계정 생성 성공")
        void createSuccess() throws Exception {
            List<String> sggList = new ArrayList<>(List.of("1"));
            List<Integer> sportList = new ArrayList<>(List.of(1));
            MemberCreate.Request reqeust = MemberCreate.Request
                    .builder()
                    .email("test@test.com")
                    .password("1")
                    .username("test")
                    .phoneNumber("1")
                    .siggAreaList(sggList)
                    .nickname("feelingGood")
                    .sportList(sportList)
                    .build();
            doNothing().when(memberService).createUser(reqeust);
            mockMvc.perform(post("/api/v1/member")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(reqeust))
                    )
                    .andExpect(status().isOk())
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("회원정보 수정 API 테스트")
    class EditProfie {
        @Test
        @DisplayName("계정 수정 실패[리퀘스트 검증 테스트]")
        void editFailDoesNotExistData() {
            List<String> errors = new ArrayList<>(
                    List.of(
                            "유저이름 은 필수 입력 사항 입니다.",
                            "핸드폰 번호 는 필수 입력 사항 입니다.",
                            "지역 은 최소 1개 이상 선택해야 합니다.",
                            "스포츠 는 최소 1개 이상 선택해야 합니다.",
                            "닉네임 은 필수 입력 사항 입니다."
                    ));
            //given
            MemberEdit.Request reqeust = MemberEdit.Request
                    .builder()
                    .build();
            Set<ConstraintViolation<MemberEdit.Request>> violations
                    = validator.validate(reqeust);
            violations.forEach(
                    (error) -> {
                        System.out.println(error.getMessage());
                        assertThat(error.getMessage()).isIn(errors);
                    }
            );
        }

        @Test
        @DisplayName("계정 수정 성공")
        void editSuccess() throws Exception {

            MemberEdit.Request reqeust = MemberEdit.Request.builder().build();
            doNothing().when(memberService).editUser(reqeust);
            mockMvc.perform(patch("/api/v1/member")
                            .param("username", "test")
                            .param("phoneNumber", "1")
                            .param("siggAreaList", "1,2")
                            .param("nickname", "feelingGood")
                            .param("sportList", "1,2")
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                    )
                    .andExpect(status().isOk())
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("비밀번호 수정 API 테스트")
    class changePassword {
        @Test
        @DisplayName("비밀번호 수정 실패[리퀘스트 검증 테스트]")
        void editFailDoesNotExistData() throws Exception {
            List<String> errors = new ArrayList<>(
                    List.of(
                            "비밀번호 는 필수 입력 사항 입니다."
                    ));
            //given
            MemberChangePassword.Request reqeust = MemberChangePassword.Request
                    .builder()
                    .build();
            Set<ConstraintViolation<MemberChangePassword.Request>> violations
                    = validator.validate(reqeust);
            violations.forEach(
                    (error) -> {
                        System.out.println(error.getMessage());
                        assertThat(error.getMessage()).isIn(errors);
                    }
            );
        }

        @Test
        @DisplayName("비밀번호 수정 성공")
        void successChangePassword() throws Exception {
            //given
            MemberChangePassword.Request reqeust = MemberChangePassword.Request
                    .builder()
                    .password("123")
                    .build();
            //when
            doNothing().when(memberService).changePassword(reqeust);
            //then
            mockMvc.perform(patch("/api/v1/member/password")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(reqeust))
            )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value("T"))
                    .andDo(print());
            ;
        }
    }

    @Nested
    @DisplayName("회원 탈퇴 API 테스트")
    class withDrawMember{
        @Test
        void successMemberWithdraw() throws Exception {
            //given
            //when
            doNothing().when(memberService).withdraw(groupService);
            //then
            mockMvc.perform(delete("/api/v1/member")
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value("T"))
                    .andDo(print());
            ;
        }
    }

    @Nested
    @DisplayName("회원 정보 API 테스트")
    class getMember{
        @Test
        @DisplayName("회원 정보 API 테스트 성공")
        public void success() throws Exception{
            //given
            //when
            mockMvc.perform(get("/api/v1/member/13"))
                    .andExpect(status().isOk());
            //then
        }
    }
}