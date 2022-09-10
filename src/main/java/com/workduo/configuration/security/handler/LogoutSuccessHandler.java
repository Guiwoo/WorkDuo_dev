package com.workduo.configuration.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {
    private int statusCode;

    public LogoutSuccessHandler(int statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
                                Authentication authentication) throws IOException, ServletException {

        response.setStatus(this.statusCode);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        Map<String,Object> map = new HashMap<>();
        map.put("success","T");
        ObjectMapper o = new ObjectMapper();
        response.getWriter().println(o.writeValueAsString(map));
    }

}
