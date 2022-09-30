package com.workduo.member.membercalendar.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workduo.common.CommonRequestContext;
import com.workduo.configuration.jwt.JwtAuthenticationFilter;
import com.workduo.configuration.jwt.TokenProvider;
import com.workduo.error.member.exception.MemberException;
import com.workduo.member.membercalendar.service.MemberCalendarService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberCalendarController.class)
@Import(
        {
                TokenProvider.class,
                CommonRequestContext.class,
                JwtAuthenticationFilter.class,
                MemberException.class
        }
)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("MEMBER CALENDAR API 테스트")
class MemberCalendarControllerTest {

    @MockBean
    private TokenProvider tokenProvider;
    @MockBean
    private MemberCalendarService memberCalendarService;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("멤버 월 일정 API 테스트")
    class CalendarMonthTest{
        @Test
        @DisplayName("멤버 월 일정 API 실패 [날짜 데이트 이상할시]")
        public void fail() throws Exception{
            //given
            //when
            mockMvc.perform(get("/api/v1/member/calendar?date=2022")
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value("F"))
                    .andDo(print());

        }

        @Test
        @DisplayName("멤버 월 일정 API 성공")
        public void success() throws Exception{
            //given
            //when
            mockMvc.perform(get("/api/v1/member/calendar?date=2022-09-29")
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isOk())
                    .andDo(print());
            ;
            //then
        }
    }
    @Nested
    @DisplayName("멤버 일 일정 리스트 API 테스트")
    class CalendarDayTest{

        @Test
        @DisplayName("멤버 일 일정 API 실패 [날짜 데이트 이상할시]")
        public void fail() throws Exception{
            //given
            //when
            mockMvc.perform(get("/api/v1/member/calendar/list?date=2022")
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value("F"))
                    .andDo(print());

        }

        @Test
        @DisplayName("멤버 일 일정 리스트 성공")
        public void getList() throws Exception{
            mockMvc.perform(get("/api/v1/member/calendar/list?date=2022-09-29")
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isOk())
                    .andDo(print());
        }
    }
}