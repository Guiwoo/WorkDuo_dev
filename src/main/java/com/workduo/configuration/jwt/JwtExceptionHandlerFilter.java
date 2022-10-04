package com.workduo.configuration.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.workduo.error.global.exception.UsernameFromTokenException;
import com.workduo.error.global.type.GlobalExceptionType;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.workduo.error.global.type.GlobalExceptionType.responseJsonString;

@Slf4j
@Component
public class JwtExceptionHandlerFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{
            filterChain.doFilter(request,response);
        } catch (UsernameFromTokenException ex){
            log.error("exception exception handler filter");
            setErrorResponse(HttpStatus.BAD_REQUEST,request,response,ex);
        }catch (RuntimeException ex){
            log.error("runtime exception exception handler filter");
            setErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,request,response,ex);
        }
    }

    public void setErrorResponse(HttpStatus status,HttpServletRequest request, HttpServletResponse response,Throwable ex){
        response.setStatus(status.value());
        response.setContentType("application/json");
        JwtErrorResponse generate = generate(status, request, ex);
        ObjectMapper obj = new ObjectMapper();
        obj.registerModule(new JavaTimeModule());
        try{
            response.getWriter().write(obj.writeValueAsString(generate));
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public JwtErrorResponse generate(HttpStatus status,HttpServletRequest request,Throwable ex){
        return new JwtErrorResponse(request.getRequestURL().toString(),
                ex.getMessage(),status.value());
    }
    @Getter
    @Setter
    class JwtErrorResponse {
        private String path;
        private String error;
        private String timestamp;
        private int status;

        public JwtErrorResponse(String path, String error, int status) {
            this.path = path;
            this.error = error;
            this.timestamp = LocalDateTime.now().toString();
            this.status = status;
        }
    }
}
