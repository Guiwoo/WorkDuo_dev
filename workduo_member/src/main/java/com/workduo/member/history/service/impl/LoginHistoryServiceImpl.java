package com.workduo.member.history.service.impl;

import com.core.domain.member.entity.Member;
import com.core.domain.member.repository.MemberRepository;
import com.core.domain.member_history.entity.LoginHistory;
import com.core.domain.member_history.repository.LoginHistoryRepository;
import com.core.error.global.type.GlobalExceptionType;
import com.core.error.member.exception.MemberException;
import com.core.error.member.type.MemberErrorCode;
import com.workduo.member.history.service.LoginHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LoginHistoryServiceImpl implements LoginHistoryService {
    private final LoginHistoryRepository loginHistoryRepository;
    private final MemberRepository memberRepository;

    public void saveLoginHistory(String email, HttpServletRequest req) throws Exception {
        Member m = memberRepository.findByEmail(email)
                .orElseThrow(()->  new MemberException(MemberErrorCode.MEMBER_EMAIL_ERROR));

        LoginHistory lH = LoginHistory.builder()
                .member(m)
                .userAgent(getAgent(req))
                .clientIp(getClientIp(req))
                .loginDt(LocalDateTime.now())
                .build();

        loginHistoryRepository.save(lH);
    }
    private String getAgent(HttpServletRequest request){
        return request.getHeader("user-agent");
    }

    private String getClientIp(HttpServletRequest request) throws Exception {
        String LOCALHOST_IPV4 = "127.0.0.1";
        String LOCALHOST_IPV6 = "0:0:0:0:0:0:0:1";
        String ipAddress = request.getHeader("X-Forwarded-For");
        if(ipAddress == null || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }

        if(ipAddress == null || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }

        if(ipAddress == null || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            if(LOCALHOST_IPV4.equals(ipAddress) || LOCALHOST_IPV6.equals(ipAddress)) {
                try {
                    InetAddress inetAddress = InetAddress.getLocalHost();
                    ipAddress = inetAddress.getHostAddress();
                } catch (UnknownHostException e) {
                    // 이상한 호스트 인 경우의 에러
                    throw new Exception(GlobalExceptionType.ANONYMOUS_HOST_ERROR.getMessage());
                }
            }
        }

        if(!StringUtils.isEmpty(ipAddress)
                && ipAddress.length() > 15
                && ipAddress.indexOf(",") > 0) {
            ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
        }
        return ipAddress;
    }
}
