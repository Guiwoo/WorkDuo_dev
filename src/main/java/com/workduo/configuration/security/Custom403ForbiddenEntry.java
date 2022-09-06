package com.workduo.configuration.security;

import com.workduo.error.global.type.GlobalExceptionType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class Custom403ForbiddenEntry implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException)
            throws IOException, ServletException {
        response.sendError(GlobalExceptionType.INTERNAL_ERROR.getHttpStatus().value());
        response.getWriter().print(GlobalExceptionType.INTERNAL_ERROR.getMessage());
    }
}
