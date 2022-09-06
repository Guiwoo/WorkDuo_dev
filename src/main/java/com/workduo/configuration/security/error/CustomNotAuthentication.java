package com.workduo.configuration.security.error;

import com.workduo.error.global.type.GlobalExceptionType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.workduo.error.global.type.GlobalExceptionType.responseJsonString;

public class CustomNotAuthentication implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException)
            throws IOException, ServletException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.sendError(GlobalExceptionType.LOGIN_ERROR.getHttpStatus().value(),
                authException.getMessage());

        response.getWriter().println(
                responseJsonString(GlobalExceptionType.LOGIN_ERROR.getMessage())
        );
    }
}
