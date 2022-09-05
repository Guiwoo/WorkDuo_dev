package com.workduo.group.group.service.impl;

import com.workduo.area.siggarea.entity.SiggArea;
import com.workduo.area.siggarea.repository.SiggAreaRepository;
import com.workduo.common.CommonRequestContext;
import com.workduo.error.group.exception.GroupException;
import com.workduo.group.group.dto.CreateGroup;
import com.workduo.group.group.entity.Group;
import com.workduo.group.group.repository.GroupRepository;
import com.workduo.group.group.service.GroupService;
import com.workduo.group.groupcreatemember.repository.GroupCreateMemberRepository;
import com.workduo.member.member.entity.Member;
import com.workduo.member.member.repository.MemberRepository;
import com.workduo.sport.sport.entity.Sport;
import com.workduo.sport.sport.repository.SportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.workduo.error.group.type.GroupErrorCode.GROUP_CREATE_MAXIMUM_EXCEEDED;
import static com.workduo.group.group.type.GroupStatus.GROUP_STATUS_ING;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

    private final MemberRepository memberRepository;
    private final GroupRepository groupRepository;
    private final GroupCreateMemberRepository groupCreateMemberRepository;
    private final SportRepository sportRepository;
    private final SiggAreaRepository siggAreaRepository;
    private final CommonRequestContext context;

    @Override
    @Transactional
    public void createGroup(CreateGroup.Request request) {
        Member member = getMember(context.getMemberEmail());
        SiggArea siggArea = getSiggArea(request.getSiggAreaId());
        Sport sport = getSport(request.getSportId());

        createGroupValidate(member);

        Group group = Group.builder()
                .siggArea(siggArea)
                .sport(sport)
                .name(request.getName())
                .limitPerson(request.getLimitPerson())
                .introduce(request.getIntroduce())
                .thumbnailPath(request.getThumbnailPath())
                .groupStatus(GROUP_STATUS_ING)
                .build();

        groupRepository.save(group);
    }

    private void createGroupValidate(Member member) {

        Long groupCreateMemberCount = groupCreateMemberCount(member);
        if (groupCreateMemberCount >= 3) {
            throw new GroupException(GROUP_CREATE_MAXIMUM_EXCEEDED);
        }
    }

    private Member getMember(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("user not found"));
    }

    private Long groupCreateMemberCount(Member member) {
        return groupCreateMemberRepository.countByMember(member);
    }

    private Sport getSport(Integer id) {
        return sportRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("해당 운동은 없는 운동입니다."));
    }

    private SiggArea getSiggArea(Integer id) {
        return siggAreaRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("해당 지역은 없는 지역입니다."));
    }
}
