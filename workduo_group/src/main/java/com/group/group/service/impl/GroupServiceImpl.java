package com.group.group.service.impl;

import com.core.domain.area.siggArea.SiggArea;
import com.core.domain.area.siggArea.repository.SiggAreaRepository;
import com.core.domain.common.CommonRequestContext;
import com.core.domain.group.dto.GroupDto;
import com.core.domain.group.dto.GroupParticipantsDto;
import com.core.domain.group.dto.UpdateGroup;
import com.core.domain.group.entity.Group;
import com.core.domain.group.entity.GroupCreateMember;
import com.core.domain.group.entity.GroupJoinMember;
import com.core.domain.group.entity.GroupLike;
import com.core.domain.group.type.GroupJoinMemberStatus;
import com.core.domain.group.type.GroupRole;
import com.core.domain.group.type.GroupStatus;
import com.core.domain.member.entity.Member;
import com.core.domain.member.repository.MemberRepository;
import com.core.domain.memberCalendar.repository.MemberCalendarRepository;
import com.core.domain.sport.sport.Sport;
import com.core.domain.sport.sport.repository.SportRepository;
import com.core.error.group.exception.GroupException;
import com.core.error.group.type.GroupErrorCode;
import com.core.error.member.exception.MemberException;
import com.core.error.member.type.MemberErrorCode;
import com.core.util.AwsS3Provider;
import com.group.group.dto.GroupThumbnail;
import com.group.group.dto.ListGroup;
import com.group.group.repository.GroupCreateMemberRepository;
import com.group.group.repository.GroupJoinMemberRepository;
import com.group.group.repository.GroupLikeRepository;
import com.group.group.repository.GroupRepository;
import com.group.group.repository.query.GroupQueryRepository;
import com.group.group.service.GroupService;
import com.group.groupmetting.repository.GroupMeetingParticipantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.group.group.dto.CreateGroup.Request;
import static org.springframework.util.StringUtils.hasText;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

    private final MemberRepository memberRepository;
    private final MemberCalendarRepository memberCalendarRepository;
    private final GroupRepository groupRepository;
    private final GroupQueryRepository groupQueryRepository;
    private final GroupCreateMemberRepository groupCreateMemberRepository;
    private final GroupJoinMemberRepository groupJoinMemberRepository;
    private final GroupMeetingParticipantRepository groupMeetingParticipantRepository;
    private final GroupLikeRepository groupLikeRepository;
    private final SportRepository sportRepository;
    private final SiggAreaRepository siggAreaRepository;
    private final CommonRequestContext context;
    private final AwsS3Provider awsS3Provider;
    private final EntityManager entityManager;

    /**
     * 그룹 생성
     * @param request
     */
    @Override
    @Transactional
    public void createGroup(Request request, List<MultipartFile> multipartFiles) {
        Member member = getMember(context.getMemberEmail());
        SiggArea siggArea = getSiggArea(request.getSgg());

        Sport sport = getSport(request.getSportId());

        createGroupValidate(member);

        Group group = Group.builder()
                .siggArea(siggArea)
                .sport(sport)
                .name(request.getName())
                .limitPerson(request.getLimitPerson())
                .introduce(request.getIntroduce())
                .groupStatus(GroupStatus.GROUP_STATUS_ING)
                .build();

        groupRepository.save(group);
        entityManager.flush();

        String path = generatePath(group.getId());
        List<String> files = awsS3Provider.uploadFile(multipartFiles, path);
        group.updateThumbnail(files.get(0));

        GroupJoinMember groupJoinMember = GroupJoinMember.builder()
                .member(member)
                .group(group)
                .groupJoinMemberStatus(GroupJoinMemberStatus.GROUP_JOIN_MEMBER_STATUS_ING)
                .groupRole(GroupRole.GROUP_ROLE_LEADER)
                .build();

        groupJoinMemberRepository.save(groupJoinMember);

        GroupCreateMember groupCreateMember = GroupCreateMember.builder()
                .group(group)
                .member(member)
                .build();

        groupCreateMemberRepository.save(groupCreateMember);
    }

    /**
     * 그룹 해지 - 그룹장만 가능
     * @param groupId
     */
    @Override
    @Transactional
    public void deleteGroup(Long groupId) {
        Member member = getMember(context.getMemberEmail());

        Group group = getGroup(groupId);

        boolean existsGroupLeader =
                groupCreateMemberRepository.existsByMemberAndGroup(member, group);

        if (!existsGroupLeader) {
            throw new GroupException(GroupErrorCode.GROUP_NOT_LEADER);
        }

        groupCreateMemberRepository.deleteByMemberAndGroup(member, group);
        groupJoinMemberRepository.updateGroupJoinMemberStatusCancel(group);
        groupMeetingParticipantRepository.deleteAllByGroup(group);
        memberCalendarRepository.updateMemberCalendarMeetingActiveStatusGroupCancel(group);
        group.updateGroupStatusCancel();
    }

    /**
     * 그룹 탈퇴 - 그룹장은 불가능
     * @param groupId
     */
    @Override
    @Transactional
    public void withdrawGroup(Long groupId) {
        Member member = getMember(context.getMemberEmail());
        Group group = getGroup(groupId);
        GroupJoinMember groupJoinMember = getGroupJoinMember(member, group);

        withdrawGroupValidate(member, group, groupJoinMember);

        groupMeetingParticipantRepository.deleteByGroupAndMember(group, member);
        memberCalendarRepository.updateMemberCalendarMemberAndGroupWithdraw(member, group);
        groupJoinMember.withdrawGroup();
    }

    /**
     * 그룹 상세
     * @param groupId
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public GroupDto groupDetail(Long groupId) {
//        getMember(context.getMemberEmail());

        Group group = getGroup(groupId);
        if (group.getGroupStatus() != GroupStatus.GROUP_STATUS_ING) {
            throw new GroupException(GroupErrorCode.GROUP_ALREADY_DELETE_GROUP);
        }

        return groupQueryRepository.findById(groupId)
                .orElseThrow(() -> new GroupException(GroupErrorCode.GROUP_NOT_FOUND));
    }

    /**
     * 그룹 리스트
     *
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public Page<GroupDto> groupList(Pageable pageable, ListGroup.Request condition) {
        String memberEmail = context.getMemberEmail();
        Long memberId = null;
        if (hasText(memberEmail)) {
            Member member = getMember(memberEmail);
            memberId = member.getId();
        }

        return groupQueryRepository.findByGroupList(pageable, memberId, condition);
    }

    /**
     * 그룹 좋아요
     * @param groupId
     */
    @Override
    @Transactional
    public void groupLike(Long groupId) {
        Member member = getMember(context.getMemberEmail());
        Group group = getGroup(groupId);

        groupLikeValidate(member, group);

        GroupLike groupLike = GroupLike.builder()
                .group(group)
                .member(member)
                .build();

        groupLikeRepository.save(groupLike);
    }

    /**
     * 그룹 좋아요 취소
     * @param groupId
     */
    @Override
    @Transactional
    public void groupUnLike(Long groupId) {
        Member member = getMember(context.getMemberEmail());
        Group group = getGroup(groupId);

        groupUnLikeValidate(member, group);

        groupLikeRepository.deleteByGroupAndMember(group, member);
    }

    /**
     * 그룹 참여
     * @param groupId
     */
    @Override
    @Transactional
    public void groupParticipant(Long groupId) {
        Member member = getMember(context.getMemberEmail());
        Group group = getGroup(groupId);

        groupJoinValidate(member, group);

        GroupJoinMember groupJoinMember = GroupJoinMember.builder()
                .group(group)
                .member(member)
                .groupJoinMemberStatus(GroupJoinMemberStatus.GROUP_JOIN_MEMBER_STATUS_ING)
                .groupRole(GroupRole.GROUP_ROLE_NORMAL)
                .build();

        groupJoinMemberRepository.save(groupJoinMember);
    }

    /**
     * 그룹 참여자 리스트
     * @param pageable
     * @param groupId
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public Page<GroupParticipantsDto> groupParticipantList(Pageable pageable, Long groupId) {
        Member member = getMember(context.getMemberEmail());
        Group group = getGroup(groupId);
        GroupJoinMember groupJoinMember = getGroupJoinMember(member, group);

        groupParticipantValidate(member, group, groupJoinMember);
        return groupQueryRepository.findByGroupParticipantList(pageable, groupId);
    }

    /**
     * 그룹 썸네일 수정
     * @param groupId
     * @param multipartFiles
     * @return
     */
    @Override
    @Transactional
    public GroupThumbnail groupThumbnailUpdate(Long groupId, List<MultipartFile> multipartFiles) {
        Member member = getMember(context.getMemberEmail());
        Group group = getGroup(groupId);

        boolean existsGroupLeader =
                groupCreateMemberRepository.existsByMemberAndGroup(member, group);

        if (!existsGroupLeader) {
            throw new GroupException(GroupErrorCode.GROUP_NOT_LEADER);
        }

        List<String> paths = new ArrayList<>(
                Arrays.asList(AwsS3Provider.parseAwsUrl(group.getThumbnailPath()))
        );

        awsS3Provider.deleteFile(paths);

        String path = generatePath(group.getId());
        List<String> files = awsS3Provider.uploadFile(multipartFiles, path);
        group.updateThumbnail(files.get(0));

        return GroupThumbnail.fromEntity(group);
    }

    @Override
    @Transactional
    public UpdateGroup.Response groupUpdate(Long groupId, UpdateGroup.Request request) {
        Member member = getMember(context.getMemberEmail());
        Group group = getGroup(groupId);

        boolean existsGroupLeader =
                groupCreateMemberRepository.existsByMemberAndGroup(member, group);

        if (!existsGroupLeader) {
            throw new GroupException(GroupErrorCode.GROUP_NOT_LEADER);
        }

        Integer countByGroup = groupJoinMemberRepository.countByGroup(group);
        group.groupUpdate(request, countByGroup);

        return UpdateGroup.Response.fromEntity(group);
    }

    private void groupParticipantValidate(Member member, Group group, GroupJoinMember groupJoinMember) {
        if (group.getGroupStatus() != GroupStatus.GROUP_STATUS_ING) {
            throw new GroupException(GroupErrorCode.GROUP_ALREADY_DELETE_GROUP);
        }

        boolean existsByMember = groupJoinMemberRepository.existsByGroupAndMember(group, member);
        if (!existsByMember) {
            throw new GroupException(GroupErrorCode.GROUP_NOT_FOUND_USER);
        }

        if (groupJoinMember.getGroupJoinMemberStatus() != GroupJoinMemberStatus.GROUP_JOIN_MEMBER_STATUS_ING) {
            throw new GroupException(GroupErrorCode.GROUP_ALREADY_WITHDRAW);
        }
    }

    private void groupJoinValidate(Member member, Group group) {

        boolean exists = groupJoinMemberRepository.existsByGroupAndMember(group, member);
        if (exists) {
            throw new GroupException(GroupErrorCode.GROUP_ALREADY_PARTICIPANT) ;
        }

        if (group.getGroupStatus() != GroupStatus.GROUP_STATUS_ING) {
            throw new GroupException(GroupErrorCode.GROUP_ALREADY_DELETE_GROUP);
        }

        Integer groupParticipants = groupJoinMemberRepository.countByGroup(group);
        if (groupParticipants >= group.getLimitPerson()) {
            throw new GroupException(GroupErrorCode.GROUP_MAXIMUM_PARTICIPANT);
        }
    }

    private void createGroupValidate(Member member) {

        Long groupCreateMemberCount = groupCreateMemberCount(member);
        if (groupCreateMemberCount >= 3) {
            throw new GroupException(GroupErrorCode.GROUP_MAXIMUM_EXCEEDED);
        }
    }

    private void withdrawGroupValidate(Member member, Group group, GroupJoinMember groupJoinMember) {

        if (group.getGroupStatus() != GroupStatus.GROUP_STATUS_ING) {
            throw new GroupException(GroupErrorCode.GROUP_ALREADY_DELETE_GROUP);
        }

        boolean existsGroupLeader =
                groupCreateMemberRepository.existsByMemberAndGroup
                        (member, group);

        if (existsGroupLeader) {
            throw new GroupException(GroupErrorCode.GROUP_LEADER_NOT_WITHDRAW);
        }

        if (groupJoinMember.getGroupJoinMemberStatus() != GroupJoinMemberStatus.GROUP_JOIN_MEMBER_STATUS_ING) {
            throw new GroupException(GroupErrorCode.GROUP_ALREADY_WITHDRAW);
        }

    }

    private void groupLikeValidate(Member member, Group group) {

        boolean exists = groupLikeRepository.existsByGroupAndMember(group, member);
        if (exists) {
            throw new GroupException(GroupErrorCode.GROUP_ALREADY_LIKE);
        }

        if (group.getGroupStatus() != GroupStatus.GROUP_STATUS_ING) {
            throw new GroupException(GroupErrorCode.GROUP_ALREADY_DELETE_GROUP);
        }

        boolean existsByMember = groupJoinMemberRepository.existsByGroupAndMember(group, member);
        if (!existsByMember ) {
            throw new GroupException(GroupErrorCode.GROUP_NOT_FOUND_USER);
        }

        GroupJoinMember groupJoinMember = getGroupJoinMember(member, group);
        if (groupJoinMember.getGroupJoinMemberStatus() != GroupJoinMemberStatus.GROUP_JOIN_MEMBER_STATUS_ING) {
            throw new GroupException(GroupErrorCode.GROUP_ALREADY_WITHDRAW);
        }
    }

    private void groupUnLikeValidate(Member member, Group group) {

        if (group.getGroupStatus() != GroupStatus.GROUP_STATUS_ING) {
            throw new GroupException(GroupErrorCode.GROUP_ALREADY_DELETE_GROUP);
        }

        boolean existsByMember = groupJoinMemberRepository.existsByGroupAndMember(group, member);
        if (!existsByMember ) {
            throw new GroupException(GroupErrorCode.GROUP_NOT_FOUND_USER);
        }

        GroupJoinMember groupJoinMember = getGroupJoinMember(member, group);
        if (groupJoinMember.getGroupJoinMemberStatus() != GroupJoinMemberStatus.GROUP_JOIN_MEMBER_STATUS_ING) {
            throw new GroupException(GroupErrorCode.GROUP_ALREADY_WITHDRAW);
        }
    }

    private GroupJoinMember getGroupJoinMember(Member member, Group group) {
        return groupJoinMemberRepository.findByMemberAndGroup(member, group)
                .orElseThrow(() -> new GroupException(GroupErrorCode.GROUP_NOT_FOUND_USER));
    }

    private Member getMember(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_EMAIL_ERROR));
    }

    private Group getGroup(Long groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupException(GroupErrorCode.GROUP_NOT_FOUND));
    }

    private Long groupCreateMemberCount(Member member) {
        return groupCreateMemberRepository.countByMember(member);
    }

    private Sport getSport(Integer id) {
        return sportRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("해당 운동은 없는 운동입니다."));
    }

    private SiggArea getSiggArea(String sgg) {
        return siggAreaRepository.findBySgg(sgg)
                .orElseThrow(() -> new IllegalStateException("해당 지역은 없는 지역입니다."));
    }

    public String generatePath(Long groupId) {
        return "com/group/" + groupId + "/";
    }
}
