package com.workduo.configuration.security;

import com.workduo.configuration.jwt.JwtAuthenticationFilter;
import com.workduo.configuration.security.error.CustomNotAuthentication;
import com.workduo.configuration.security.error.CustomNotAuthorization;
import com.workduo.configuration.security.handler.LogoutSuccessHandler;
import com.workduo.member.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HeaderWriterLogoutHandler;
import org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter;

import static org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter.Directive.*;
import static org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter.Directive.EXECUTION_CONTEXTS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration {
    private final MemberService memberService;
    private final JwtAuthenticationFilter authenticationFilter;
    private static final ClearSiteDataHeaderWriter.Directive[] SOURCE =
            {CACHE, COOKIES, STORAGE, EXECUTION_CONTEXTS};

    @Bean
    public LogoutSuccessHandler getLogoutHandler(){
        return new LogoutSuccessHandler(200);
    }
    @Bean
    public PasswordEncoder gertPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(memberService);
        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();

        http
                .httpBasic().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http
                .headers().frameOptions().sameOrigin().and()
                .csrf().ignoringAntMatchers("/h2-console/**").disable();

        http.authenticationManager(authenticationManager);
        //접근 누구나 가능
        http.authorizeRequests()
                .antMatchers(
                        "/h2-console/**",
                        "/api/v1/member/login",
                        "/api/v1/member/logout"
                ).permitAll()
                .antMatchers(HttpMethod.POST,"/api/v1/member")
                .permitAll();

        http.authorizeRequests()
                .antMatchers(HttpMethod.GET,
                        "/api/v1/group",
                        "/api/v1/group/{groupId}",
                        "/api/v1/member/content/list"
                ).permitAll();

        http.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);


        // 로그인 된 유저(토큰 이 있는),권한 이 있는 접근
        http.authorizeRequests()
                .antMatchers( "/api/v1/group/**")
                .hasAnyAuthority("ROLE_MEMBER", "ROLE_ADMIN")
                .antMatchers(HttpMethod.PATCH,"/api/v1/member")
                .hasAnyAuthority( "ROLE_MEMBER", "ROLE_ADMIN")
                .antMatchers("/api/v1/member/password")
                .hasAnyAuthority( "ROLE_MEMBER", "ROLE_ADMIN")
                .antMatchers(HttpMethod.DELETE,"/api/v1/member")
                .hasAnyAuthority( "ROLE_MEMBER", "ROLE_ADMIN")
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(new CustomNotAuthentication())
                .accessDeniedHandler(new CustomNotAuthorization());

        http.logout()
                .logoutUrl("/api/v1/member/logout")
                .permitAll()
                .addLogoutHandler(new HeaderWriterLogoutHandler(new ClearSiteDataHeaderWriter(SOURCE)))
                .logoutSuccessHandler(getLogoutHandler());

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web -> web.ignoring()
                .antMatchers("/h2-console/**")
                .antMatchers("/static/**")
                .antMatchers("/css/**")
                .antMatchers("/js/**")
                .antMatchers("/images/**"));
    }
}
