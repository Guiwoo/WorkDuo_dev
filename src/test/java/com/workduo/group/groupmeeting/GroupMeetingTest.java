package com.workduo.group.groupmeeting;

import com.workduo.group.gropcontent.dto.creategroupcontent.CreateGroupContent;
import com.workduo.group.gropcontent.dto.creategroupcontent.CreateGroupContentDto;
import com.workduo.group.gropcontent.repository.GroupContentRepository;
import com.workduo.group.gropcontent.service.GroupContentService;
import com.workduo.group.group.entity.Group;
import com.workduo.group.group.repository.GroupRepository;
import com.workduo.group.groupmeetingparticipant.dto.createGroupMeetingParticipant.CreateParticipant;
import com.workduo.group.groupmeetingparticipant.dto.createGroupMeetingParticipant.CreateParticipantDto;
import com.workduo.group.groupmeetingparticipant.repository.GroupMeetingParticipantRepository;
import com.workduo.group.groupmeetingparticipant.service.GroupMeetingParticipantService;
import com.workduo.group.groupmetting.repository.GroupMeetingRepository;
import com.workduo.group.groupmetting.service.GroupMeetingLockService;
import com.workduo.member.member.entity.Member;
import com.workduo.member.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureMockMvc
public class GroupMeetingTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private GroupMeetingRepository groupMeetingRepository;

    @Autowired
    private GroupMeetingParticipantRepository groupMeetingParticipantRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    @Qualifier("groupMeetingParticipantServiceImpl")
    private GroupMeetingParticipantService groupMeetingParticipantService;

    @Autowired
    private GroupContentRepository groupContentRepository;

    @Autowired
    @Qualifier("groupContentServiceImpl")
    private GroupContentService groupContentService;

    @Autowired
    private GroupMeetingLockService groupMeetingLockService;



    @Test
    @DisplayName("createGroupContentAndMeeting Test")
    @WithMockUser
    public void createGroupContentAndMeeting() throws Exception {
        // given
        Member member = memberRepository.findById(17L)
                .orElseThrow(() -> new IllegalStateException("not found member"));

        Group group = groupRepository.findById(13L)
                .orElseThrow(() -> new IllegalStateException("not found group"));

        CreateGroupContent.Request request = CreateGroupContent.Request.builder()
                .groupId(group.getId())
                .title("title")
                .content("content")
                .activate(true)
                .maxParticipant(10)
                .location("location")
                .meetingDate(LocalDateTime.now())
                .build();
        // when
        CreateGroupContentDto groupContent = groupContentService.createGroupContent(request, member.getId());

        // then
        assertEquals(groupContent.getGroupId(), group.getId());
        assertEquals(groupContent.getMemberId(), member.getId());
    }

    @Test
    @DisplayName("createGroupContentAndMeeting participant success")
    public void createGroupContentMeetingParticipantSuccess() throws Exception {
        // given
        Member member = memberRepository.findById(17L)
                .orElseThrow(() -> new IllegalStateException("not found member"));

        CreateParticipant.Request request = CreateParticipant.Request.builder()
                .groupMeetingId(4L)
                .build();
        // when
        CreateParticipantDto createParticipantDto = groupMeetingParticipantService.meetingParticipant(request, member.getId());

        // then
        assertEquals(request.getGroupMeetingId(), createParticipantDto.getGroupMeetingId());
    }

    @Test
    @DisplayName("createGroupContentAndMeeting participant fail")
    public void createGroupContentMeetingParticipantFail() throws Exception {
        // given
        Member member = memberRepository.findById(17L)
                .orElseThrow(() -> new IllegalStateException("not found member"));

        CreateParticipant.Request request = CreateParticipant.Request.builder()
                .groupMeetingId(4L)
                .build();
        // when
        IllegalStateException illegalStateException = assertThrows(IllegalStateException.class,
                () -> groupMeetingParticipantService.meetingParticipant(request, member.getId()));

        // then
        assertEquals(illegalStateException.getMessage(), "이미 참가한 모임입니다.");
    }

    static class MeetingRunnable implements Runnable {
        Member member;
        CreateParticipant.Request request;
        GroupMeetingParticipantService groupMeetingParticipantService;

        public MeetingRunnable(Member member, CreateParticipant.Request request, GroupMeetingParticipantService groupMeetingParticipantService) {
            this.member = member;
            this.request = request;
            this.groupMeetingParticipantService = groupMeetingParticipantService;
        }

        @Override
        public void run() {
            try {
                groupMeetingParticipantService.meetingParticipant(request, member.getId());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    @DisplayName("createGroupContentAndMeeting lock")
    public void createGroupContentMeetingParticipantLock() throws InterruptedException {
        // given
        Long numberOfThreads = 17L;
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        for (int i = 0; i < 2; i++) {
            Member member = memberRepository.findById(17L + i)
                    .orElseThrow(() -> new IllegalStateException("not found member"));

            CreateParticipant.Request request = CreateParticipant.Request.builder()
                    .groupMeetingId(1L)
                    .build();

            MeetingRunnable meetingRunnable = new MeetingRunnable(member, request, groupMeetingParticipantService);
            Thread thread = new Thread(meetingRunnable);
            thread.start();
        }
    }
}
