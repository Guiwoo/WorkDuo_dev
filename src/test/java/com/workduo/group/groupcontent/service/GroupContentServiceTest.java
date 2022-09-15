package com.workduo.group.groupcontent.service;

import com.workduo.area.sidoarea.entity.SidoArea;
import com.workduo.area.siggarea.entity.SiggArea;
import com.workduo.common.CommonRequestContext;
import com.workduo.configuration.jpa.JpaAuditingConfiguration;
import com.workduo.error.group.exception.GroupException;
import com.workduo.error.member.exception.MemberException;
import com.workduo.group.gropcontent.dto.creategroupcontent.CreateGroupContent;
import com.workduo.group.gropcontent.entity.GroupContent;
import com.workduo.group.gropcontent.repository.GroupContentImageRepository;
import com.workduo.group.gropcontent.repository.GroupContentLikeRepository;
import com.workduo.group.gropcontent.repository.GroupContentRepository;
import com.workduo.group.gropcontent.service.impl.GroupContentServiceImpl;
import com.workduo.group.group.entity.Group;
import com.workduo.group.group.entity.GroupJoinMember;
import com.workduo.group.group.repository.GroupJoinMemberRepository;
import com.workduo.group.group.repository.GroupRepository;
import com.workduo.group.group.type.GroupStatus;
import com.workduo.member.member.entity.Member;
import com.workduo.member.member.repository.MemberRepository;
import com.workduo.sport.sport.entity.Sport;
import com.workduo.sport.sportcategory.entity.SportCategory;
import com.workduo.util.AwsS3Utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.workduo.error.group.type.GroupErrorCode.*;
import static com.workduo.error.member.type.MemberErrorCode.MEMBER_EMAIL_ERROR;
import static com.workduo.group.group.type.GroupJoinMemberStatus.GROUP_JOIN_MEMBER_STATUS_ING;
import static com.workduo.group.group.type.GroupJoinMemberStatus.GROUP_JOIN_MEMBER_STATUS_WITHDRAW;
import static com.workduo.group.group.type.GroupRole.GROUP_ROLE_LEADER;
import static com.workduo.group.group.type.GroupRole.GROUP_ROLE_NORMAL;
import static com.workduo.group.group.type.GroupStatus.GROUP_STATUS_CANCEL;
import static com.workduo.member.member.type.MemberStatus.MEMBER_STATUS_ING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@Import({JpaAuditingConfiguration.class})
@ExtendWith(MockitoExtension.class)
public class GroupContentServiceTest {

    @Mock
    private GroupContentRepository groupContentRepository;
    @Mock
    private GroupContentLikeRepository groupContentLikeRepository;
    @Mock
    private GroupJoinMemberRepository groupJoinMemberRepository;
    @Mock
    private GroupContentImageRepository groupContentImageRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private GroupRepository groupRepository;
    @Mock
    private CommonRequestContext context;
    @Mock
    private AwsS3Utils awsS3Utils;
    @Mock
    private EntityManager entityManager;

    @Spy
    @InjectMocks
    private GroupContentServiceImpl groupContentService;

    Member member;
    Sport sport;
    SiggArea siggArea;
    Group group;
    GroupJoinMember normal;
    Group deletedGroup;
    GroupJoinMember alreadyWithdrawMember;
    GroupContent groupContent;
    List<MultipartFile> image = new ArrayList<>();

