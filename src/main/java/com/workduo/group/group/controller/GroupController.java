package com.workduo.group.group.controller;

import com.workduo.error.global.exception.CustomMethodArgumentNotValidException;
import com.workduo.group.group.dto.CreateGroup;
import com.workduo.group.group.service.GroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/v1/group")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

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
}
