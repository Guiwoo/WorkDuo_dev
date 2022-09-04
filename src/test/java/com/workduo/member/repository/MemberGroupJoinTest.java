package com.workduo.member.repository;

import com.workduo.area.siggarea.entity.SiggArea;
import com.workduo.configuration.jpa.JpaAuditingConfiguration;
import com.workduo.group.group.type.GroupStatus;
import com.workduo.group.groupjoinmember.type.GroupJoinMemberStatus;
import com.workduo.group.groupjoinmember.type.GroupRole;
import com.workduo.group.group.entity.Group;
import com.workduo.group.group.repository.GroupRepository;
import com.workduo.group.groupjoinmember.entity.GroupJoinMember;
import com.workduo.group.groupjoinmember.repository.GroupJoinMemberRepository;
import com.workduo.member.member.entity.Member;
import com.workduo.member.member.repository.MemberQueryRepository;
import com.workduo.member.member.repository.MemberQueryRepositoryImpl;
import com.workduo.member.member.repository.MemberRepository;
import com.workduo.member.member.type.MemberStatus;
import com.workduo.sport.sport.entity.Sport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;

import static com.workduo.group.group.type.GroupStatus.*;
import static com.workduo.group.groupjoinmember.type.GroupRole.GROUP_ROLE_NORMAL;
import static com.workduo.member.member.type.MemberStatus.*;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Transactional
@Rollback(value = false)
@Import({JpaAuditingConfiguration.class, MemberQueryRepositoryImpl.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class MemberGroupJoinTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupJoinMemberRepository groupJoinUserRepository;

    @Autowired
    private MemberQueryRepositoryImpl memberQueryRepository;

    @PersistenceContext
    private EntityManager em;

    @Test
    @DisplayName("member group insert")
    @WithAnonymousUser
    @Transactional
    public void memberGroupInsert() throws Exception {
        Member member = Member.builder()
                .username("한규빈")
                .email("rbsks147@naver.com")
                .phoneNumber("010-4046-3138")
                .nickname("규난")
                .password("1234")
                .memberStatus(MEMBER_STATUS_ING)
                .build();

        SiggArea siggArea = SiggArea.builder()
                .id(1)
                .build();
        Sport sport = Sport.builder()
                .id(1)
                .build();

        Group group = Group.builder()
                .siggArea(siggArea)
                .sport(sport)
                .name("group1")
                .limitPerson(100)
                .introduce("group1 test")
                .thumbnailPath("test")
                .groupStatus(GROUP_STATUS_ING)
                .build();

        memberRepository.save(member);
        groupRepository.save(group);


        GroupJoinMember groupJoinUser = GroupJoinMember.builder()
                .member(member)
                .group(group)
                .groupRole(GROUP_ROLE_NORMAL)
                .groupJoinMemberStatus(GroupJoinMemberStatus.GROUP_JOIN_MEMBER_STATUS_ING)
                .build();

        groupJoinUserRepository.save(groupJoinUser);

        em.flush();
        em.clear();

//        Member findMember = memberRepository.findById(13L)
//                .orElseThrow(() -> new UsernameNotFoundException("not found user"));
//
//        Group findGroup = groupRepository.findById(8L)
//                .orElseThrow(() -> new IllegalStateException(""));
//
//        GroupJoinMember fineGroupJoinMember = groupJoinUserRepository.findById(5L)
//                .orElseThrow(() -> new IllegalStateException(""));

//        assertThat(findMember.getId()).isEqualTo(fineGroupJoinMember.getMember().getId());
//        assertThat(findGroup.getId()).isEqualTo(fineGroupJoinMember.getGroup().getId());
    }

    @Test
    @DisplayName("findAllQueryTest")
    public void findAllQueryTest() throws Exception {
        // given

        // when
        List<Member> all = memberQueryRepository.findAll();

        // then
        assertThat(all.size()).isEqualTo(0);
    }
}