    @BeforeEach
    public void init() {
        image.add(new MockMultipartFile(
                "multipartFiles",
                "imagefile.jpeg",
                "image/jpeg",
                "<<jpeg data>>".getBytes()
        ));

        member = Member.builder()
                .id(1L)
                .username("한규빈")
                .phoneNumber("01011111111")
                .nickname("규난")
                .password("1234")
                .email("rbsks147@naver.com")
                .memberStatus(MEMBER_STATUS_ING)
                .build();

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

        group = Group.builder()
                .id(1L)
                .groupStatus(GroupStatus.GROUP_STATUS_ING)
                .thumbnailPath("test")
                .introduce("test")
                .name("test")
                .siggArea(siggArea)
                .limitPerson(10)
                .sport(sport)
                .build();

        normal = GroupJoinMember.builder()
                .member(member)
                .group(group)
                .groupJoinMemberStatus(GROUP_JOIN_MEMBER_STATUS_ING)
                .groupRole(GROUP_ROLE_NORMAL)
                .id(1L)
                .build();

        deletedGroup = Group.builder()
                .id(1L)
                .groupStatus(GROUP_STATUS_CANCEL)
                .thumbnailPath("test")
                .introduce("test")
                .name("test")
                .siggArea(siggArea)
                .limitPerson(10)
                .sport(sport)
                .build();

        alreadyWithdrawMember = GroupJoinMember.builder()
                .member(member)
                .group(group)
                .groupJoinMemberStatus(GROUP_JOIN_MEMBER_STATUS_WITHDRAW)
                .groupRole(GROUP_ROLE_LEADER)
                .id(3L)
                .build();

        groupContent = GroupContent.builder()
                .title("test")
                .content("test")
                .group(group)
                .member(member)
                .deletedYn(false)
                .sortValue(0)
                .noticeYn(false)
                .build();
    }

    @Nested
    public class createGroupContent {

        @Test
        @DisplayName("그룹 피드 생성 성공")
        public void createGroupContent() throws Exception {
            // given
            doReturn("rbsks147@naver.com").when(context)
                    .getMemberEmail();
            doReturn(Optional.of(member)).when(memberRepository)
                    .findByEmail(anyString());
            doReturn(Optional.of(group)).when(groupRepository)
                    .findById(anyLong());
            doReturn(true).when(groupJoinMemberRepository)
                    .existsByGroupAndMember(any(), any());
            doReturn(Optional.of(normal)).when(groupJoinMemberRepository)
                    .findByMemberAndGroup(any(), any());
            doReturn(groupContent).when(groupContentRepository)
                    .save(any());
            groupContentService.generatePath(anyLong(), anyLong());
            doReturn(new ArrayList<>(List.of("test"))).when(awsS3Utils)
                    .uploadFile(any(), anyString());

            CreateGroupContent.Request request = CreateGroupContent.Request.builder()
                    .title("test")
                    .content("test")
                    .noticeYn(false)
                    .sortValue(0)
                    .build();

            // when
            groupContentService.createGroupContent(1L, request, image);

            // then
            verify(memberRepository, times(1))
                    .findByEmail(anyString());
            verify(groupRepository, times(1))
                    .findById(anyLong());
            verify(groupJoinMemberRepository, times(1))
                    .existsByGroupAndMember(any(), any());
            verify(groupJoinMemberRepository, times(1))
                    .findByMemberAndGroup(any(), any());
            verify(groupContentRepository, times(1))
                    .save(any());
            verify(groupContentImageRepository, times(1))
                    .saveAll(any());
        }

        @Test
        @DisplayName("그룹 피드 생성 실패 - 유저 정보 없음")
        public void createGroupContentFailNotFoundUser() throws Exception {
            // given
            doReturn("").when(context)
                    .getMemberEmail();
            doReturn(Optional.empty()).when(memberRepository)
                    .findByEmail(anyString());
            CreateGroupContent.Request request = CreateGroupContent.Request.builder()
                    .title("test")
                    .content("test")
                    .noticeYn(false)
                    .sortValue(0)
//                    .files(new ArrayList<>())
                    .build();

            // when
            MemberException groupContentException =
                    assertThrows(MemberException.class,
                            () -> groupContentService.createGroupContent(1L, request, image));

            // then
            assertEquals(groupContentException.getErrorMessage(), MEMBER_EMAIL_ERROR.getMessage());
        }

