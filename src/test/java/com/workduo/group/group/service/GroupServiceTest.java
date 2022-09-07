package com.workduo.group.group.service;

import com.workduo.area.sidoarea.entity.SidoArea;
import com.workduo.area.siggarea.dto.siggarea.SiggAreaDto;
import com.workduo.area.siggarea.entity.SiggArea;
import com.workduo.area.siggarea.repository.SiggAreaRepository;
import com.workduo.common.CommonRequestContext;
import com.workduo.configuration.jpa.JpaAuditingConfiguration;
import com.workduo.error.group.exception.GroupException;
import com.workduo.group.group.dto.CreateGroup;
import com.workduo.group.group.dto.GroupDto;
import com.workduo.group.group.entity.Group;
import com.workduo.group.group.repository.GroupLikeRepository;
import com.workduo.group.group.repository.query.GroupQueryRepository;
import com.workduo.group.group.repository.GroupRepository;
import com.workduo.group.group.service.impl.GroupServiceImpl;
import com.workduo.group.group.type.GroupStatus;
import com.workduo.group.groupcreatemember.entity.GroupCreateMember;
import com.workduo.group.groupcreatemember.repository.GroupCreateMemberRepository;
import com.workduo.group.group.entity.GroupJoinMember;
import com.workduo.group.group.repository.GroupJoinMemberRepository;
import com.workduo.group.groupmeetingparticipant.repository.GroupMeetingParticipantRepository;
import com.workduo.member.member.entity.Member;
import com.workduo.member.member.repository.MemberRepository;
import com.workduo.member.membercalendar.repository.MemberCalendarRepository;
import com.workduo.sport.sport.dto.SportDto;
import com.workduo.sport.sport.entity.Sport;
import com.workduo.sport.sport.repository.SportRepository;
import com.workduo.sport.sportcategory.entity.SportCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.workduo.error.group.type.GroupErrorCode.*;
import static com.workduo.group.group.type.GroupStatus.GROUP_STATUS_CANCEL;
import static com.workduo.group.group.type.GroupJoinMemberStatus.GROUP_JOIN_MEMBER_STATUS_ING;
import static com.workduo.group.group.type.GroupJoinMemberStatus.GROUP_JOIN_MEMBER_STATUS_WITHDRAW;
import static com.workduo.group.group.type.GroupRole.GROUP_ROLE_LEADER;
import static com.workduo.group.group.type.GroupRole.GROUP_ROLE_NORMAL;
import static com.workduo.member.member.type.MemberStatus.MEMBER_STATUS_ING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@Import({JpaAuditingConfiguration.class})
@ExtendWith(MockitoExtension.class)
public class GroupServiceTest {

    @Mock
    private MemberRepository memberRepository;
    @Mock
    private GroupRepository groupRepository;
    @Mock
    private GroupCreateMemberRepository groupCreateMemberRepository;
    @Mock
    private SportRepository sportRepository;
    @Mock
    private SiggAreaRepository siggAreaRepository;
    @Mock
    private CommonRequestContext context;
    @Mock
    private GroupJoinMemberRepository groupJoinMemberRepository;
    @Mock
    private GroupMeetingParticipantRepository groupMeetingParticipantRepository;
    @Mock
    private MemberCalendarRepository memberCalendarRepository;
    @Mock
    private GroupQueryRepository groupQueryRepository;
    @Mock
    private GroupLikeRepository groupLikeRepository;

    @Spy
    @InjectMocks
    private GroupServiceImpl groupService;

    Member member;
    Sport sport;
    SiggArea siggArea;
    Group group;
    GroupDto groupDto;
    GroupCreateMember groupCreateMember;
    GroupJoinMember normal;
    GroupJoinMember leader;
    GroupJoinMember alreadyWithdrawMember;
    Group deletedGroup;
    
