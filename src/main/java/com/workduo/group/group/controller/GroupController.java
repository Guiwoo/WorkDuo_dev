package com.workduo.group.group.controller;

import com.workduo.common.CommonResponse;
import com.workduo.error.global.exception.CustomMethodArgumentNotValidException;
import com.workduo.group.group.dto.*;
import com.workduo.group.group.service.GroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/group")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    /**
     * 그룹 생성
     * @param request
     * @param bindingResult
     * @return
     */
    @PostMapping("")
    public ResponseEntity<?> createGroup(
            @RequestBody @Validated CreateGroup.Request request,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new CustomMethodArgumentNotValidException(bindingResult);
        }

        groupService.createGroup(request);

        return new ResponseEntity<>(
                CreateGroup.Response.from(),
                HttpStatus.CREATED
        );
    }

    /**
     * 그룹 해지 - 그룹장만 가능
     * @param groupId
     * @return
     */
    @PostMapping("/{groupId}")
    public ResponseEntity<?> deleteGroup(
            @PathVariable("groupId") Long groupId) {
        groupService.deleteGroup(groupId);

        return new ResponseEntity<>(
                CancelGroup.Response.from(),
                HttpStatus.OK
        );
    }

    /**
     * 그룹 탈퇴 - 그룹장은 불가능
     * @param groupId
     * @return
     */
    @DeleteMapping("/{groupId}")
    public ResponseEntity<?> withdrawGroup(
            @PathVariable("groupId") Long groupId) {
        groupService.withdrawGroup(groupId);

        return new ResponseEntity<>(
                CancelGroup.Response.from()
                , HttpStatus.OK
        );
    }

    /**
     * 그룹 상세
     * @param groupId
     * @return
     */
    @GetMapping("/{groupId}")
    public ResponseEntity<?> detailGroup(
            @PathVariable("groupId") Long groupId) {

        return new ResponseEntity<>(
                DetailGroup.Response.from(groupService.groupDetail(groupId))
                , HttpStatus.OK
        );
    }

    /**
     * 그룹 리스트
     */
    @GetMapping("")
    public ResponseEntity<?> groupList(
            Pageable pageable,
            ListGroup.Request condition) {
        return new ResponseEntity<>(
                ListGroup.Response.from(groupService.groupList(pageable, condition))
                , HttpStatus.OK
        );
    }

    /**
     * 그룹 좋아요
     * @param groupId
     * @return
     */
    @PostMapping("/{groupId}/like")
    public ResponseEntity<?> groupLike(
            @PathVariable("groupId") Long groupId) {

        groupService.groupLike(groupId);
        return new ResponseEntity<>(
                CommonResponse.from(),
                HttpStatus.OK
        );
    }

    /**
     * 그룹 좋아요 취소
     * @param groupId
     * @return
     */
    @DeleteMapping("/{groupId}/like")
    public ResponseEntity<?> groupUnLike(
            @PathVariable("groupId") Long groupId) {

        groupService.groupUnLike(groupId);
        return new ResponseEntity<>(
                CommonResponse.from(),
                HttpStatus.OK
        );
    }

    /**
     * 그룹 참여
     * @param groupId
     * @return
     */
    @PostMapping("/{groupId}/participant")
    public ResponseEntity<?> groupParticipant(
            @PathVariable("groupId") Long groupId) {

        groupService.groupParticipant(groupId);
        return new ResponseEntity<>(
                CommonResponse.from(),
                HttpStatus.OK
        );
    }

    /**
     * 그룹 참여자 리스트
     * @param pageable
     * @return
     */
    @GetMapping("participant/{groupId}")
    public ResponseEntity<?> groupParticipantList(
            Pageable pageable,
            @PathVariable("groupId") Long groupId) {

        return new ResponseEntity<>(
                ParticipantGroup.Response.from(
                        groupService.groupParticipantList(pageable, groupId)
                ),
                HttpStatus.OK);
    }
}
