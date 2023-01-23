package com.workduo.member.membercalendar.service;

import com.workduo.common.CommonRequestContext;
import com.workduo.error.member.exception.MemberException;
import com.workduo.member.member.entity.Member;
import com.workduo.member.member.repository.MemberRepository;
import com.workduo.member.member.type.MemberStatus;
import com.workduo.member.membercalendar.dto.CalendarDay;
import com.workduo.member.membercalendar.repository.query.MemberCalendarQueryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.workduo.error.member.type.MemberErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MEMBER Calendar 테스트")
class MemberCalendarServiceImplTest {
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private CommonRequestContext commonRequestContext;
    @Mock
    private MemberCalendarQueryRepository memberCalendarQueryRepository;
    @InjectMocks
    MemberCalendarServiceImpl memberCalendarService;

    @Nested
    @DisplayName("멤버 일정 월 서비스 테스트")
    class month{

        @Test
        @DisplayName("멤버 일정 월 서비스 실패 [로그인 유저 미일치]")
        public void fail1() throws Exception{
            //given

            //when
            MemberException exception = assertThrows(
                    MemberException.class,
                    ()->memberCalendarService.getMonthCalendar(LocalDate.now()));
            //then
            assertEquals(MEMBER_EMAIL_ERROR,exception.getErrorCode());
        }
        @Test
        @DisplayName("멤버 일정 월 서비스 실패 [정지된 회원]")
        public void fail2() throws Exception{
            Member m = Member.builder().memberStatus(MemberStatus.MEMBER_STATUS_STOP).build();
            //given
            given(memberRepository.findByEmail(any()))
                    .willReturn(Optional.of(m));
            //when
            MemberException exception = assertThrows(
                    MemberException.class,
                    ()->memberCalendarService.getMonthCalendar(LocalDate.now()));
            //then
            assertEquals(MEMBER_STOP_ERROR,exception.getErrorCode());
        }
        @Test
        @DisplayName("멤버 일정 월 서비스 실패 [탈퇴한 회원]")
        public void fail3() throws Exception{
            Member m = Member.builder().memberStatus(MemberStatus.MEMBER_STATUS_WITHDRAW).build();
            //given
            given(memberRepository.findByEmail(any()))
                    .willReturn(Optional.of(m));
            //when
            MemberException exception = assertThrows(
                    MemberException.class,
                    ()->memberCalendarService.getMonthCalendar(LocalDate.now()));
            //then
            assertEquals(MEMBER_WITHDRAW_ERROR,exception.getErrorCode());
        }

        @Test
        @DisplayName("멤버 일정 월 서비스 성공")
        public void success() throws Exception{
            Member m = Member.builder().build();
            //given
            given(memberRepository.findByEmail(any()))
                    .willReturn(Optional.of(m));
            doReturn(List.of("2022-09-01","2022-09-30")).when(memberCalendarQueryRepository)
                    .searchMemberMonthDate(any(), any(),any());

            //when
            List<String> monthCalendar = memberCalendarService.getMonthCalendar(LocalDate.now());
            //then
            verify(memberCalendarQueryRepository,times(1))
                    .searchMemberMonthDate(any(),any(),any());
            assertThat(monthCalendar.size()).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("멤버 일정 일 서비스 테스트")
    class DayOfMonth{

        @Test
        @DisplayName("멤버 일정 일 서비스 실패 [로그인 유저 미일치]")
        public void fail1() throws Exception{
            //given
            //when
            MemberException exception = assertThrows(
                    MemberException.class,
                    ()->memberCalendarService.getDayCalendar(LocalDate.now()));
            //then
            assertEquals(MEMBER_EMAIL_ERROR,exception.getErrorCode());
        }
        @Test
        @DisplayName("멤버 일정 일 서비스 실패 [정지된 회원]")
        public void fail2() throws Exception{
            Member m = Member.builder().memberStatus(MemberStatus.MEMBER_STATUS_STOP).build();
            //given
            given(memberRepository.findByEmail(any()))
                    .willReturn(Optional.of(m));
            //when
            MemberException exception = assertThrows(
                    MemberException.class,
                    ()->memberCalendarService.getDayCalendar(LocalDate.now()));
            //then
            assertEquals(MEMBER_STOP_ERROR,exception.getErrorCode());
        }

        @Test
        @DisplayName("멤버 일정 일 서비스 실패 [탈퇴한 회원]")
        public void fail3() throws Exception{
            Member m = Member.builder().memberStatus(MemberStatus.MEMBER_STATUS_WITHDRAW).build();
            //given
            given(memberRepository.findByEmail(any()))
                    .willReturn(Optional.of(m));
            //when
            MemberException exception = assertThrows(
                    MemberException.class,
                    ()->memberCalendarService.getDayCalendar(LocalDate.now()));
            //then
            assertEquals(MEMBER_WITHDRAW_ERROR,exception.getErrorCode());
        }


        @Test
        @DisplayName("멤버 일정 일 서비스 실패 [일정이 없는 경우]")
        public void fail4() throws Exception{
            Member m = Member.builder().build();
            //given
            given(memberRepository.findByEmail(any()))
                    .willReturn(Optional.of(m));
            doReturn(new ArrayList<>()).when(memberCalendarQueryRepository)
                    .searchMemberDayDate(any(), any(),any());
            //when
            MemberException exception = assertThrows(
                    MemberException.class,
                    ()->memberCalendarService.getDayCalendar(LocalDate.now()));
            //then
            assertEquals(MEMBER_CALENDAR_DOES_NOT_EXIST,exception.getErrorCode());
        }

        @Test
        @DisplayName("멤버 일정 일 서비스 성공")
        public void success() throws Exception{

            Member m = Member.builder().build();
            CalendarDayDto d = CalendarDayDto.builder()
                    .startDate(LocalDateTime.now())
                    .endDate(LocalDateTime.now())
                    .build();
            CalendarDayDto d2 = CalendarDayDto.builder()
                    .startDate(LocalDateTime.now())
                    .endDate(LocalDateTime.now())
                    .build();
            //given
            given(memberRepository.findByEmail(any()))
                    .willReturn(Optional.of(m));
            doReturn(List.of(d,d2)).when(memberCalendarQueryRepository)
                    .searchMemberDayDate(any(), any(),any());

            //when
            List<CalendarDay> dayCalendar = memberCalendarService.getDayCalendar(LocalDate.now());
            //then
            verify(memberCalendarQueryRepository,times(1))
                    .searchMemberDayDate(any(),any(),any());
            assertThat(dayCalendar.size()).isEqualTo(2);
        }
    }
}