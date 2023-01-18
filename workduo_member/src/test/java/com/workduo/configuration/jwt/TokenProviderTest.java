package com.workduo.configuration.jwt;

import com.workduo.member.member.service.MemberService;
import com.workduo.member.member.dto.MemberRoleDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenProviderTest {
    @Mock
    private MemberService memberService;
    @Spy
    @InjectMocks
    TokenProvider tokenProvider;


    @Test
    @DisplayName("토큰생성 성공/정확한 유저 이름")
    void successGenerateToken(){
        List<MemberRoleDto> list = new ArrayList<>();
        var now = new Date();

        String algo = "eyJhbGciOiJIUzI1NiJ9";
        String rs = tokenProvider.generateToken("abc",list);
        assertEquals(algo,rs.split("\\.")[0]);
    }

    @Test
    @DisplayName("유저의 인증정보를 가져오는 메서드 확인")
    void getUserAuthenticationTest(){
        doReturn("abc").when(tokenProvider).getEmail(any());
        when(memberService.loadUserByUsername("abc")).thenReturn(mock(UserDetails.class));

        tokenProvider.getAuthentication(any());
        verify(tokenProvider,times(1)).getEmail(any());
        verify(tokenProvider,times(1)).getAuthentication(any());
    }

    @Test
    @DisplayName("토큰에서  유저 이름 가져오기")
    void getUserName() {
        Claims c = Jwts.claims().setSubject("abc");

        doReturn(c).when(tokenProvider).parseClaims(any());
        String name = tokenProvider.getEmail("abc");

        assertEquals(name, "abc");
    }

    @Test
    @DisplayName("토큰 검증 실패 텍스트 없는경우")
    void validateToken(){
        boolean result = tokenProvider.validateToken(null);
        assertFalse(result);
    }
    @Test
    @DisplayName("토큰 검증 만료기간 지난경우")
    void validateTokenExpired() throws ParseException {
        var now = new Date();
        Claims claims = Jwts.claims().setSubject("abc").setExpiration(now);
        var expiredDate = new SimpleDateFormat("yyyy-MM-dd")
                .parse("2022-09-05");

        doReturn(claims).when(tokenProvider).parseClaims(any());

        boolean result = tokenProvider.validateToken("token");
        assertFalse(result);
    }
}