        @Test
        @DisplayName("그룹 피드 생성 실패 - 그룹 정보 없음")
        public void createGroupContentFailNotFoundGroup() throws Exception {
            // given
            doReturn("rbsks147@naver.com").when(context)
                    .getMemberEmail();
            doReturn(Optional.of(member)).when(memberRepository)
                    .findByEmail(anyString());
            doReturn(Optional.empty()).when(groupRepository)
                    .findById(anyLong());
            CreateGroupContent.Request request = CreateGroupContent.Request.builder()
                    .title("test")
                    .content("test")
                    .noticeYn(false)
                    .sortValue(0)
//                    .files(new ArrayList<>())
                    .build();

            // when
            GroupException groupContentException =
                    assertThrows(GroupException.class,
                            () -> groupContentService.createGroupContent(1L, request, image));

            // then
            assertEquals(groupContentException.getErrorMessage(), GROUP_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("그룹 피드 생성 실패 - 이미 삭제된 그룹")
        public void createGroupContentFailAlreadyDeltetGroup() throws Exception {
            // given
            doReturn("rbsks147@naver.com").when(context)
                    .getMemberEmail();
            doReturn(Optional.of(member)).when(memberRepository)
                    .findByEmail(anyString());
            doReturn(Optional.of(deletedGroup)).when(groupRepository)
                    .findById(anyLong());
            CreateGroupContent.Request request = CreateGroupContent.Request.builder()
                    .title("test")
                    .content("test")
                    .noticeYn(false)
                    .sortValue(0)
//                    .files(new ArrayList<>())
                    .build();

            // when
            GroupException groupContentException =
                    assertThrows(GroupException.class,
                            () -> groupContentService.createGroupContent(1L, request, image));

            // then
            assertEquals(groupContentException.getErrorMessage(), GROUP_ALREADY_DELETE_GROUP.getMessage());
        }

        @Test
        @DisplayName("그룹 피드 생성 실패 - 그룹에 속한 유저가 아닌 경우")
        public void createGroupContentFailGroupNotFoundUser() throws Exception {
            // given
            doReturn("rbsks147@naver.com").when(context)
                    .getMemberEmail();
            doReturn(Optional.of(member)).when(memberRepository)
                    .findByEmail(anyString());
            doReturn(Optional.of(group)).when(groupRepository)
                    .findById(anyLong());
            doReturn(false).when(groupJoinMemberRepository)
                    .existsByGroupAndMember(any(), any());

            CreateGroupContent.Request request = CreateGroupContent.Request.builder()
                    .title("test")
                    .content("test")
                    .noticeYn(false)
                    .sortValue(0)
//                    .files(new ArrayList<>())
                    .build();

            // when
            GroupException groupContentException =
                    assertThrows(GroupException.class,
                            () -> groupContentService.createGroupContent(1L, request, image));

            // then
            assertEquals(groupContentException.getErrorMessage(), GROUP_NOT_FOUND_USER.getMessage());
        }

        @Test
        @DisplayName("그룹 피드 생성 실패 - 그룹을 탈퇴한 경우")
        public void createGroupContentFailAlreadyWithdrawGroup() throws Exception {
            // given
            doReturn("rbsks147@naver.com").when(context)
                    .getMemberEmail();
            doReturn(Optional.of(member)).when(memberRepository)
                    .findByEmail(anyString());
            doReturn(Optional.of(group)).when(groupRepository)
                    .findById(anyLong());
            doReturn(true).when(groupJoinMemberRepository)
                    .existsByGroupAndMember(any(), any());
            doReturn(Optional.of(alreadyWithdrawMember)).when(groupJoinMemberRepository)
                    .findByMemberAndGroup(any(), any());

            CreateGroupContent.Request request = CreateGroupContent.Request.builder()
                    .title("test")
                    .content("test")
                    .noticeYn(false)
                    .sortValue(0)
//                    .files(new ArrayList<>())
                    .build();

            // when
            GroupException groupContentException =
                    assertThrows(GroupException.class,
                            () -> groupContentService.createGroupContent(1L, request, image));

            // then
            assertEquals(groupContentException.getErrorMessage(), GROUP_ALREADY_WITHDRAW.getMessage());
        }
    }
}
