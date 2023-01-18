package com.workduo.group.groupmeeting;

import com.workduo.configuration.querydsl.CustomMySQL8InnoDBDialect;
import com.workduo.configuration.jpa.JpaAuditingConfiguration;
import com.workduo.configuration.querydsl.QueryDslConfiguration;
import com.workduo.group.group.repository.GroupRepository;
import com.workduo.group.groupmetting.dto.MeetingInquireDto;
import com.workduo.group.groupmetting.repository.query.impl.GroupMeetingQueryRepositoryImpl;
import com.workduo.member.member.repository.MemberRepository;
import org.hibernate.dialect.MySQL57Dialect;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Transactional
@Import({
        JpaAuditingConfiguration.class,
        GroupMeetingQueryRepositoryImpl.class,
        QueryDslConfiguration.class,
        MySQL57Dialect.class,
        CustomMySQL8InnoDBDialect.class
        })
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
public class GroupMeetingRepositoryTest {

    @Autowired
    private GroupMeetingQueryRepositoryImpl groupMeetingQueryRepository;

    @Test
    @DisplayName("meetingInquireList")
    @Transactional
    @Rollback(value = false)
    public void meetingInquireList() throws Exception {
        // given
        Long memberId = 3L;
        LocalDateTime startDate = LocalDateTime.of(2022, 9, 26, 0, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2022, 9, 26, 23, 59, 59, 999999);
        // when
        List<MeetingInquireDto> meetingInquireDtos = groupMeetingQueryRepository.meetingInquireList(memberId, startDate, endDate);

        // then
        for (MeetingInquireDto meetingInquireDto : meetingInquireDtos) {
            System.out.println(meetingInquireDto.getTime() + " " + meetingInquireDto.getTerm());
        }
    }

    @Test
    @DisplayName("existsByMeeting")
    @Transactional
    @Rollback(value = false)
    public void existsByMeeting() throws Exception {
        // given
        Long memberId = 3L;
        LocalDateTime startDate = LocalDateTime.of(2022, 9, 26, 0, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2022, 9, 27, 00, 00, 0, 0);
        LocalDateTime newStartDate = LocalDateTime.of(2022, 9, 26, 23, 0, 0);
        LocalDateTime newEndDate = LocalDateTime.of(2022, 9, 27, 00, 0, 0);

        // when
        boolean exists = groupMeetingQueryRepository.existsByMeeting(memberId, startDate, endDate, newStartDate, newEndDate);

        // then

        assertEquals(exists, true);

    }
}
