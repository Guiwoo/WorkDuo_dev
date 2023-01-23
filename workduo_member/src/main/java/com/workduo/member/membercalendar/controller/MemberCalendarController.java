package com.workduo.member.membercalendar.controller;

import com.workduo.member.membercalendar.service.MemberCalendarService;
import com.core.util.ApiUtils;
import com.core.util.ApiUtils.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name="멤버 일정 서비스",description = "멤버 일정 조회 관련 API 입니다.")
public class MemberCalendarController {

    private final MemberCalendarService memberService;

    @GetMapping("")
    @Operation(summary = "멤버 일정 월 단위 조회" ,description = "멤버 일정 월 단위 조회")
    public ApiResult<?> getMonthCalendar(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @Parameter(description = "일정 조회 시 YYYY-MM-DD 의 입력 이 필요합니다."
            ,example = "2022-10-03")
            @Valid LocalDate date
    ){
        return ApiUtils.success(memberService.getMonthCalendar(date));
    }

    @GetMapping("/list")
    @Operation(summary = "멤버 일정 일 단위 조회" ,description = "멤버 일정 일 단위 조회")
    public ApiResult<?> getDayCalendar(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @Parameter(description = "일정 조회 시 YYYY-MM-DD 의 입력 이 필요합니다."
                    ,example = "2022-10-03")
            @Valid LocalDate date
    ){
        return ApiUtils.success(memberService.getDayCalendar(date));
    }

}
