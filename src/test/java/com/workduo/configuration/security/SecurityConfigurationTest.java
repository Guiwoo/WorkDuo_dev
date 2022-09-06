package com.workduo.configuration.security;

import org.junit.Before;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@WebAppConfiguration
@AutoConfigureMockMvc
class SecurityConfigurationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private MockMvc mockMvc;


    @Test
    @DisplayName("권한 없이 모두 접근 가능")
    void canAccessPageWithoutAuthorization() throws Exception {

        mockMvc.perform(get("/api/v1/login"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Api 호출 실패 [로그인 안된 호출]")
    void failedAccessApiCallWithoutLogin() throws Exception {

        mockMvc.perform(get("/api/v1/auth"))
                .andDo(print())
                .andExpect(status().is(403));
    }
}