    @BeforeEach
    public void init() {
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
                .id(1)
                .amdCd("1000")
                .cityId(1000)
                .sggnm("수정구 신흥동")
                .sidoArea(SidoArea.builder()
                        .id(1)
                        .admCd("100")
                        .sidonm("경기도 성남시")
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

        groupDto = GroupDto.builder()
                .groupId(1L)
                .thumbnailPath("test")
                .introduce("test")
                .name("test")
                .siggArea(SiggAreaDto.builder()
                        .id(1)
                        .cityId(1)
                        .amdCd("test")
                        .sggnm("test")
                        .build())
                .limitPerson(10)
                .sport(SportDto.builder().build())
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

        groupCreateMember = GroupCreateMember.builder()
                .member(member)
                .group(group)
                .build();

        normal = GroupJoinMember.builder()
                .member(member)
                .group(group)
                .groupJoinMemberStatus(GROUP_JOIN_MEMBER_STATUS_ING)
                .groupRole(GROUP_ROLE_NORMAL)
                .id(1L)
                .build();

        leader = GroupJoinMember.builder()
                .member(member)
                .group(group)
                .groupJoinMemberStatus(GROUP_JOIN_MEMBER_STATUS_ING)
                .groupRole(GROUP_ROLE_LEADER)
                .id(2L)
                .build();

        alreadyWithdrawMember = GroupJoinMember.builder()
                .member(member)
                .group(group)
                .groupJoinMemberStatus(GROUP_JOIN_MEMBER_STATUS_WITHDRAW)
                .groupRole(GROUP_ROLE_LEADER)
                .id(3L)
                .build();
    }

    @Test
    @DisplayName("success create group")
    @Transactional
    public void createGroup() throws Exception {
        // given
        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.of(member));
        given(sportRepository.findById(anyInt()))
                .willReturn(Optional.of(sport));
        given(siggAreaRepository.findById(anyInt()))
                .willReturn(Optional.of(siggArea));

        doReturn("rbsks147@naver.com").when(context).getMemberEmail();
        given(groupRepository.save(any()))
                .willReturn(Group.builder()
                        .id(1L)
                        .sport(sport)
                        .build());

        // when
        CreateGroup.Request request = CreateGroup.Request.builder()
                .name("수정구 풋살 모임")
                .limitPerson(30)
                .sportId(1)
                .siggAreaId(1)
                .introduce("경기도 성남시 수정구 아재들의 풋살, 축구 모임입니다. 많은 참여 부탁드립니다.")
                .thumbnailPath("https://블라블라~~~.com")
                .build();

        ArgumentCaptor<Group> groupArgumentCaptor =
                ArgumentCaptor.forClass(Group.class);

        groupService.createGroup(request);

        // then
        verify(groupRepository, times(1))
                .save(groupArgumentCaptor.capture());
    }

    @Test
    @DisplayName("create group not found user")
    public void creatGroupFailNotFoundUser() throws Exception {
        // given
        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.empty());
        doReturn("").when(context).getMemberEmail();
        CreateGroup.Request request = CreateGroup.Request.builder()
                .name("수정구 풋살 모임")
                .limitPerson(30)
                .sportId(1)
                .siggAreaId(1)
                .introduce("경기도 성남시 수정구 아재들의 풋살, 축구 모임입니다. 많은 참여 부탁드립니다.")
                .thumbnailPath("https://블라블라~~~.com")
                .build();

        // when
        IllegalStateException userNotFoundException =
                assertThrows(IllegalStateException.class,
                        () -> groupService.createGroup(request));

