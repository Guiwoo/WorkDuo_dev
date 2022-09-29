package com.workduo.group.group.controller;

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
import com.workduo.error.member.exception.MemberException;
import com.workduo.error.member.handler.MemberExceptionHandler;
import com.workduo.group.group.dto.CreateGroup;
import com.workduo.group.group.dto.GroupDto;
import com.workduo.group.group.dto.GroupParticipantsDto;
import com.workduo.group.group.dto.ListGroup;
import com.workduo.group.group.entity.Group;
import com.workduo.group.group.service.GroupService;
import com.workduo.group.group.type.GroupStatus;
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
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.*;

import static com.workduo.error.group.type.GroupErrorCode.*;
import static com.workduo.error.member.type.MemberErrorCode.MEMBER_EMAIL_ERROR;
import static com.workduo.group.group.type.GroupRole.GROUP_ROLE_LEADER;
import static com.workduo.group.group.type.GroupStatus.GROUP_STATUS_ING;
import static com.workduo.member.member.type.MemberStatus.MEMBER_STATUS_ING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GroupController.class)
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
public class GroupControllerTest {

    @MockBean
    private GroupService groupService;
    @MockBean
    private TokenProvider tokenProvider;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static ValidatorFactory factory;
    private static Validator validator;

    static Member member;
    static SportCategory sportCategory;
    static Sport sport;
    static SidoArea sidoArea;
    static SiggArea siggArea;
    static Group group;
    static GroupDto groupDto;
    static MockMultipartFile image;

