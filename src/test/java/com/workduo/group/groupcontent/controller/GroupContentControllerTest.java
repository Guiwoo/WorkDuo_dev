package com.workduo.group.groupcontent.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workduo.area.sidoarea.dto.SidoAreaDto;
import com.workduo.area.sidoarea.entity.SidoArea;
import com.workduo.area.siggarea.dto.siggarea.SiggAreaDto;
import com.workduo.area.siggarea.entity.SiggArea;
import com.workduo.common.CommonRequestContext;
import com.workduo.configuration.jwt.JwtAuthenticationFilter;
import com.workduo.configuration.jwt.TokenProvider;
import com.workduo.error.global.handler.GlobalExceptionHandler;
import com.workduo.error.group.exception.GroupException;
import com.workduo.error.group.handler.GroupExceptionHandler;
import com.workduo.error.group.type.GroupErrorCode;
import com.workduo.error.member.exception.MemberException;
import com.workduo.error.member.handler.MemberExceptionHandler;
import com.workduo.error.member.type.MemberErrorCode;
import com.workduo.group.gropcontent.controller.GroupContentController;
import com.workduo.group.gropcontent.dto.creategroupcontent.CreateGroupContent;
import com.workduo.group.gropcontent.dto.detailgroupcontent.DetailGroupContentDto;
import com.workduo.group.gropcontent.dto.detailgroupcontent.GroupContentCommentDto;
import com.workduo.group.gropcontent.dto.detailgroupcontent.GroupContentDto;
import com.workduo.group.gropcontent.dto.detailgroupcontent.GroupContentImageDto;
import com.workduo.group.gropcontent.entity.GroupContent;
import com.workduo.group.gropcontent.service.GroupContentService;
import com.workduo.group.group.dto.GroupDto;
import com.workduo.group.group.entity.Group;
import com.workduo.member.member.entity.Member;
import com.workduo.sport.sport.dto.SportDto;
import com.workduo.sport.sport.entity.Sport;
import com.workduo.sport.sportcategory.dto.SportCategoryDto;
import com.workduo.sport.sportcategory.entity.SportCategory;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.io.FileInputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.workduo.error.group.type.GroupErrorCode.*;
import static com.workduo.error.member.type.MemberErrorCode.MEMBER_EMAIL_ERROR;
import static com.workduo.group.group.type.GroupStatus.GROUP_STATUS_ING;
import static com.workduo.member.member.type.MemberStatus.MEMBER_STATUS_ING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GroupContentController.class)
@Import(
        {
                TokenProvider.class,
                CommonRequestContext.class,
                JwtAuthenticationFilter.class,
                MemberException.class,
                MemberExceptionHandler.class,
                GroupException.class,
                GroupExceptionHandler.class,
                GlobalExceptionHandler.class
        }
)
@AutoConfigureMockMvc(addFilters = false)
public class GroupContentControllerTest {

    @MockBean
    private GroupContentService groupContentService;
    @MockBean
    private TokenProvider tokenProvider;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static ValidatorFactory factory;
    private static Validator validator;

    static Member member;
    static Sport sport;
    static SiggArea siggArea;
    static Group group;
    static GroupDto groupDto;

