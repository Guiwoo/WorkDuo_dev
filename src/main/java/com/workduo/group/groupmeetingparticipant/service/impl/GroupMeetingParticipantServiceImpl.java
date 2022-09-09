package com.workduo.group.groupmeetingparticipant.service.impl;

import com.workduo.configuration.aop.groupmeeting.GroupMeetingLock;
import com.workduo.error.member.exception.MemberException;
import com.workduo.error.member.type.MemberErrorCode;
import com.workduo.group.group.entity.Group;
import com.workduo.group.group.repository.GroupRepository;
import com.workduo.group.groupmeetingparticipant.dto.createGroupMeetingParticipant.CreateParticipant;
import com.workduo.group.groupmeetingparticipant.dto.createGroupMeetingParticipant.CreateParticipantDto;
import com.workduo.group.groupmeetingparticipant.entity.GroupMeetingParticipant;
import com.workduo.group.groupmeetingparticipant.repository.GroupMeetingParticipantRepository;
import com.workduo.group.groupmeetingparticipant.service.GroupMeetingParticipantService;
import com.workduo.group.groupmetting.entity.GroupMeeting;
import com.workduo.group.groupmetting.repository.GroupMeetingRepository;
import com.workduo.member.member.entity.Member;
import com.workduo.member.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.workduo.error.member.type.MemberErrorCode.MEMBER_EMAIL_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupMeetingParticipantServiceImpl implements GroupMeetingParticipantService {

    private final GroupMeetingParticipantRepository groupMeetingParticipantRepository;
    private final MemberRepository memberRepository;
    private final GroupMeetingRepository groupMeetingRepository;
    private final GroupRepository groupRepository;

    @Override
    @Transactional
    public CreateParticipantDto meetingParticipant(CreateParticipant.Request request, Long memberId) {
        Member findMember = getMember(memberId);
        GroupMeeting findGroupMeeting = getGroupMeeting(request.getGroupMeetingId());

        validateGroupMeetingParticipant(findMember, findGroupMeeting);

        GroupMeetingParticipant groupMeetingParticipant = GroupMeetingParticipant.builder()
                .member(findMember)
                .groupMeeting(findGroupMeeting)
                .build();

        groupMeetingParticipantRepository.save(groupMeetingParticipant);

        return CreateParticipantDto.fromEntity(groupMeetingParticipant, findGroupMeeting);
    }
    
    private void validateGroupMeetingParticipant(Member member, GroupMeeting groupMeeting) {
        boolean exists =
                groupMeetingParticipantRepository.existsByMemberAndGroupMeeting(member, groupMeeting);
        if (exists) {
            throw new IllegalStateException("이미 참가한 모임입니다.");
        }

        int maxParticipants = groupMeeting.getMaxParticipant();
        Integer numberOfParticipants = groupMeetingParticipantRepository.countByGroupMeeting(groupMeeting);
        if (maxParticipants - numberOfParticipants <= 0) {
            throw new IllegalStateException("참가인원이 꽉차 마감되었습니다.");
        }
    }

    private GroupMeeting getGroupMeeting(Long groupMeetingId) {
        return groupMeetingRepository.findById(groupMeetingId)
                .orElseThrow(() -> new IllegalStateException("meeting not found"));
    }

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MEMBER_EMAIL_ERROR));
    }

    private Group getGroup(Long groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalStateException("group not found"));
    }
}
