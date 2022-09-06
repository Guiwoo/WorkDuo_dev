package com.workduo.configuration.jwt;

import com.workduo.common.CommonRequestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private TokenProvider tokenProvider;
    @Mock
    private CommonRequestContext context;

    JwtAuthenticationFilter jwt;

    @BeforeEach
    void init(){
        jwt = new JwtAuthenticationFilter(tokenProvider,context);
    }

    @Test
    @DisplayName("토큰 헤더 에 [없을떄]")
    void jwtResolveTokenFromRequestNull() throws ServletException, IOException {


        MockHttpServletRequest req = new MockHttpServletRequest();

        String result = jwt.resolveTokenFromRequest(req);

        assertNull(result);
    }

    @Test
    @DisplayName("토큰 헤더 에 [있을때] [프리픽스 없는 토큰]")
    void jwtResolveTokenFromRequestExist() throws ServletException, IOException {


        MockHttpServletRequest req = new MockHttpServletRequest();
        req.addHeader("Authorization","망나니");

        String result = jwt.resolveTokenFromRequest(req);

        assertNull(result);
    }

    @Test
    @DisplayName("토큰 헤더 에 [있을때] [비어있는 토큰]")
    void jwtResolveTokenFromRequestExistEmpty() throws ServletException, IOException {


        MockHttpServletRequest req = new MockHttpServletRequest();
        req.addHeader("Authorization","");

        String result = jwt.resolveTokenFromRequest(req);

        assertNull(result);
    }

    @Test
    @DisplayName("토큰 헤더 에 [있을때] [정확한 토큰]")
    void jwtResolveTokenFromRequestSuccess() throws ServletException, IOException {



        MockHttpServletRequest req = new MockHttpServletRequest();
        req.addHeader("Authorization","Bearer amIToken");

        String result = jwt.resolveTokenFromRequest(req);

        assertEquals(result,"amIToken");
    }

    @Test
    @DisplayName("토큰 이 없어서 다음으로 넘어감")
    void jwtFilterDoNext() throws ServletException, IOException {

        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();
        var chain = mock(FilterChain.class);
        req.addHeader("Authorization","Bearer amIToken");

        jwt.doFilter(req,res,chain);

        verify(chain,times(1)).doFilter(req,res);
    }

    @Test
    @DisplayName("토큰 이 있지만 실패 해서 다음으로 넘어감")
    void jwtFilterDoNextFailTokenValid() throws ServletException, IOException {

        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();
        var chain = mock(FilterChain.class);
        req.addHeader("Authorization","Bearer amIToken");

        jwt.doFilter(req,res,chain);

        verify(tokenProvider,times(1)).validateToken("amIToken");
        verify(chain,times(1)).doFilter(req,res);
    }

    @Test
    @DisplayName("토큰 이 있고 성공 해서 다음으로 넘어감")
    void jwtFilterDoNextSuccessToken() throws ServletException, IOException {

        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();
        var chain = mock(FilterChain.class);
        req.addHeader("Authorization","Bearer amIToken");
        var s = mock(SecurityContextHolder.getContext().getClass());
        when(tokenProvider.validateToken(any())).thenReturn(true);
        when(tokenProvider.getUsername(any())).thenReturn("email");
        jwt.doFilter(req,res,chain);

        verify(tokenProvider,times(1)).validateToken("amIToken");
        verify(tokenProvider,times(1)).getUsername("amIToken");
        verify(context,times(1)).setMemberEmail("email");
        verify(chain,times(1)).doFilter(req,res);
    }
}