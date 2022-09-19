package com.workduo.member.content.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workduo.common.CommonRequestContext;
import com.workduo.configuration.jwt.JwtAuthenticationFilter;
import com.workduo.configuration.jwt.TokenProvider;
import com.workduo.member.content.dto.ContentCreate;
import com.workduo.member.content.entity.MemberContent;
import com.workduo.member.content.repository.MemberContentRepository;
import com.workduo.member.content.service.MemberContentService;
import com.workduo.member.member.dto.MemberLogin;
import com.workduo.member.member.dto.auth.MemberAuthenticateDto;
import com.workduo.member.member.repository.MemberRepository;
import com.workduo.member.member.service.MemberService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberContentController.class)
@Import(
        {
                TokenProvider.class,
                CommonRequestContext.class,
                JwtAuthenticationFilter.class,
        }
)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("MEMBER Content API 테스트")
class MemberContentControllerTest {
    @MockBean
    private TokenProvider tokenProvider;
    @MockBean
    private MemberContentService memberContentService;

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
    @DisplayName("개인 피드 생성 API 테스트")
    class CreateContentApi{

        @Test
        @DisplayName("멤버 피드 생성 실패 [리퀘스트 검증 테스트]")
        public void failCreateMemberContent() throws Exception{
            List<String> errors = new ArrayList<>(
                    List.of("제목 은 필수 입력 사항 입니다.",
                            "내용 은 필수 입력 사항 입니다.",
                            "정렬값은 최소 0 입니다."));
            //given
            ContentCreate.Request reqeust = ContentCreate.Request.builder().build();
            Set<ConstraintViolation<ContentCreate.Request>> violations
                    = validator.validate(reqeust);
            violations.forEach(
                    (error) -> {
                        System.out.println(error.getMessage());
                        assertThat(error.getMessage()).isIn(errors);
                    }
            );

            assertThat(violations.size()).isEqualTo(2);
        }

        @Test
        @DisplayName("개인 피드 생성 성공")
        public void successCreateMemberContent() throws Exception{

            ContentCreate.Request req = ContentCreate.Request.builder().build();
            List<MultipartFile> arr = new ArrayList<>();
            MockMultipartFile image = new MockMultipartFile(
                    "multipartFiles",
                    "imagefile.jpeg",
                    "image/jpeg",
                    "<<jpeg data>>".getBytes()
            );
            //when
            doNothing().when(memberContentService).createContent(req,arr);
            //when
            mockMvc.perform(multipart("/api/v1/member/content")
                            .file(image)
                            .param("title", "test")
                            .param("content", "test")
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isOk())
                    .andDo(print());
        }
    }
}