        // then
        System.out.println(userNotFoundException.getMessage());
        assertEquals(userNotFoundException.getMessage(), "user not found");
    }

    @Test
    @DisplayName("create group create maximum exceeded")
    public void creatGroupFailGroupCreateMaximumExceeded() throws Exception {
        // given
        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.of(member));
        doReturn("rbsks147@naver.com").when(context).getMemberEmail();

        given(sportRepository.findById(anyInt()))
                .willReturn(Optional.of(sport));
        given(siggAreaRepository.findById(anyInt()))
                .willReturn(Optional.of(siggArea));

        given(groupCreateMemberRepository.countByMember(any()))
                .willReturn(3L);

        CreateGroup.Request request = CreateGroup.Request.builder()
                .name("수정구 풋살 모임")
                .limitPerson(30)
                .sportId(1)
                .siggAreaId(1)
                .introduce("경기도 성남시 수정구 아재들의 풋살, 축구 모임입니다. 많은 참여 부탁드립니다.")
                .thumbnailPath("https://블라블라~~~.com")
                .build();

        // when
        GroupException groupException =
                assertThrows(GroupException.class,
                        () -> groupService.createGroup(request));

        // then
        assertEquals(groupException.getErrorCode(), GROUP_MAXIMUM_EXCEEDED);
    }

    @Test
    @DisplayName("create group create not found sigg area")
    public void creatGroupFailNotFoundSiggArea() throws Exception {
        // given
        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.of(member));
        doReturn("rbsks147@naver.com").when(context).getMemberEmail();

        given(siggAreaRepository.findById(anyInt()))
                .willReturn(Optional.empty());

        CreateGroup.Request request = CreateGroup.Request.builder()
                .name("수정구 풋살 모임")
                .limitPerson(30)
                .sportId(1)
                .siggAreaId(1)
                .introduce("경기도 성남시 수정구 아재들의 풋살, 축구 모임입니다. 많은 참여 부탁드립니다.")
                .thumbnailPath("https://블라블라~~~.com")
                .build();

        // when
        IllegalStateException groupException =
                assertThrows(IllegalStateException.class,
                        () -> groupService.createGroup(request));

        // then
        assertEquals(groupException.getMessage(), "해당 지역은 없는 지역입니다.");
    }

    @Test
    @DisplayName("create group create not found sport")
    public void creatGroupFailNotFoundSport() throws Exception {
        // given
        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.of(member));
        doReturn("rbsks147@naver.com").when(context).getMemberEmail();

        given(siggAreaRepository.findById(anyInt()))
                .willReturn(Optional.of(siggArea));
        given(sportRepository.findById(anyInt()))
                .willReturn(Optional.empty());

        CreateGroup.Request request = CreateGroup.Request.builder()
                .name("수정구 풋살 모임")
                .limitPerson(30)
                .sportId(1)
                .siggAreaId(1)
                .introduce("경기도 성남시 수정구 아재들의 풋살, 축구 모임입니다. 많은 참여 부탁드립니다.")
                .thumbnailPath("https://블라블라~~~.com")
                .build();

        // when
        IllegalStateException groupException =
                assertThrows(IllegalStateException.class,
                        () -> groupService.createGroup(request));

        // then
        assertEquals(groupException.getMessage(), "해당 운동은 없는 운동입니다.");
    }

    @Test
    @DisplayName("success delete group")
    public void deleteGroup() throws Exception {
        // given
        doReturn(Optional.of(member)).when(memberRepository).findByEmail(anyString());
        doReturn("rbsks147@naver.com").when(context).getMemberEmail();
        doReturn(Optional.of(group)).when(groupRepository).findById(anyLong());
        doReturn(true).when(groupCreateMemberRepository)
                .existsByMemberAndGroup(member, group);
        doNothing().when(groupCreateMemberRepository)
                .deleteByMemberAndGroup(member, group);
        doNothing().when(groupJoinMemberRepository)
                .updateGroupJoinMemberStatusCancel(group);
        doNothing().when(groupMeetingParticipantRepository)
                .deleteByGroup(group);
        doNothing().when(memberCalendarRepository).
                updateMemberCalendarMeetingActiveStatusGroupCancel(group);

        // when
        groupService.deleteGroup(1L);

        // then
        verify(memberRepository, times(1))
                .findByEmail(anyString());

        verify(groupRepository, times(1))
                .findById(anyLong());

        verify(groupCreateMemberRepository, times(1))
                .existsByMemberAndGroup(member, group);

        verify(groupCreateMemberRepository, times(1))
                .deleteByMemberAndGroup(member, group);

        verify(groupJoinMemberRepository, times(1))
                .updateGroupJoinMemberStatusCancel(group);

        verify(groupMeetingParticipantRepository, times(1))
                .deleteByGroup(group);

        verify(memberCalendarRepository, times(1))
                .updateMemberCalendarMeetingActiveStatusGroupCancel(group);

    }

    @Test
    @DisplayName("delete group create not found user")
    public void deleteGroupFailNotFoundUser() throws Exception {
        // given
        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.empty());
        doReturn("").when(context).getMemberEmail();

        // when
        IllegalStateException groupException =
                assertThrows(IllegalStateException.class,
                () -> groupService.deleteGroup(1L));

        // then
        assertEquals(groupException.getMessage(), "user not found");
    }

    @Test
    @DisplayName("delete group create not found group")
    public void deleteGroupFailNotFoundGroup() throws Exception {
        // given
        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.of(member));
        doReturn("rbsks147@naver.com").when(context).getMemberEmail();

        given(groupRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        // when
        GroupException groupException =
                assertThrows(GroupException.class,
                        () -> groupService.deleteGroup(1L));

        // then
        assertEquals(groupException.getErrorCode(), GROUP_NOT_FOUND);
    }

    @Test
    @DisplayName("delete group create not leader")
    public void deleteGroupFailNotLeader() throws Exception {
        // given
        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.of(member));
        doReturn("rbsks147@naver.com").when(context).getMemberEmail();

        given(groupRepository.findById(anyLong()))
                .willReturn(Optional.of(group));

        given(groupCreateMemberRepository.existsByMemberAndGroup(any(), any()))
                .willReturn(false);

        // when
        GroupException groupException =
                assertThrows(GroupException.class,
                        () -> groupService.deleteGroup(1L));

        // then
        assertEquals(groupException.getErrorCode(), GROUP_NOT_LEADER);
    }

    @Test
    @DisplayName("success withdraw group")
    public void withdrawGroup() throws Exception {
        // given
        doReturn(Optional.of(member)).when(memberRepository).findByEmail(anyString());
        doReturn("rbsks147@naver.com").when(context).getMemberEmail();
        doReturn(Optional.of(group)).when(groupRepository).findById(anyLong());
        doReturn(Optional.of(normal)).when(groupJoinMemberRepository).findByMember(any());
        doReturn(false).when(groupCreateMemberRepository)
                .existsByMemberAndGroup(member, group);
        doNothing().when(groupMeetingParticipantRepository)
                .deleteByMember(member);
        doNothing().when(memberCalendarRepository).
                updateMemberCalendarMemberWithdraw(member);

        // when
        groupService.withdrawGroup(1L);

        // then
        verify(memberRepository, times(1))
                .findByEmail(anyString());

        verify(groupRepository, times(1))
                .findById(anyLong());

        verify(groupJoinMemberRepository, times(1))
                .findByMember(any());

        verify(groupCreateMemberRepository, times(1))
                .existsByMemberAndGroup(member, group);

        verify(groupMeetingParticipantRepository, times(1))
                .deleteByMember(member);

        verify(memberCalendarRepository, times(1))
                .updateMemberCalendarMemberWithdraw(member);
    }

    @Test
    @DisplayName("withdraw group not found user")
    public void withdrawGroupFailNotFoundUser() throws Exception {
        // given
        doReturn(Optional.empty()).when(memberRepository).findByEmail(anyString());
        doReturn("").when(context).getMemberEmail();


        // when
        IllegalStateException groupException =
                assertThrows(IllegalStateException.class,
                        () -> groupService.withdrawGroup(1L));

        // then
        assertEquals(groupException.getMessage(), "user not found");
    }

    @Test
    @DisplayName("withdraw group not found group")
    public void withdrawGroupFailNotFoundGroup() throws Exception {
        // given
        doReturn(Optional.of(member)).when(memberRepository).findByEmail(anyString());
        doReturn("rbsks147@naver.com").when(context).getMemberEmail();
        doReturn(Optional.empty()).when(groupRepository).findById(anyLong());

        // when
        GroupException groupException =
                assertThrows(GroupException.class,
                        () -> groupService.withdrawGroup(1L));

        // then
        assertEquals(groupException.getErrorCode(), GROUP_NOT_FOUND);
    }

    @Test
    @DisplayName("withdraw group group not found group")
    public void withdrawGroupFailGroupNotFoundGroup() throws Exception {
        // given
        doReturn(Optional.of(member)).when(memberRepository).findByEmail(anyString());
        doReturn("rbsks147@naver.com").when(context).getMemberEmail();
        doReturn(Optional.of(group)).when(groupRepository).findById(anyLong());
        doReturn(Optional.empty()).when(groupJoinMemberRepository)
                .findByMember(any());

        // when
        GroupException groupException =
                assertThrows(GroupException.class,
                        () -> groupService.withdrawGroup(1L));

        // then
        assertEquals(groupException.getErrorCode(), GROUP_NOT_FOUND_USER);
    }

    @Test
    @DisplayName("withdraw group leader not withdraw")
    public void withdrawGroupFailLeaderNotWithdraw() throws Exception {
        // given
        doReturn(Optional.of(member)).when(memberRepository).findByEmail(anyString());
        doReturn("rbsks147@naver.com").when(context).getMemberEmail();
        doReturn(Optional.of(group)).when(groupRepository).findById(anyLong());
        doReturn(Optional.of(leader)).when(groupJoinMemberRepository)
                .findByMember(any());
        doReturn(true).when(groupCreateMemberRepository)
                .existsByMemberAndGroup(any(), any());

        // when
        GroupException groupException =
                assertThrows(GroupException.class,
                        () -> groupService.withdrawGroup(1L));

        // then
        assertEquals(groupException.getErrorCode(), GROUP_LEADER_NOT_WITHDRAW);
    }

    @Test
    @DisplayName("withdraw group already withdraw")
    public void withdrawGroupFailAlreadyWithdraw() throws Exception {
        // given
        doReturn(Optional.of(member)).when(memberRepository).findByEmail(anyString());
        doReturn("rbsks147@naver.com").when(context).getMemberEmail();
        doReturn(Optional.of(group)).when(groupRepository).findById(anyLong());
        doReturn(Optional.of(alreadyWithdrawMember)).when(groupJoinMemberRepository)
                .findByMember(any());
        doReturn(false).when(groupCreateMemberRepository)
                .existsByMemberAndGroup(any(), any());

        // when
        GroupException groupException =
                assertThrows(GroupException.class,
                        () -> groupService.withdrawGroup(1L));

        // then
        assertEquals(groupException.getErrorCode(), GROUP_ALREADY_WITHDRAW);
    }

    @Test
    @DisplayName("그룹 상세 보기 성공")
    public void groupDetail() throws Exception {
        // given
//        doReturn(Optional.of(member)).when(memberRepository)
//                .findByEmail(anyString());
//        doReturn("rbsks147@naver.com").when(context)
//                .getMemberEmail();
        doReturn(Optional.of(groupDto)).when(groupQueryRepository)
                .findById(anyLong());

        // when
        GroupDto groupDto = groupService.groupDetail(1L);

        // then
        assertEquals(groupDto.getGroupId(), group.getId());
    }