    @BeforeEach
    public void setup() {
        GroupContentController groupContentController = new GroupContentController(groupContentService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(groupContentController)
                .setControllerAdvice(
                        GroupExceptionHandler.class,
                        MemberExceptionHandler.class,
                        GlobalExceptionHandler.class
                )
                .setCustomArgumentResolvers(
                        new PageableHandlerMethodArgumentResolver()
                )
                .setViewResolvers(
                        ((viewName, locale) -> new MappingJackson2JsonView())
                )
                .build();
    }

    @BeforeAll
    public static void init() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        sport = Sport.builder()
                .id(1)
                .sportCategory(SportCategory.builder()
                        .id(1)
                        .name("구기")
                        .build())
                .name("축구")
                .build();

        siggArea = SiggArea.builder()
                .sgg("11110")
                .sidonm("11")
                .sggnm("종로구")
                .sidonm("서울특별시")
                .sidoArea(SidoArea.builder()
                        .sido("11")
                        .sidonm("서울특별시")
                        .build())
                .build();

        member = Member.builder()
                .id(1L)
                .username("한규빈")
                .phoneNumber("01011111111")
                .nickname("규난")
                .password("1234")
                .email("rbsks147@naver.com")
                .memberStatus(MEMBER_STATUS_ING)
                .build();

        group = Group.builder()
                .id(1L)
                .name("test")
                .sport(sport)
                .limitPerson(10)
                .siggArea(siggArea)
                .thumbnailPath("test")
                .groupStatus(GROUP_STATUS_ING)
                .introduce("test")
                .build();

        groupDto = GroupDto.builder()
                .groupId(1L)
                .introduce("test")
                .thumbnailPath("test")
                .limitPerson(10)
                .sport(SportDto.builder().id(1).build())
                .sportCategory(SportCategoryDto.builder().id(1).build())
                .sidoArea(SidoAreaDto.builder().sido("11").build())
                .siggArea(SiggAreaDto.builder().sgg("11110").build())
                .name("test")
                .build();
    }

    @AfterAll
    public static void close() {
        factory.close();
    }

    @Nested
    public class createGroupContent {

        @Test
        @DisplayName("그룹 피드 생성 - 리퀘스트 파라미터 검증 실패")
        public void createGroupContentRequestFail() throws Exception {
            // given
            List<String> errors = new ArrayList<>(List.of(
                    "제목은 필수 입력 사항입니다.",
                    "내용은 필수 입력 사항입니다.",
                    "정렬값은 최소 0 입니다."
            ));
            CreateGroupContent.Request request = CreateGroupContent.Request.builder().build();

            // when
            Set<ConstraintViolation<CreateGroupContent.Request>> violations =
                    validator.validate(request);

            // then
            violations.forEach(
                    error -> assertThat(error.getMessage()).isIn(errors)
            );
        }

        @Test
        @DisplayName("그룹 피드 생성 성공")
        public void createGroupContent() throws Exception {
            // given
            MockMultipartFile image = new MockMultipartFile(
                    "multipartFiles",
                    "imagefile.jpeg",
                    "image/jpeg",
                    "<<jpeg data>>".getBytes()
            );

            // when

            // then
            mockMvc.perform(multipart("/api/v1/group/1/content")
                    .file(image)
                    .param("title", "test")
                    .param("content", "test")
                    .with(request -> {
                        request.setMethod("POST");
                        return request;
                    })
                    .contentType(MediaType.MULTIPART_FORM_DATA))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value("T"))
                    .andExpect(jsonPath("$.result").isEmpty())
                    .andDo(print());
        }

        @Test
        @DisplayName("그룹 피드 생성 실패 - 유저 정보 없음")
        public void createGroupContentFailNotFoundUser() throws Exception {
            // given
            doThrow(new MemberException(MEMBER_EMAIL_ERROR)).when(groupContentService)
                    .createGroupContent(anyLong(), any(), anyList());

            MockMultipartFile image = new MockMultipartFile(
                    "multipartFiles",
                    "imagefile.jpeg",
                    "image/jpeg",
                    "<<jpeg data>>".getBytes()
            );

            // when

            // then
            mockMvc.perform(multipart("/api/v1/group/1/content")
                            .file(image)
                            .param("title", "test")
                            .param("content", "test")
                            .with(request -> {
                                request.setMethod("POST");
                                return request;
                            })
                            .contentType(MediaType.MULTIPART_FORM_DATA))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.success").value("F"))
                    .andExpect(jsonPath("$.errorMessage").value(MEMBER_EMAIL_ERROR.getMessage()))
                    .andDo(print());
        }

