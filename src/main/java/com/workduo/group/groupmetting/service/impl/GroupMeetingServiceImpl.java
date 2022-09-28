package com.workduo.group.groupmetting.service.impl;

import com.workduo.common.CommonRequestContext;
import com.workduo.error.group.exception.GroupException;
import com.workduo.error.member.exception.MemberException;
import com.workduo.group.group.entity.Group;
import com.workduo.group.group.entity.GroupJoinMember;
import com.workduo.group.group.repository.GroupJoinMemberRepository;
import com.workduo.group.group.repository.GroupRepository;
import com.workduo.group.groupmetting.dto.*;
import com.workduo.group.groupmetting.entity.GroupMeeting;
import com.workduo.group.groupmetting.entity.GroupMeetingParticipant;
import com.workduo.group.groupmetting.repository.GroupMeetingParticipantRepository;
import com.workduo.group.groupmetting.repository.GroupMeetingRepository;
import com.workduo.group.groupmetting.repository.query.GroupMeetingQueryRepository;
import com.workduo.group.groupmetting.service.GroupMeetingService;
import com.workduo.member.member.entity.Member;
import com.workduo.member.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.workduo.error.group.type.GroupErrorCode.*;
import static com.workduo.error.member.type.MemberErrorCode.MEMBER_EMAIL_ERROR;
import static com.workduo.group.group.type.GroupJoinMemberStatus.GROUP_JOIN_MEMBER_STATUS_ING;
import static com.workduo.group.group.type.GroupStatus.GROUP_STATUS_ING;

@Service
@RequiredArgsConstructor
public class GroupMeetingServiceImpl implements GroupMeetingService {

    private final MemberRepository memberRepository;
    private final GroupRepository groupRepository;
    private final GroupJoinMemberRepository groupJoinMemberRepository;
    private final GroupMeetingRepository groupMeetingRepository;
    private final GroupMeetingParticipantRepository groupMeetingParticipantRepository;
    private final GroupMeetingQueryRepository groupMeetingQueryRepository;
    private final CommonRequestContext context;

    @Override
    @Transactional(readOnly = true)
    public TimeDto meetingInquire(LocalDate startDate) {
        Member member = getMember(context.getMemberEmail());
        LocalDateTime startDateTime = parseTime(startDate, 0, 0, 0, 0);
        LocalDateTime endDateTime = parseTime(startDate, 23, 59, 59, 999999);

        List<MeetingInquireDto> meetingInquireDtos =
                groupMeetingQueryRepository.meetingInquireList(member.getId(), startDateTime, endDateTime);

        TimeDto timeDto = new TimeDto(
                startDateTime.getYear() + "-" +
                        (startDateTime.getMonth().getValue() < 10 ? "0" : "") +
                        startDateTime.getMonth().getValue() + "-" +
                        startDateTime.getDayOfMonth());

        List<Time> times = new ArrayList<>();
        while (startDateTime.isBefore(endDateTime)) {
            String time = (startDateTime.getHour() < 10 ? "0" : "") + startDateTime.getHour() + ":00";

            boolean timeCheck = false;
            for (MeetingInquireDto meetingInquireDto : meetingInquireDtos) {
                if (time.equals(meetingInquireDto.getTime())) {
                    timeCheck = true;
                    int cnt = meetingInquireDto.getTerm() / 60;
                    for (int i = 0; i < cnt; i++) {
                        times.add(new Time(time, true));
                        startDateTime = startDateTime.plusHours(1);
                        time = (startDateTime.getHour() < 10 ? "0" : "") + startDateTime.getHour() + ":00";
                    }
                    break;
                }
            }

            if (!timeCheck) {
                times.add(new Time(time, false));
                startDateTime = startDateTime.plusHours(1);
            }
        }

        timeDto.getTimes().addAll(times);

        return timeDto;
    }

