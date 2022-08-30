package com.workduo.group.groupmetting.repository;

import com.workduo.group.gropcontent.entity.GroupContent;
import com.workduo.group.groupmetting.entity.GroupMeeting;
import com.workduo.member.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupMeetingRepository extends JpaRepository<GroupMeeting, Long> {
    boolean
    existsByMemberAndGroupContent(Member member, GroupContent groupContent);
}
