package com.workduo.configuration.security;

import com.workduo.error.global.type.GlobalExceptionType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.workduo.error.global.type.GlobalExceptionType.responseJsonString;

public class LogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {

    private String logoutUrl;
    private int statusCode;

    public LogoutSuccessHandler(String logoutUrl, int statusCode) {
        this.logoutUrl = logoutUrl;
        this.statusCode = statusCode;
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
                                Authentication authentication) throws IOException, ServletException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        Map<String,Object> map = new HashMap<>();
        map.put("success","T");
        response.getWriter().println(map);
    }

}