//    @Test
//    @DisplayName("그룹 상세 보기 실패 - 해당 유저 없음")
//    public void groupDetailFailNotFoundUser() throws Exception {
//        // given
//        doReturn(Optional.empty()).when(memberRepository)
//                .findByEmail(anyString());
//        doReturn("").when(context)
//                .getMemberEmail();
//
//        // when
//        IllegalStateException groupException =
//                assertThrows(IllegalStateException.class,
//                        () -> groupService.groupDetail(1L));
//
//        // then
//        assertEquals(groupException.getMessage(), "user not found");
//    }

    @Test
    @DisplayName("그룹 상세 보기 실패 - 해당 그룹 없음")
    public void groupDetailFailNotFoundGroup() throws Exception {
        // given
//        doReturn(Optional.of(member)).when(memberRepository)
//                .findByEmail(anyString());
//        doReturn("rbsks147@naver.com").when(context)
//                .getMemberEmail();
        doReturn(Optional.empty()).when(groupQueryRepository)
                .findById(anyLong());

        // when
        GroupException groupException =
                assertThrows(GroupException.class,
                        () -> groupService.groupDetail(1L));

        // then
        assertEquals(groupException.getErrorCode(), GROUP_NOT_FOUND);
    }

    @Test
    @DisplayName("success group like")
    public void groupLike() throws Exception {
        // given
        doReturn(Optional.of(member)).when(memberRepository)
                .findByEmail(anyString());
        doReturn("rbsks147@naver.com").when(context)
                .getMemberEmail();
        doReturn(Optional.of(group)).when(groupRepository)
                .findById(anyLong());
        doReturn(true).when(groupJoinMemberRepository)
                .existsByMember(any());
        doReturn(Optional.of(normal)).when(groupJoinMemberRepository)
                .findByMember(member);

        // when
        groupService.groupLike(1L);

        // then
        verify(memberRepository, times(1))
                .findByEmail(any());

        verify(groupRepository, times(1))
                .findById(anyLong());

        verify(groupJoinMemberRepository, times(1))
                .existsByMember(any());

        verify(groupJoinMemberRepository, times(1))
                .findByMember(any());
    }

    @Test
    @DisplayName("group like not found user")
    public void groupLikeFailNotFoundUser() throws Exception {
        // given
        doReturn(Optional.empty()).when(memberRepository)
                .findByEmail(anyString());
        doReturn("").when(context)
                .getMemberEmail();

        // when
        IllegalStateException groupException =
                assertThrows(IllegalStateException.class,
                        () -> groupService.groupUnLike(1L));

        // then
        assertEquals(groupException.getMessage(), "user not found");
    }

    @Test
    @DisplayName("group like not found group")
    public void groupLikeFailNotFoundGroup() throws Exception {
        // given
        doReturn(Optional.of(member)).when(memberRepository)
                .findByEmail(anyString());
        doReturn("rbsks147@naver.com").when(context)
                .getMemberEmail();
        doReturn(Optional.empty()).when(groupRepository)
                .findById(anyLong());

        // when
        GroupException groupException =
                assertThrows(GroupException.class,
                        () -> groupService.groupUnLike(1L));

        // then
        assertEquals(groupException.getErrorCode(),  GROUP_NOT_FOUND);
    }

    @Test
    @DisplayName("group like already delete group")
    public void groupLikeFailAlreadyDeleteGroup() throws Exception {
        // given
        doReturn(Optional.of(member)).when(memberRepository)
                .findByEmail(anyString());
        doReturn("rbsks147@naver.com").when(context)
                .getMemberEmail();
        doReturn(Optional.of(deletedGroup)).when(groupRepository)
                .findById(anyLong());

        // when
        GroupException groupException =
                assertThrows(GroupException.class,
                        () -> groupService.groupUnLike(1L));

        // then
        assertEquals(groupException.getErrorCode(),  GROUP_ALREADY_DELETE_GROUP);
    }

    @Test
    @DisplayName("group like group not found user")
    public void groupLikeFailGroupNotFoundUser() throws Exception {
        // given
        doReturn(Optional.of(member)).when(memberRepository)
                .findByEmail(anyString());
        doReturn("rbsks147@naver.com").when(context)
                .getMemberEmail();
        doReturn(Optional.of(group)).when(groupRepository)
                .findById(anyLong());
        doReturn(false).when(groupJoinMemberRepository)
                .existsByMember(any());

        // when
        GroupException groupException =
                assertThrows(GroupException.class,
                        () -> groupService.groupUnLike(1L));

        // then
        assertEquals(groupException.getErrorCode(),  GROUP_NOT_FOUND_USER);
    }

    @Test
    @DisplayName("group like already withdraw group")
    public void groupLikeFailAlreadyWithdrawGroup() throws Exception {
        // given
        doReturn(Optional.of(member)).when(memberRepository)
                .findByEmail(anyString());
        doReturn("rbsks147@naver.com").when(context)
                .getMemberEmail();
        doReturn(Optional.of(group)).when(groupRepository)
                .findById(anyLong());
        doReturn(true).when(groupJoinMemberRepository)
                .existsByMember(any());
        doReturn(Optional.of(alreadyWithdrawMember)).when(groupJoinMemberRepository)
                .findByMember(any());

        // when
        GroupException groupException =
                assertThrows(GroupException.class,
                        () -> groupService.groupUnLike(1L));

        // then
        assertEquals(groupException.getErrorCode(),  GROUP_ALREADY_WITHDRAW);
    }
}
