package com.workduo.group.groupmeetingparticipant.controller;

import com.workduo.configuration.aop.groupmeeting.GroupMeetingLock;
import com.workduo.group.groupmeetingparticipant.dto.createGroupMeetingParticipant.CreateParticipant;
import com.workduo.group.groupmeetingparticipant.service.GroupMeetingParticipantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/groupMeetingParticipant")
@RequiredArgsConstructor
public class GroupMeetingParticipantController {

    private final GroupMeetingParticipantService groupMeetingParticipantService;

    @PostMapping("")
    @GroupMeetingLock
    public CreateParticipant.Response securityTest(
            @RequestBody @Valid CreateParticipant.Request request) throws InterruptedException {
        try {
            return CreateParticipant.Response.from(
                    groupMeetingParticipantService.meetingParticipant(request, request.getMemberId()));
        } catch (Exception e) {
            throw e;
        }

    }
}