    @Override
    @Transactional
    public void createMeeting(CreateMeeting.Request request, Long groupId) {
        Member member = getMember(context.getMemberEmail());
        Group group = getGroup(groupId);

        commonMeetingValidate(group, member);
        createMeetingValidate(member, request);

        GroupMeeting groupMeeting = GroupMeeting.builder()
                .group(group)
                .member(member)
                .title(request.getTitle())
                .content(request.getContent())
                .location(request.getLocation())
                .maxParticipant(request.getMaxParticipant())
                .meetingStartDate(request.getMeetingStartDate())
                .meetingEndDate(request.getMeetingEndDate().minusSeconds(1))
                .deletedYn(false)
                .build();
        groupMeetingRepository.save(groupMeeting);

        GroupMeetingParticipant meetingParticipant = GroupMeetingParticipant.builder()
                .group(group)
                .member(member)
                .groupMeeting(groupMeeting)
                .build();
        groupMeetingParticipantRepository.save(meetingParticipant);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MeetingDto> groupMeetingList(Pageable pageable, Long groupId) {
        Member member = getMember(context.getMemberEmail());
        Group group = getGroup(groupId);

        commonMeetingValidate(group, member);

        return groupMeetingQueryRepository.groupMeetingList(pageable, groupId);
    }

    @Override
    @Transactional(readOnly = true)
    public MeetingDto groupMeetingDetail(Long groupId, Long meetingId) {
        Member member = getMember(context.getMemberEmail());
        Group group = getGroup(groupId);

        commonMeetingValidate(group, member);

        return getGroupMeeting(meetingId, groupId, member.getId());
    }

    @Override
    @Transactional
    public void groupMeetingUpdate(Long groupId, Long meetingId, UpdateMeeting.Request request) {
        Member member = getMember(context.getMemberEmail());
        Group group = getGroup(groupId);

        commonMeetingValidate(group, member);

        GroupMeeting authorGroupMeeting = getAuthorGroupMeeting(meetingId, group, member);
        Integer participants = groupMeetingParticipantRepository.countByGroupMeetingAndGroup(authorGroupMeeting, group);
        authorGroupMeeting.updateGroupMeeting(
                request.getTitle(),
                request.getContent(),
                request.getLocation(),
                request.getMaxParticipant(),
                participants);

    }

    private void createMeetingValidate(Member member, CreateMeeting.Request request) {
        LocalDateTime meetingStartDate = request.getMeetingStartDate();
        LocalDateTime meetingEndDate = request.getMeetingEndDate();

        if (meetingStartDate.isEqual(meetingEndDate) || meetingStartDate.isAfter(meetingEndDate)) {
            throw new GroupException(GROUP_MEETING_START_TIME_IS_AFTER);
        }

        if (meetingStartDate.getMinute() > 0 || meetingEndDate.getMinute() > 0) {
            throw new GroupException(GROUP_MEETING_TIME_NOT_HOUR);
        }

        LocalDateTime startDate = parseTime(meetingStartDate, 0, 0, 0, 0);
        LocalDateTime endDate = parseTime(meetingStartDate, 23, 59, 59,0);

        boolean exists = groupMeetingQueryRepository.existsByMeeting(
                member.getId(),
                startDate,
                endDate,
                meetingStartDate,
                meetingEndDate);

        if (exists) {
            throw new GroupException(GROUP_MEETING_DUPLICATION);
        }
    }

    private void commonMeetingValidate(Group group, Member member) {
        if (group.getGroupStatus() != GROUP_STATUS_ING) {
            throw new GroupException(GROUP_ALREADY_DELETE_GROUP);
        }

        GroupJoinMember groupJoinMember = getGroupJoinMember(group, member);
        if (groupJoinMember.getGroupJoinMemberStatus() != GROUP_JOIN_MEMBER_STATUS_ING) {
            throw new GroupException(GROUP_ALREADY_WITHDRAW);
        }
    }

    private Member getMember(String memberEmail) {
        return memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new MemberException(MEMBER_EMAIL_ERROR));
    }

    private Group getGroup(Long groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupException(GROUP_NOT_FOUND));
    }

    private GroupJoinMember getGroupJoinMember(Group group, Member member) {
        return groupJoinMemberRepository.findByMemberAndGroup(member, group)
                .orElseThrow(() -> new GroupException(GROUP_NOT_FOUND_USER));
    }

    private MeetingDto getGroupMeeting(Long meetingId, Long groupId, Long memberId) {
        return groupMeetingQueryRepository.findByGroupMeeting(meetingId, groupId, memberId)
                .orElseThrow(() -> new GroupException(GROUP_MEETING_NOT_FOUND));
    }

    private GroupMeeting getAuthorGroupMeeting(Long meetingId, Group group, Member member) {
        return groupMeetingRepository.findByIdAndGroupAndMember(meetingId, group, member)
                .orElseThrow(() -> new GroupException(GROUP_NOT_SAME_AUTHOR));
    }

    private LocalDateTime parseTime(
            LocalDate localDate,
            int hour,
            int minute,
            int second,
            int nanoSecond) {

        return LocalDateTime.of(
                        localDate.getYear(),
                        localDate.getMonth(),
                        localDate.getDayOfMonth(),
                        hour,
                        minute,
                        second,
                        nanoSecond
        );
    }

    private LocalDateTime parseTime(
            LocalDateTime localDateTime,
            int hour,
            int minute,
            int second,
            int nanoSecond) {

        return LocalDateTime.of(
                localDateTime.getYear(),
                localDateTime.getMonth(),
                localDateTime.getDayOfMonth(),
                hour,
                minute,
                second,
                nanoSecond
        );
    }

}
