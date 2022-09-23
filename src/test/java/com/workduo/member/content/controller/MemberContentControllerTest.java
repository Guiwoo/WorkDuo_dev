package com.workduo.member.content.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.workduo.common.CommonRequestContext;
import com.workduo.configuration.jwt.JwtAuthenticationFilter;
import com.workduo.configuration.jwt.TokenProvider;
import com.workduo.member.content.dto.*;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDateTime;
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
    @Nested
    @DisplayName("개인 피드 목록 API 테스트")
    class getLists{
        // get api 가 실패하는 경우가 뭐가 있을까요 ... 서버가 다운되지 않는이상은 항상 결과를 줄텐데..
        @Test
        public void success() throws Exception{

            MemberContentListDto c = MemberContentListDto.builder()
                    .id(13L)
                    .title("test title")
                    .content("test content")
                    .memberId(1L)
                    .username("user")
                    .profileImg("aws/s3/somewhere")
                    .deletedYn(false)
                    .createdAt(LocalDateTime.now())
                    .count(3L)
                    .memberContentImages(new ArrayList<>())
                    .build();
            List<MemberContentListDto> list = new ArrayList<>(List.of(c));
            Page<MemberContentListDto> plist = new PageImpl<>(list);
            given(memberContentService.getContentList(any())).willReturn(
                plist
            );

            mockMvc.perform(get("/api/v1/member/content/list")
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value("T"))
                    .andExpect(jsonPath("$.result.content.size()").value(1))
                    .andDo(print());

        }
    }

    @Nested
    @DisplayName("개인 프드 상세 API 테스트")
    class getDetail{
        @Test
        public void success() throws Exception{
            MemberContentListDto c = MemberContentListDto.builder()
                    .id(13L)
                    .title("test title")
                    .content("test content")
                    .memberId(1L)
                    .username("user")
                    .profileImg("aws/s3/somewhere")
                    .deletedYn(false)
                    .createdAt(LocalDateTime.now())
                    .count(3L)
                    .memberContentImages(new ArrayList<>())
                    .build();

            MemberContentCommentDto mcc = MemberContentCommentDto.builder()
                    .content("This is test comment!")
                    .id(1L)
                    .likeCnt(123467688L)
                    .build();
            List<MemberContentCommentDto> cList = new ArrayList<>(List.of(mcc));
            Page<MemberContentCommentDto> pList = new PageImpl<>(cList);

            given(memberContentService.getContentDetail(any())).willReturn(
                    MemberContentDetailDto.from(c,pList)
            );

            mockMvc.perform(get("/api/v1/member/content/3")
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value("T"))
                    .andExpect(jsonPath("$.result.size()").value(12))
                    .andExpect(jsonPath("$.result.comments.content[0].likeCnt").value(123467688))
                    .andDo(print());

        }
    }
}