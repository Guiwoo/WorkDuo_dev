package com.workduo.group.group.controller;

import com.workduo.error.global.exception.CustomMethodArgumentNotValidException;
import com.workduo.group.group.dto.CancelGroup;
import com.workduo.group.group.dto.CreateGroup;
import com.workduo.group.group.service.GroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

        return new ResponseEntity<>(CreateGroup.Response.from(), HttpStatus.CREATED);
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

        return new ResponseEntity<>(CancelGroup.Response.from(), HttpStatus.OK);
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

        return new ResponseEntity<>(CancelGroup.Response.from(), HttpStatus.OK);
    }
}
