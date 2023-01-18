package com.workduo.configuration.security.error;

import com.core.error.global.type.GlobalExceptionType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.core.error.global.type.GlobalExceptionType.responseJsonString;

public class CustomNotAuthorization implements AccessDeniedHandler {
    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException)
            throws IOException, ServletException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.sendError(GlobalExceptionType.AUTHORIZATION_ERROR.getHttpStatus().value());
        response.getWriter().println(
                responseJsonString(GlobalExceptionType.AUTHORIZATION_ERROR.getMessage())
        );
    }
}

