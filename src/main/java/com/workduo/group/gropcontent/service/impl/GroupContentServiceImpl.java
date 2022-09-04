package com.workduo.group.gropcontent.service.impl;

import com.workduo.group.gropcontent.dto.creategroupcontent.CreateGroupContent;
import com.workduo.group.gropcontent.dto.creategroupcontent.CreateGroupContentDto;
import com.workduo.group.gropcontent.entity.GroupContent;
import com.workduo.group.gropcontent.repository.GroupContentRepository;
import com.workduo.group.gropcontent.service.GroupContentService;
import com.workduo.group.group.entity.Group;
import com.workduo.group.group.repository.GroupRepository;
import com.workduo.group.groupmetting.entity.GroupMeeting;
import com.workduo.group.groupmetting.repository.GroupMeetingRepository;
import com.workduo.member.member.entity.Member;
import com.workduo.member.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupContentServiceImpl implements GroupContentService {

    private final GroupContentRepository groupContentRepository;
    private final MemberRepository memberRepository;
    private final GroupRepository groupRepository;

    @Override
    @Transactional
    public CreateGroupContentDto createGroupContent(CreateGroupContent.Request request, Long memberId) {
        Member findMember = getMember(memberId);
        Group findGroup = getGroup(request.getGroupId());
        GroupContent groupContent = GroupContent.builder()
                .member(findMember)
                .group(findGroup)
                .title(request.getTitle())
                .content(request.getContent())
                .deletedYn(false)
                .build();

        groupContentRepository.save(groupContent);

        return CreateGroupContentDto.fromEntity(groupContent);
    }

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalStateException("user not found"));
    }

    private Group getGroup(Long groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalStateException("group not found"));
    }
}