        @Test
        @DisplayName("그룹 피드 생성 실패 - 그룹 정보 없음")
        public void createGroupContentFailNotFoundGroup() throws Exception {
            // given
            doThrow(new GroupException(GROUP_NOT_FOUND)).when(groupContentService)
                    .createGroupContent(anyLong(), any(), anyList());

            MockMultipartFile image = new MockMultipartFile(
                    "multipartFiles",
                    "imagefile.jpeg",
                    "image/jpeg",
                    "<<jpeg data>>".getBytes()
            );

            // when

            // then
            mockMvc.perform(multipart("/api/v1/group/1/content")
                            .file(image)
                            .param("title", "test")
                            .param("content", "test")
                            .with(request -> {
                                request.setMethod("POST");
                                return request;
                            })
                            .contentType(MediaType.MULTIPART_FORM_DATA))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value("F"))
                    .andExpect(jsonPath("$.errorMessage").value(GROUP_NOT_FOUND.getMessage()))
                    .andDo(print());
        }

        @Test
        @DisplayName("그룹 피드 생성 실패 - 이미 삭제된 그룹")
        public void createGroupContentFailAlreadyDeleteGroup() throws Exception {
            // given
            doThrow(new GroupException(GROUP_ALREADY_DELETE_GROUP)).when(groupContentService)
                    .createGroupContent(anyLong(), any(), anyList());

            MockMultipartFile image = new MockMultipartFile(
                    "multipartFiles",
                    "imagefile.jpeg",
                    "image/jpeg",
                    "<<jpeg data>>".getBytes()
            );

            // when

            // then
            mockMvc.perform(multipart("/api/v1/group/1/content")
                            .file(image)
                            .param("title", "test")
                            .param("content", "test")
                            .with(request -> {
                                request.setMethod("POST");
                                return request;
                            })
                            .contentType(MediaType.MULTIPART_FORM_DATA))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value("F"))
                    .andExpect(jsonPath("$.errorMessage").value(GROUP_ALREADY_DELETE_GROUP.getMessage()))
                    .andDo(print());
        }

        @Test
        @DisplayName("그룹 피드 생성 실패 - 그룹에 속한 유저가 아님")
        public void createGroupContentFailNotFoundGroupUser() throws Exception {
            // given
            doThrow(new GroupException(GROUP_NOT_FOUND_USER)).when(groupContentService)
                    .createGroupContent(anyLong(), any(), anyList());

            MockMultipartFile image = new MockMultipartFile(
                    "multipartFiles",
                    "imagefile.jpeg",
                    "image/jpeg",
                    "<<jpeg data>>".getBytes()
            );

            // when

            // then
            mockMvc.perform(multipart("/api/v1/group/1/content")
                            .file(image)
                            .param("title", "test")
                            .param("content", "test")
                            .with(request -> {
                                request.setMethod("POST");
                                return request;
                            })
                            .contentType(MediaType.MULTIPART_FORM_DATA))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value("F"))
                    .andExpect(jsonPath("$.errorMessage").value(GROUP_NOT_FOUND_USER.getMessage()))
                    .andDo(print());
        }

        @Test
        @DisplayName("그룹 피드 생성 실패 - 이미 탈퇴한 그룹")
        public void createGroupContentFailAlreadyWithdrawUser() throws Exception {
            // given
            doThrow(new GroupException(GROUP_ALREADY_WITHDRAW)).when(groupContentService)
                    .createGroupContent(anyLong(), any(), anyList());

            MockMultipartFile image = new MockMultipartFile(
                    "multipartFiles",
                    "imagefile.jpeg",
                    "image/jpeg",
                    "<<jpeg data>>".getBytes()
            );

            // when

            // then
            mockMvc.perform(multipart("/api/v1/group/1/content")
                            .file(image)
                            .param("title", "test")
                            .param("content", "test")
                            .with(request -> {
                                request.setMethod("POST");
                                return request;
                            })
                            .contentType(MediaType.MULTIPART_FORM_DATA))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value("F"))
                    .andExpect(jsonPath("$.errorMessage").value(GROUP_ALREADY_WITHDRAW.getMessage()))
                    .andDo(print());
        }
    }

    @Nested
    public class groupContentList {

        @Test
        @DisplayName("그룹 피드 리스트 성공")
        public void detailGroupContent() throws Exception {
            // given
            GroupContentDto groupContentDto = GroupContentDto.builder()
                    .id(1L)
                    .title("test title")
                    .content("test content")
                    .memberId(1L)
                    .username("test username")
                    .nickname("test user nickname")
                    .profileImg("test")
                    .deletedYn(false)
                    .createdAt(LocalDateTime.now())
                    .contentLike(1L)
                    .build();

            List<GroupContentDto> groupContentDtoList =
                    new ArrayList<>(List.of(groupContentDto));
            Page<GroupContentDto> groupContentDtos = new PageImpl<>(groupContentDtoList);
            doReturn(groupContentDtos).when(groupContentService)
                    .groupContentList(any(), anyLong());
            // when

            // then
            mockMvc.perform(get("/api/v1/group/1/content")
                    .param("page", "0")
                    .param("size", "5")
                    .contentType(MediaType.APPLICATION_JSON)
            )
                    .andExpect(status().isOk())
                    .andDo(print());
        }

        @Test
        @DisplayName("그룹 피드 리스트 실패 - 유저 정보 없음")
        public void groupContentListFailNotFoundUser() throws Exception {
            // given

            // when
            doThrow(new MemberException(MEMBER_EMAIL_ERROR)).when(groupContentService)
                    .groupContentList(any(), anyLong());

            // then
            mockMvc.perform(get("/api/v1/group/1/content")
                            .param("page", "0")
                            .param("size", "5")
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.success").value("F"))
                    .andExpect(jsonPath("$.errorMessage").value(MEMBER_EMAIL_ERROR.getMessage()))
                    .andDo(print());
        }

        @Test
        @DisplayName("그룹 피드 리스트 실패 - 그룹 정보 없음")
        public void groupContentListFailNotFoundGroup() throws Exception {
            // given

            // when
            doThrow(new GroupException(GROUP_NOT_FOUND)).when(groupContentService)
                    .groupContentList(any(), anyLong());

            // then
            mockMvc.perform(get("/api/v1/group/1/content")
                            .param("page", "0")
                            .param("size", "5")
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value("F"))
                    .andExpect(jsonPath("$.errorMessage").value(GROUP_NOT_FOUND.getMessage()))
                    .andDo(print());
        }

        @Test
        @DisplayName("그룹 피드 리스트 실패 - 이미 삭제된 그룹")
        public void groupContentListFailAlreadDeleteGroup() throws Exception {
            // given

            // when
            doThrow(new GroupException(GROUP_ALREADY_DELETE_GROUP)).when(groupContentService)
                    .groupContentList(any(), anyLong());

            // then
            mockMvc.perform(get("/api/v1/group/1/content")
                            .param("page", "0")
                            .param("size", "5")
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value("F"))
                    .andExpect(jsonPath("$.errorMessage").value(GROUP_ALREADY_DELETE_GROUP.getMessage()))
                    .andDo(print());
        }

        @Test
        @DisplayName("그룹 피드 리스트 실패 - 그룹에 해당 유저가 없는 경우")
        public void groupContentListFailNotFoundGroupUser() throws Exception {
            // given

            // when
            doThrow(new GroupException(GROUP_NOT_FOUND_USER)).when(groupContentService)
                    .groupContentList(any(), anyLong());

            // then
            mockMvc.perform(get("/api/v1/group/1/content")
                            .param("page", "0")
                            .param("size", "5")
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value("F"))
                    .andExpect(jsonPath("$.errorMessage").value(GROUP_NOT_FOUND_USER.getMessage()))
                    .andDo(print());
        }

        @Test
        @DisplayName("그룹 피드 리스트 실패 - 이미 그룹을 탈퇴한 유저")
        public void groupContentListFailAlreadtWithdrawUser() throws Exception {
            // given

            // when
            doThrow(new GroupException(GROUP_ALREADY_WITHDRAW)).when(groupContentService)
                    .groupContentList(any(), anyLong());

            // then
            mockMvc.perform(get("/api/v1/group/1/content")
                            .param("page", "0")
                            .param("size", "5")
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value("F"))
                    .andExpect(jsonPath("$.errorMessage").value(GROUP_ALREADY_WITHDRAW.getMessage()))
                    .andDo(print());
        }
    }

    @Nested
    public class detailGroupContent {

        @Test
        @DisplayName("그룹 피드 상세 성공")
        public void detailGroupContent() throws Exception {
            // given
            GroupContentDto groupContentDto = GroupContentDto.builder()
                    .id(1L)
                    .title("test title")
                    .content("test content")
                    .memberId(1L)
                    .username("tset username")
                    .nickname("test user nickname")
                    .profileImg("test profile path")
                    .deletedYn(false)
                    .createdAt(LocalDateTime.now())
                    .contentLike(1L)
                    .build();

            GroupContentImageDto groupContentImageDto = GroupContentImageDto.builder()
                    .id(1L)
                    .imagePath("teste file path")
                    .build();
            List<GroupContentImageDto> groupContentImageDtos =
                    new ArrayList<>(List.of(groupContentImageDto));

            GroupContentCommentDto groupContentCommentDto = GroupContentCommentDto.builder()
                    .commentId(1L)
                    .memberId(1L)
                    .username("tset username")
                    .nickname("test user nickname")
                    .profileImg("test profile path")
                    .groupContentId(1L)
                    .content("test content")
                    .createdAt(LocalDateTime.now())
                    .commentLike(1L)
                    .build();
            List<GroupContentCommentDto> groupContentCommentDtos =
                    new ArrayList<>(List.of(groupContentCommentDto));
            Page<GroupContentCommentDto> groupContentCommentDtoPage =
                    new PageImpl<>(groupContentCommentDtos);
            DetailGroupContentDto detailGroupContentDto =
                    DetailGroupContentDto.from(
                            groupContentDto,
                            groupContentImageDtos,
                            groupContentCommentDtoPage
                    );

            doReturn(detailGroupContentDto).when(groupContentService)
                    .detailGroupContent(anyLong(), anyLong());
            // when

            // then
            mockMvc.perform(get("/api/v1/group/1/content/1")
                    .contentType(MediaType.APPLICATION_JSON)
            )
                    .andExpect(status().isOk())
                    .andDo(print());
        }

        @Test
        @DisplayName("그룹 피드 상세 실패 - 유저 정보 없음")
        public void detailGroupContentFailNotFoundUser() throws Exception {
            // given

            // when
            doThrow(new MemberException(MEMBER_EMAIL_ERROR)).when(groupContentService)
                    .detailGroupContent(anyLong(), anyLong());

            // then
            mockMvc.perform(get("/api/v1/group/1/content/1")
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.success").value("F"))
                    .andExpect(jsonPath("$.errorMessage").value(MEMBER_EMAIL_ERROR.getMessage()))
                    .andDo(print());
        }

        @Test
        @DisplayName("그룹 피드 상세 실패 - 그룹 정보 없음")
        public void detailGroupContentFailNotFoundGroup() throws Exception {
            // given

            // when
            doThrow(new GroupException(GROUP_NOT_FOUND)).when(groupContentService)
                    .detailGroupContent(anyLong(), anyLong());

            // then
            mockMvc.perform(get("/api/v1/group/1/content/1")
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value("F"))
                    .andExpect(jsonPath("$.errorMessage").value(GROUP_NOT_FOUND.getMessage()))
                    .andDo(print());
        }

        @Test
        @DisplayName("그룹 피드 상세 실패 - 그룹 피드 없음")
        public void detailGroupContentFailNotFoundGroupContent() throws Exception {
            // given

            // when
            doThrow(new GroupException(GROUP_NOT_FOUND_CONTENT)).when(groupContentService)
                    .detailGroupContent(anyLong(), anyLong());

            // then
            mockMvc.perform(get("/api/v1/group/1/content/1")
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value("F"))
                    .andExpect(jsonPath("$.errorMessage").value(GROUP_NOT_FOUND_CONTENT.getMessage()))
                    .andDo(print());
        }

        @Test
        @DisplayName("그룹 피드 상세 실패 - 이미 삭제된 그룹")
        public void detailGroupContentFailAlreadyDeleteGroup() throws Exception {
            // given

            // when
            doThrow(new GroupException(GROUP_ALREADY_DELETE_GROUP)).when(groupContentService)
                    .detailGroupContent(anyLong(), anyLong());

            // then
            mockMvc.perform(get("/api/v1/group/1/content/1")
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value("F"))
                    .andExpect(jsonPath("$.errorMessage").value(GROUP_ALREADY_DELETE_GROUP.getMessage()))
                    .andDo(print());
        }

        @Test
        @DisplayName("그룹 피드 상세 실패 - 그룹에서 해당 유저를 찾을 수 없음")
        public void detailGroupContentFailNotFoundGroupUser() throws Exception {
            // given

            // when
            doThrow(new GroupException(GROUP_NOT_FOUND_USER)).when(groupContentService)
                    .detailGroupContent(anyLong(), anyLong());

            // then
            mockMvc.perform(get("/api/v1/group/1/content/1")
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value("F"))
                    .andExpect(jsonPath("$.errorMessage").value(GROUP_NOT_FOUND_USER.getMessage()))
                    .andDo(print());
        }

        @Test
        @DisplayName("그룹 피드 상세 실패 - 이미 탈퇴한 유저")
        public void detailGroupContentFailAlreadyWithdrawUser() throws Exception {
            // given

            // when
            doThrow(new GroupException(GROUP_ALREADY_WITHDRAW)).when(groupContentService)
                    .detailGroupContent(anyLong(), anyLong());

            // then
            mockMvc.perform(get("/api/v1/group/1/content/1")
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value("F"))
                    .andExpect(jsonPath("$.errorMessage").value(GROUP_ALREADY_WITHDRAW.getMessage()))
                    .andDo(print());
        }

        @Test
        @DisplayName("그룹 피드 상세 실패 - 피드가 삭제된 경우")
        public void detailGroupContentFailAlreadyDeleteContent() throws Exception {
            // given

            // when
            doThrow(new GroupException(GROUP_ALREADY_DELETE_CONTENT)).when(groupContentService)
                    .detailGroupContent(anyLong(), anyLong());

            // then
            mockMvc.perform(get("/api/v1/group/1/content/1")
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value("F"))
                    .andExpect(jsonPath("$.errorMessage").value(GROUP_ALREADY_DELETE_CONTENT.getMessage()))
                    .andDo(print());
        }
    }

    @Nested
    public class groupContentLike {

        @Test
        @DisplayName("그룹 피드 좋아요 성공")
        public void groupContentLike() throws Exception {
            // given

            // when

            // then
            mockMvc.perform(post("/api/v1/group/1/content/1/like"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value("T"))
                    .andExpect(jsonPath("$.result").isEmpty())
                    .andDo(print());

            verify(groupContentService, times(1))
                    .groupContentLike(anyLong(), anyLong());
        }

        @Test
        @DisplayName("그룹 피드 좋아요 실패 - 유저 정보 없음")
        public void groupContentLikeFailNotFoundUser() throws Exception {
            // given

            // when
            doThrow(new MemberException(MEMBER_EMAIL_ERROR)).when(groupContentService)
                    .groupContentLike(anyLong(), anyLong());
            // then
            mockMvc.perform(post("/api/v1/group/1/content/1/like"))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.success").value("F"))
                    .andExpect(jsonPath("$.errorMessage").value(MEMBER_EMAIL_ERROR.getMessage()))
                    .andDo(print());
        }

        @Test
        @DisplayName("그룹 피드 좋아요 실패 - 그룹 정보 없음")
        public void groupContentLikeFailNotFoundGroup() throws Exception {
            // given

            // when
            doThrow(new GroupException(GROUP_NOT_FOUND)).when(groupContentService)
                    .groupContentLike(anyLong(), anyLong());
            // then
            mockMvc.perform(post("/api/v1/group/1/content/1/like"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value("F"))
                    .andExpect(jsonPath("$.errorMessage").value(GROUP_NOT_FOUND.getMessage()))
                    .andDo(print());
        }

        @Test
        @DisplayName("그룹 피드 좋아요 실패 - 그룹 피드 정보 없음")
        public void groupContentLikeFailNotFoundGroupContent() throws Exception {
            // given

            // when
            doThrow(new GroupException(GROUP_NOT_FOUND_CONTENT)).when(groupContentService)
                    .groupContentLike(anyLong(), anyLong());
            // then
            mockMvc.perform(post("/api/v1/group/1/content/1/like"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value("F"))
                    .andExpect(jsonPath("$.errorMessage").value(GROUP_NOT_FOUND_CONTENT.getMessage()))
                    .andDo(print());
        }

        @Test
        @DisplayName("그룹 피드 좋아요 실패 - 이미 삭제된 그룹")
        public void groupContentLikeFailAlreadyDeleteGroup() throws Exception {
            // given

            // when
            doThrow(new GroupException(GROUP_ALREADY_DELETE_GROUP)).when(groupContentService)
                    .groupContentLike(anyLong(), anyLong());
            // then
            mockMvc.perform(post("/api/v1/group/1/content/1/like"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value("F"))
                    .andExpect(jsonPath("$.errorMessage").value(GROUP_ALREADY_DELETE_GROUP.getMessage()))
                    .andDo(print());
        }

        @Test
        @DisplayName("그룹 피드 좋아요 실패 - 이미 삭제된 피드")
        public void groupContentLikeFailAlreadyContent() throws Exception {
            // given

            // when
            doThrow(new GroupException(GROUP_ALREADY_DELETE_CONTENT)).when(groupContentService)
                    .groupContentLike(anyLong(), anyLong());
            // then
            mockMvc.perform(post("/api/v1/group/1/content/1/like"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value("F"))
                    .andExpect(jsonPath("$.errorMessage").value(GROUP_ALREADY_DELETE_CONTENT.getMessage()))
                    .andDo(print());
        }

        @Test
        @DisplayName("그룹 피드 좋아요 실패 - 이미 탈퇴한 유저")
        public void groupContentLikeFailAlreadyUser() throws Exception {
            // given

            // when
            doThrow(new GroupException(GROUP_ALREADY_WITHDRAW)).when(groupContentService)
                    .groupContentLike(anyLong(), anyLong());
            // then
            mockMvc.perform(post("/api/v1/group/1/content/1/like"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value("F"))
                    .andExpect(jsonPath("$.errorMessage").value(GROUP_ALREADY_WITHDRAW.getMessage()))
                    .andDo(print());
        }

        @Test
        @DisplayName("그룹 피드 좋아요 실패 - 이미 좋아요를 누른 유저")
        public void groupContentLikeFailAlreadyLike() throws Exception {
            // given

            // when
            doThrow(new GroupException(GROUP_ALREADY_LIKE)).when(groupContentService)
                    .groupContentLike(anyLong(), anyLong());
            // then
            mockMvc.perform(post("/api/v1/group/1/content/1/like"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value("F"))
                    .andExpect(jsonPath("$.errorMessage").value(GROUP_ALREADY_LIKE.getMessage()))
                    .andDo(print());
        }
    }

    @Nested
    public class groupContentUnLike {

        @Test
        @DisplayName("그룹 피드 좋아요 취소 성공")
        public void groupContentUnLike() throws Exception {
            // given

            // when

            // then
            mockMvc.perform(delete("/api/v1/group/1/content/1/like"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value("T"))
                    .andExpect(jsonPath("$.result").isEmpty())
                    .andDo(print());

            verify(groupContentService, times(1))
                    .groupContentUnLike(anyLong(), anyLong());
        }

        @Test
        @DisplayName("그룹 피드 좋아요 취소 실패 - 유저 정보 없음")
        public void groupContentUnLikeFailNotFoundUser() throws Exception {
            // given

            // when
            doThrow(new MemberException(MEMBER_EMAIL_ERROR)).when(groupContentService)
                    .groupContentUnLike(anyLong(), anyLong());
            // then
            mockMvc.perform(delete("/api/v1/group/1/content/1/like"))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.success").value("F"))
                    .andExpect(jsonPath("$.errorMessage").value(MEMBER_EMAIL_ERROR.getMessage()))
                    .andDo(print());
        }

        @Test
        @DisplayName("그룹 피드 좋아요 취소 실패 - 그룹 정보 없음")
        public void groupContentUnLikeFailNotFoundGroup() throws Exception {
            // given

            // when
            doThrow(new GroupException(GROUP_NOT_FOUND)).when(groupContentService)
                    .groupContentUnLike(anyLong(), anyLong());
            // then
            mockMvc.perform(delete("/api/v1/group/1/content/1/like"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value("F"))
                    .andExpect(jsonPath("$.errorMessage").value(GROUP_NOT_FOUND.getMessage()))
                    .andDo(print());
        }

        @Test
        @DisplayName("그룹 피드 좋아요 취소 실패 - 그룹 피드 정보 없음")
        public void groupContentUnLikeFailNotFoundGroupContent() throws Exception {
            // given

            // when
            doThrow(new GroupException(GROUP_NOT_FOUND_CONTENT)).when(groupContentService)
                    .groupContentUnLike(anyLong(), anyLong());
            // then
            mockMvc.perform(delete("/api/v1/group/1/content/1/like"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value("F"))
                    .andExpect(jsonPath("$.errorMessage").value(GROUP_NOT_FOUND_CONTENT.getMessage()))
                    .andDo(print());
        }

        @Test
        @DisplayName("그룹 피드 좋아요 취소 실패 - 이미 삭제된 그룹")
        public void groupContentUnLikeFailAlreadyDeleteGroup() throws Exception {
            // given

            // when
            doThrow(new GroupException(GROUP_ALREADY_DELETE_GROUP)).when(groupContentService)
                    .groupContentUnLike(anyLong(), anyLong());
            // then
            mockMvc.perform(delete("/api/v1/group/1/content/1/like"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value("F"))
                    .andExpect(jsonPath("$.errorMessage").value(GROUP_ALREADY_DELETE_GROUP.getMessage()))
                    .andDo(print());
        }

        @Test
        @DisplayName("그룹 피드 좋아요 취소 실패 - 이미 삭제된 피드")
        public void groupContentUnLikeFailAlreadyContent() throws Exception {
            // given

            // when
            doThrow(new GroupException(GROUP_ALREADY_DELETE_CONTENT)).when(groupContentService)
                    .groupContentUnLike(anyLong(), anyLong());
            // then
            mockMvc.perform(delete("/api/v1/group/1/content/1/like"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value("F"))
                    .andExpect(jsonPath("$.errorMessage").value(GROUP_ALREADY_DELETE_CONTENT.getMessage()))
                    .andDo(print());
        }

        @Test
        @DisplayName("그룹 피드 좋아요 취소 실패 - 이미 탈퇴한 유저")
        public void groupContentUnLikeFailAlreadyUser() throws Exception {
            // given

            // when
            doThrow(new GroupException(GROUP_ALREADY_WITHDRAW)).when(groupContentService)
                    .groupContentUnLike(anyLong(), anyLong());
            // then
            mockMvc.perform(delete("/api/v1/group/1/content/1/like"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value("F"))
                    .andExpect(jsonPath("$.errorMessage").value(GROUP_ALREADY_WITHDRAW.getMessage()))
                    .andDo(print());
        }
    }
}
