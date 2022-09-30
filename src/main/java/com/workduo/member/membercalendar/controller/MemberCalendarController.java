package com.workduo.member.membercalendar.controller;

import com.workduo.member.membercalendar.service.MemberCalendarService;
import com.workduo.util.ApiUtils;
import com.workduo.util.ApiUtils.ApiResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDate;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/member/calendar")
public class MemberCalendarController {

    private final MemberCalendarService memberService;

    @GetMapping("")
    public ApiResult<?> getMonthCalendar(
            // date type example "2022-09-30"
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @Valid LocalDate date
    ){
        return ApiUtils.success(memberService.getMonthCalendar(date));
    }

    @GetMapping("/list")
    public ApiResult<?> getDayCalendar(
            // date type example "2022-09-30"
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @Valid LocalDate date
    ){
        return ApiUtils.success(memberService.getDayCalendar(date));
    }

}