    @BeforeEach
    public void setup() {
        GroupController groupController = new GroupController(groupService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(groupController)
                .setControllerAdvice(
                        GroupExceptionHandler.class,
                        MemberExceptionHandler.class,
                        GlobalExceptionHandler.class
                ).
                setCustomArgumentResolvers(
                        new PageableHandlerMethodArgumentResolver()
                )
                .setViewResolvers(
                        (viewName, locale) -> new MappingJackson2JsonView()
                )
                .build();
    }

    @BeforeAll
    public static void init() {
        image = new MockMultipartFile(
                "multipartFiles",
                "imagefile.jpeg",
                "image/jpeg",
                "<<jpeg data>>".getBytes()
        );

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

        sportCategory = SportCategory.builder()
                .id(1)
                .name("구기")
                .build();

        sidoArea = SidoArea.builder()
                .sido("11")
                .sidonm("서울특별시")
                .build();
    }

    @AfterAll
    public static void close() {
        factory.close();
    }

    @Nested
    public class createGroup {
        @Test
        @DisplayName("그룹 생성 - 리퀘스트 파라미터 검증 실패")
        public void createGroupRequestFail() throws Exception {
            // given
            List<String> errors = new ArrayList<>(List.of(
                    "그룹이름은 필수 입력 사항입니다.",
                    "그룹 인원은 최소 10명입니다.",
                    "그룹 인원은 최대 200명입니다.",
                    "운동은 필수 선택 사항입니다.",
                    "지역은 필수 선택 사항입니다.",
                    "그룹 소개글은 필수 입력 사항입니다."
            ));

            CreateGroup.Request request = CreateGroup.Request.builder().build();
            // when
            Set<ConstraintViolation<CreateGroup.Request>> violations =
                    validator.validate(request);

            // then
            violations.forEach(
                    error -> assertThat(error.getMessage()).isIn(errors)
            );
        }

        @Test
        @DisplayName("그룹 생성 성공")
        public void createGroup() throws Exception {
            // given

            // when

            // then
            mockMvc.perform(multipart("/api/v1/group")
                            .file(image)
                            .param("name", "test")
                            .param("limitPerson", "10")
                            .param("sportId", "1")
                            .param("sgg", "11110")
                            .param("introduce", "test")
                            .with(request -> {
                                request.setMethod("POST");
                                return request;
                            })
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value("T"))
                    .andExpect(jsonPath("$.result").isEmpty())
                    .andDo(print());

            verify(groupService, times(1))
                    .createGroup(any(), any());
        }

        @Test
        @DisplayName("그룹 생성 실패 - 유저 정보 없음")
        public void createGroupFailNotFoundUser() throws Exception {
            // given

            // when
            doThrow(new MemberException(MEMBER_EMAIL_ERROR)).when(groupService)
                    .createGroup(any(), any());

            // then
            mockMvc.perform(multipart("/api/v1/group")
                            .file(image)
                            .param("name", "test")
                            .param("limitPerson", "10")
                            .param("sportId", "1")
                            .param("sgg", "11110")
                            .param("introduce", "test")
                            .with(request -> {
                                request.setMethod("POST");
                                return request;
                            })
                    )
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.success").value("F"))
                    .andExpect(jsonPath("$.errorMessage").value(MEMBER_EMAIL_ERROR.getMessage()))
                    .andDo(print());
        }

        @Test
        @DisplayName("그룹 생성 실패 - 지역 정보 없음")
        public void createGroupFailNotFoundSigg() throws Exception {
            // given

            // when
            doThrow(new IllegalStateException("해당 지역은 없는 지역입니다.")).when(groupService)
                    .createGroup(any(), any());

            // then
            mockMvc.perform(multipart("/api/v1/group")
                            .file(image)
                            .param("name", "test")
                            .param("limitPerson", "10")
                            .param("sportId", "1")
                            .param("sgg", "11110")
                            .param("introduce", "test")
                            .with(request -> {
                                request.setMethod("POST");
                                return request;
                            })
                    )
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.success").value("F"))
                    .andExpect(jsonPath("$.message").value("해당 지역은 없는 지역입니다."))
                    .andDo(print());
        }

        @Test
        @DisplayName("그룹 생성 실패 - 운동 정보 없음")
        public void createGroupFailNotFoundSport() throws Exception {
            // given

            // when
            doThrow(new IllegalStateException("해당 운동은 없는 운동입니다.")).when(groupService)
                    .createGroup(any(), any());

            // then
            mockMvc.perform(multipart("/api/v1/group")
                            .file(image)
                            .param("name", "test")
                            .param("limitPerson", "10")
                            .param("sportId", "1")
                            .param("sgg", "11110")
                            .param("introduce", "test")
                            .with(request -> {
                                request.setMethod("POST");
                                return request;
                            })
                    )
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.success").value("F"))
                    .andExpect(jsonPath("$.message").value("해당 운동은 없는 운동입니다."))
                    .andDo(print());
        }

        @Test
        @DisplayName("그룹 생성 실패 - 그룹 생성 최대개수 초과")
        public void createGroupFailMaximumExceeded() throws Exception {
            // given

            // when
            doThrow(new GroupException(GROUP_MAXIMUM_EXCEEDED)).when(groupService)
                    .createGroup(any(), any());

            // then
            mockMvc.perform(multipart("/api/v1/group")
                            .file(image)
                            .param("name", "test")
                            .param("limitPerson", "10")
                            .param("sportId", "1")
                            .param("sgg", "11110")
                            .param("introduce", "test")
                            .with(request -> {
                                request.setMethod("POST");
                                return request;
                            })
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value("F"))
                    .andExpect(jsonPath("$.errorMessage").value(GROUP_MAXIMUM_EXCEEDED.getMessage()))
                    .andDo(print());
        }
    }

    @Nested
    public class deleteGroup {

        @Test
        @DisplayName("그룹 삭제 성공")
        public void deleteGroup() throws Exception {
            // given

            // when

            // then
            mockMvc.perform(post("/api/v1/group/1")
                    .contentType(MediaType.APPLICATION_JSON)
            )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value("T"))
                    .andExpect(jsonPath("$.result").isEmpty())
                    .andDo(print());

            verify(groupService, times(1))
                    .deleteGroup(anyLong());
        }

        @Test
        @DisplayName("그룹 삭제 실패 - 유저 정보 없음")
        public void deleteGroupNotFoundUser() throws Exception {
            // given

            // when
            doThrow(new MemberException(MEMBER_EMAIL_ERROR)).when(groupService)
                            .deleteGroup(anyLong());

            // then
            mockMvc.perform(post("/api/v1/group/1")
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.success").value("F"))
                    .andExpect(jsonPath("$.errorMessage").value(MEMBER_EMAIL_ERROR.getMessage()))
                    .andDo(print());

        }

        @Test
        @DisplayName("그룹 삭제 실패 - 그룹 정보 없음")
        public void deleteGroupNotFoundGroup() throws Exception {
            // given

            // when
            doThrow(new GroupException(GROUP_NOT_FOUND)).when(groupService)
                    .deleteGroup(anyLong());

            // then
            mockMvc.perform(post("/api/v1/group/1")
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value("F"))
                    .andExpect(jsonPath("$.errorMessage").value(GROUP_NOT_FOUND.getMessage()))
                    .andDo(print());

        }

        @Test
        @DisplayName("그룹 삭제 실패 - 그룹장이 아닐 경우")
        public void deleteGroupNotLeader() throws Exception {
            // given

            // when
            doThrow(new GroupException(GROUP_NOT_LEADER)).when(groupService)
                    .deleteGroup(anyLong());

            // then
            mockMvc.perform(post("/api/v1/group/1")
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value("F"))
                    .andExpect(jsonPath("$.errorMessage").value(GROUP_NOT_LEADER.getMessage()))
                    .andDo(print());

        }
    }

    @Nested
    public class withdrawGroup {

        @Test
        @DisplayName("그룹 탈퇴 성공")
        public void withdrawGroup() throws Exception {
            // given

            // when

            // then
            mockMvc.perform(delete("/api/v1/group/1")
                    .contentType(MediaType.APPLICATION_JSON)
            )
                    .andExpect(jsonPath("$.success").value("T"))
                    .andExpect(jsonPath("$.result").isEmpty())
                    .andDo(print());
        }

        @Test
        @DisplayName("그룹 탈퇴 실패 - 유저 정보 없음")
        public void withdrawGroupNotFoundUser() throws Exception {
            // given

            // when
            doThrow(new MemberException(MEMBER_EMAIL_ERROR)).when(groupService)
                            .withdrawGroup(anyLong());

            // then
            mockMvc.perform(delete("/api/v1/group/1")
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.success").value("F"))
                    .andExpect(jsonPath("$.errorMessage").value(MEMBER_EMAIL_ERROR.getMessage()))
                    .andDo(print());
        }

        @Test
        @DisplayName("그룹 탈퇴 실패 - 그룹 정보 없음")
        public void withdrawGroupNotFoundGroup() throws Exception {
            // given

            // when
            doThrow(new GroupException(GROUP_NOT_FOUND)).when(groupService)
                    .withdrawGroup(anyLong());

            // then
            mockMvc.perform(delete("/api/v1/group/1")
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value("F"))
                    .andExpect(jsonPath("$.errorMessage").value(GROUP_NOT_FOUND.getMessage()))
                    .andDo(print());
        }

        @Test
        @DisplayName("그룹 탈퇴 실패 - 그룹에 속한 유저가 아닐 때")
        public void withdrawGroupNotFoundGroupUser() throws Exception {
            // given

            // when
            doThrow(new GroupException(GROUP_NOT_FOUND_USER)).when(groupService)
                    .withdrawGroup(anyLong());

            // then
            mockMvc.perform(delete("/api/v1/group/1")
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value("F"))
                    .andExpect(jsonPath("$.errorMessage").value(GROUP_NOT_FOUND_USER.getMessage()))
                    .andDo(print());
        }

        @Test
        @DisplayName("그룹 탈퇴 실패 - 이미 삭제된 그룹인 경우")
        public void withdrawGroupAlreadyDeleteGroup() throws Exception {
            // given

            // when
            doThrow(new GroupException(GROUP_ALREADY_DELETE_GROUP)).when(groupService)
                    .withdrawGroup(anyLong());

            // then
            mockMvc.perform(delete("/api/v1/group/1")
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value("F"))
                    .andExpect(jsonPath("$.errorMessage").value(GROUP_ALREADY_DELETE_GROUP.getMessage()))
                    .andDo(print());
        }

        @Test
        @DisplayName("그룹 탈퇴 실패 - 그룹 리더인 경우")
        public void withdrawGroupLeaderNotWithdraw() throws Exception {
            // given

            // when
            doThrow(new GroupException(GROUP_LEADER_NOT_WITHDRAW)).when(groupService)
                    .withdrawGroup(anyLong());

            // then
            mockMvc.perform(delete("/api/v1/group/1")
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value("F"))
                    .andExpect(jsonPath("$.errorMessage").value(GROUP_LEADER_NOT_WITHDRAW.getMessage()))
                    .andDo(print());
        }

        @Test
        @DisplayName("그룹 탈퇴 실패 - 이미 탈퇴한 그룹인 경우")
        public void withdrawGroupAlreadyWithdraw() throws Exception {
            // given

            // when
            doThrow(new GroupException(GROUP_ALREADY_WITHDRAW)).when(groupService)
                    .withdrawGroup(anyLong());

            // then
            mockMvc.perform(delete("/api/v1/group/1")
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value("F"))
                    .andExpect(jsonPath("$.errorMessage").value(GROUP_ALREADY_WITHDRAW.getMessage()))
                    .andDo(print());
        }
    }

    @Nested
    public class detailGroup {

        @Test
        @DisplayName("그룹 상세")
        public void detailGroup() throws Exception {
            // given
            doReturn(groupDto).when(groupService)
                    .groupDetail(anyLong());

            // when

            // then
            mockMvc.perform(get("/api/v1/group/1")
                    .contentType(MediaType.APPLICATION_JSON)
            )
                    .andExpect(jsonPath("$.success").value("T"))
                    .andExpect(jsonPath("$.result.groupId").value(1))
                    .andExpect(jsonPath("$.result.siggArea.sgg").value("11110"))
                    .andExpect(jsonPath("$.result.sidoArea.sido").value("11"))
                    .andExpect(jsonPath("$.result.sport.id").value(1))
                    .andExpect(jsonPath("$.result.sportCategory.id").value(1))
                    .andDo(print());
        }

        @Test
        @DisplayName("그룹 상세 실패 - 이미 삭제된 그룹")
        public void detailGroupFailAlreadyDelete() throws Exception {
            // given

            // when
            doThrow(new GroupException(GROUP_ALREADY_DELETE_GROUP))
                    .when(groupService).groupDetail(anyLong());

            // then
            mockMvc.perform(get("/api/v1/group/1")
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value("F"))
                    .andExpect(jsonPath("$.errorMessage").value(GROUP_ALREADY_DELETE_GROUP.getMessage()))
                    .andDo(print());
        }

        @Test
        @DisplayName("그룹 상세 실패 - 그룹 정보 없음")
        public void detailGroupFailNotFoundGroup() throws Exception {
            // given

            // when
            doThrow(new GroupException(GROUP_NOT_FOUND))
                    .when(groupService).groupDetail(anyLong());

            // then
            mockMvc.perform(get("/api/v1/group/1")
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value("F"))
                    .andExpect(jsonPath("$.errorMessage").value(GROUP_NOT_FOUND.getMessage()))
                    .andDo(print());
        }
    }

    @Nested
    public class groupLike {

        @Test
        @DisplayName("그룹 좋아요")
        public void groupLike() throws Exception {
            // given
            doNothing().when(groupService)
                    .groupLike(anyLong());

            // when

            // then
            mockMvc.perform(post("/api/v1/group/1/like")
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(jsonPath("$.success").value("T"))
                    .andExpect(jsonPath("$.result").isEmpty())
                    .andDo(print());
        }

        @Test
        @DisplayName("그룹 좋아요 실패 - 유저 정보 없음")
        public void groupLikeFailNotFoundUser() throws Exception {
            // given


            // when
            doThrow(new MemberException(MEMBER_EMAIL_ERROR)).when(groupService)
                            .groupLike(anyLong());
            // then
            mockMvc.perform(post("/api/v1/group/1/like")
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(jsonPath("$.success").value("F"))
                    .andExpect(jsonPath("$.errorMessage").value(MEMBER_EMAIL_ERROR.getMessage()))
                    .andDo(print());
        }

        @Test
        @DisplayName("그룹 좋아요 실패 - 그룹 정보 없음")
        public void groupLikeFailNotFoundGroup() throws Exception {
            // given


            // when
            doThrow(new GroupException(GROUP_NOT_FOUND)).when(groupService)
                    .groupLike(anyLong());
            // then
            mockMvc.perform(post("/api/v1/group/1/like")
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(jsonPath("$.success").value("F"))
                    .andExpect(jsonPath("$.errorMessage").value(GROUP_NOT_FOUND.getMessage()))
                    .andDo(print());
        }

        @Test
        @DisplayName("그룹 좋아요 실패 - 이미 좋아요를 누른 경우")
        public void groupLikeFailAlreadyLike() throws Exception {
            // given


            // when
            doThrow(new GroupException(GROUP_ALREADY_LIKE)).when(groupService)
                    .groupLike(anyLong());
            // then
            mockMvc.perform(post("/api/v1/group/1/like")
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(jsonPath("$.success").value("F"))
                    .andExpect(jsonPath("$.errorMessage").value(GROUP_ALREADY_LIKE.getMessage()))
                    .andDo(print());
        }

        @Test
        @DisplayName("그룹 좋아요 실패 - 삭제된 그룹인 경우")
        public void groupLikeFailAlreadyDelete() throws Exception {
            // given


            // when
            doThrow(new GroupException(GROUP_ALREADY_DELETE_GROUP)).when(groupService)
                    .groupLike(anyLong());
            // then
            mockMvc.perform(post("/api/v1/group/1/like")
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(jsonPath("$.success").value("F"))
                    .andExpect(jsonPath("$.errorMessage").value(GROUP_ALREADY_DELETE_GROUP.getMessage()))
                    .andDo(print());
        }

        @Test
        @DisplayName("그룹 좋아요 실패 - 그룹에 속한 유저가 아닌 경우")
        public void groupLikeFailNotFoundGroupUser() throws Exception {
            // given


            // when
            doThrow(new GroupException(GROUP_NOT_FOUND_USER)).when(groupService)
                    .groupLike(anyLong());
            // then
            mockMvc.perform(post("/api/v1/group/1/like")
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(jsonPath("$.success").value("F"))
                    .andExpect(jsonPath("$.errorMessage").value(GROUP_NOT_FOUND_USER.getMessage()))
                    .andDo(print());
        }

        @Test
        @DisplayName("그룹 좋아요 실패 - 그룹을 탈퇴한 유저인 경우")
        public void groupLikeFailAlreadyWithdraw() throws Exception {
            // given


            // when
            doThrow(new GroupException(GROUP_ALREADY_WITHDRAW)).when(groupService)
                    .groupLike(anyLong());
            // then
            mockMvc.perform(post("/api/v1/group/1/like")
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(jsonPath("$.success").value("F"))
                    .andExpect(jsonPath("$.errorMessage").value(GROUP_ALREADY_WITHDRAW.getMessage()))
                    .andDo(print());
        }
    }

    @Nested
    public class groupUnLike {

        @Test
        @DisplayName("그룹 좋아요 취소")
        public void groupUnLike() throws Exception {
            // given
            doNothing().when(groupService)
                    .groupUnLike(anyLong());

            // when

            // then
            mockMvc.perform(delete("/api/v1/group/1/like")
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(jsonPath("$.success").value("T"))
                    .andExpect(jsonPath("$.result").isEmpty())
                    .andDo(print());
        }

        @Test
        @DisplayName("그룹 좋아요 취소 실패 - 유저 정보 없음")
        public void groupUnLikeFailNotFoundUser() throws Exception {
            // given


            // when
            doThrow(new MemberException(MEMBER_EMAIL_ERROR)).when(groupService)
                    .groupUnLike(anyLong());
            // then
            mockMvc.perform(delete("/api/v1/group/1/like")
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(jsonPath("$.success").value("F"))
                    .andExpect(jsonPath("$.errorMessage").value(MEMBER_EMAIL_ERROR.getMessage()))
                    .andDo(print());
        }

        @Test
        @DisplayName("그룹 좋아요 취소 실패 - 그룹 정보 없음")
        public void groupUnLikeFailNotFoundGroup() throws Exception {
            // given


            // when
            doThrow(new GroupException(GROUP_NOT_FOUND)).when(groupService)
                    .groupUnLike(anyLong());
            // then
            mockMvc.perform(delete("/api/v1/group/1/like")
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(jsonPath("$.success").value("F"))
                    .andExpect(jsonPath("$.errorMessage").value(GROUP_NOT_FOUND.getMessage()))
                    .andDo(print());
        }

        @Test
        @DisplayName("그룹 좋아요 취소 실패 - 삭제된 그룹인 경우")
        public void groupUnLikeFailAlreadyDelete() throws Exception {
            // given


            // when
            doThrow(new GroupException(GROUP_ALREADY_DELETE_GROUP)).when(groupService)
                    .groupUnLike(anyLong());
            // then
            mockMvc.perform(delete("/api/v1/group/1/like")
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(jsonPath("$.success").value("F"))
                    .andExpect(jsonPath("$.errorMessage").value(GROUP_ALREADY_DELETE_GROUP.getMessage()))
                    .andDo(print());
        }

        @Test
        @DisplayName("그룹 좋아요 취소 실패 - 그룹에 속한 유저가 아닌 경우")
        public void groupUnLikeFailNotFoundGroupUser() throws Exception {
            // given


            // when
            doThrow(new GroupException(GROUP_NOT_FOUND_USER)).when(groupService)
                    .groupUnLike(anyLong());
            // then
            mockMvc.perform(delete("/api/v1/group/1/like")
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(jsonPath("$.success").value("F"))
                    .andExpect(jsonPath("$.errorMessage").value(GROUP_NOT_FOUND_USER.getMessage()))
                    .andDo(print());
        }

        @Test
        @DisplayName("그룹 좋아요 취소 실패 - 그룹을 탈퇴한 유저인 경우")
        public void groupUnLikeFailAlreadyWithdraw() throws Exception {
            // given


            // when
            doThrow(new GroupException(GROUP_ALREADY_WITHDRAW)).when(groupService)
                    .groupUnLike(anyLong());
            // then
            mockMvc.perform(delete("/api/v1/group/1/like")
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(jsonPath("$.success").value("F"))
                    .andExpect(jsonPath("$.errorMessage").value(GROUP_ALREADY_WITHDRAW.getMessage()))
                    .andDo(print());
        }
    }

    @Nested
    public class groupParticipant {

        @Test
        @DisplayName("그룹 참여")
        public void groupParticipant() throws Exception {
            // given
            doNothing().when(groupService)
                    .groupParticipant(anyLong());

            // when

            // then
            mockMvc.perform(post("/api/v1/group/1/participant")
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(jsonPath("$.success").value("T"))
                    .andExpect(jsonPath("$.result").isEmpty())
                    .andDo(print());
        }

        @Test
        @DisplayName("그룹 참여 실패 - 유저 정보 없음")
        public void groupParticipantFailNotFoundUser() throws Exception {
            // given


            // when
            doThrow(new MemberException(MEMBER_EMAIL_ERROR)).when(groupService)
                    .groupParticipant(anyLong());
            // then
            mockMvc.perform(post("/api/v1/group/1/participant")
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(jsonPath("$.success").value("F"))
                    .andExpect(jsonPath("$.errorMessage").value(MEMBER_EMAIL_ERROR.getMessage()))
                    .andDo(print());
        }

        @Test
        @DisplayName("그룹 참여 실패 - 그룹 정보 없음")
        public void groupParticipantFailNotFoundGroup() throws Exception {
            // given


            // when
            doThrow(new GroupException(GROUP_NOT_FOUND)).when(groupService)
                    .groupParticipant(anyLong());
            // then
            mockMvc.perform(post("/api/v1/group/1/participant")
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(jsonPath("$.success").value("F"))
                    .andExpect(jsonPath("$.errorMessage").value(GROUP_NOT_FOUND.getMessage()))
                    .andDo(print());
        }

        @Test
        @DisplayName("그룹 참여 실패 - 이미 참가한 그룹인 경우")
        public void participantFailAlreadyParticipant() throws Exception {
            // given


            // when
            doThrow(new GroupException(GROUP_ALREADY_PARTICIPANT)).when(groupService)
                    .groupParticipant(anyLong());
            // then
            mockMvc.perform(post("/api/v1/group/1/participant")
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(jsonPath("$.success").value("F"))
                    .andExpect(jsonPath("$.errorMessage").value(GROUP_ALREADY_PARTICIPANT.getMessage()))
                    .andDo(print());
        }

        @Test
        @DisplayName("그룹 참여 실패 - 삭제된 그룹인 경우")
        public void participantFailAlreadyDelete() throws Exception {
            // given


            // when
            doThrow(new GroupException(GROUP_ALREADY_DELETE_GROUP)).when(groupService)
                    .groupParticipant(anyLong());
            // then
            mockMvc.perform(post("/api/v1/group/1/participant")
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(jsonPath("$.success").value("F"))
                    .andExpect(jsonPath("$.errorMessage").value(GROUP_ALREADY_DELETE_GROUP.getMessage()))
                    .andDo(print());
        }

        @Test
        @DisplayName("그룹 참여 실패 - 그룹 참여인원 정원 초과인 경우")
        public void groupUnLikeFailNotFoundGroupUser() throws Exception {
            // given


            // when
            doThrow(new GroupException(GROUP_MAXIMUM_PARTICIPANT)).when(groupService)
                    .groupParticipant(anyLong());
            // then
            mockMvc.perform(post("/api/v1/group/1/participant")
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(jsonPath("$.success").value("F"))
                    .andExpect(jsonPath("$.errorMessage").value(GROUP_MAXIMUM_PARTICIPANT.getMessage()))
                    .andDo(print());
        }
    }

    @Nested
    public class groupList {

        @Test
        @DisplayName("그룹 리스트 성공")
        public void groupList() throws Exception {
            // given
            SidoAreaDto sidoAreaDto = SidoAreaDto.fromEntity(sidoArea);
            SiggAreaDto siggAreaDto = SiggAreaDto.fromEntity(siggArea);
            SportCategoryDto sportCategoryDto = SportCategoryDto.fromEntity(sportCategory);
            SportDto sportDto = SportDto.fromEntity(sport);

            GroupDto groupDto = GroupDto.builder()
                    .groupId(1L)
                    .sidoArea(sidoAreaDto)
                    .siggArea(siggAreaDto)
                    .sport(sportDto)
                    .sportCategory(sportCategoryDto)
                    .introduce("test")
                    .limitPerson(10)
                    .likes(10L)
                    .participants(20L)
                    .thumbnailPath("test")
                    .name("group1")
                    .build();

            List<GroupDto> groupDtoList = new ArrayList<>(List.of(groupDto));
            Page<GroupDto> groupDtos = new PageImpl<>(groupDtoList);

            doReturn(groupDtos).when(groupService)
                    .groupList(any(), any());

            // when

            // then
            mockMvc.perform(get("/api/v1/group")
                            .param("page", "0")
                            .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isOk())
                    .andDo(print());
        }

        @Nested
        public class groupParticipantList {

            @Test
            @DisplayName("그룹 참여자 리스트 성공")
            public void groupParticipantList() throws Exception {
                // given
                GroupParticipantsDto groupParticipantsDto = GroupParticipantsDto.builder()
                        .userId(1L)
                        .username("test")
                        .nickname("test")
                        .profileImg("test")
                        .groupRole(GROUP_ROLE_LEADER)
                        .build();
                List<GroupParticipantsDto> participantsDtoList =
                        new ArrayList<>(List.of(groupParticipantsDto));
                Page<GroupParticipantsDto> participantsDtos = new PageImpl<>(participantsDtoList);

                doReturn(participantsDtos).when(groupService)
                        .groupParticipantList(any(), anyLong());

                // when

                // then
                mockMvc.perform(get("/api/v1/group/participant/1")
                                .param("page", "0")
                                .param("size", "5")
                                .contentType(MediaType.APPLICATION_JSON)
                        )
                        .andExpect(status().isOk())
                        .andDo(print());
            }

            @Test
            @DisplayName("그룹 참여자 리스트 실패 - 유저 정보 없음")
            public void groupParticipantListFailNotFoundUser() throws Exception {
                // given

                // when
                doThrow(new MemberException(MEMBER_EMAIL_ERROR)).when(groupService)
                                .groupParticipantList(any(), anyLong());

                // then
                mockMvc.perform(get("/api/v1/group/participant/1")
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
            @DisplayName("그룹 참여자 리스트 실패 - 그룹 없음")
            public void groupParticipantListFailNotFoundGroup() throws Exception {
                // given

                // when
                doThrow(new GroupException(GROUP_NOT_FOUND)).when(groupService)
                        .groupParticipantList(any(), anyLong());

                // then
                mockMvc.perform(get("/api/v1/group/participant/1")
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
            @DisplayName("그룹 참여자 리스트 실패 - 그룹에 해당 유저 없음")
            public void groupParticipantListFailNotFoundGroupUser() throws Exception {
                // given

                // when
                doThrow(new GroupException(GROUP_NOT_FOUND_USER)).when(groupService)
                        .groupParticipantList(any(), anyLong());

                // then
                mockMvc.perform(get("/api/v1/group/participant/1")
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
            @DisplayName("그룹 참여자 리스트 실패 - 이미 삭제된 그룹")
            public void groupParticipantListFailAlreadyDeleteGroup() throws Exception {
                // given

                // when
                doThrow(new GroupException(GROUP_ALREADY_DELETE_GROUP)).when(groupService)
                        .groupParticipantList(any(), anyLong());

                // then
                mockMvc.perform(get("/api/v1/group/participant/1")
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
            @DisplayName("그룹 참여자 리스트 실패 - 이미 탈퇴한 유저")
            public void groupParticipantListFailAlreadyWithdrawGroup() throws Exception {
                // given

                // when
                doThrow(new GroupException(GROUP_ALREADY_WITHDRAW)).when(groupService)
                        .groupParticipantList(any(), anyLong());

                // then
                mockMvc.perform(get("/api/v1/group/participant/1")
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
    }
}
