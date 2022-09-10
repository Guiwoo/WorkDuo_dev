package com.workduo.configuration.security;

import com.workduo.configuration.jwt.JwtAuthenticationFilter;
import com.workduo.configuration.security.error.CustomNotAuthentication;
import com.workduo.configuration.security.error.CustomNotAuthorization;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
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

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration {
    private final JwtAuthenticationFilter authenticationFilter;


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

        http
                .httpBasic().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http
                .headers().frameOptions().sameOrigin().and()
                .csrf().ignoringAntMatchers("/h2-console/**").disable();

        http.exceptionHandling().authenticationEntryPoint(new CustomNotAuthentication())
                        .accessDeniedHandler(new CustomNotAuthorization());


        //접근 누구나 가능
        http.authorizeRequests()
                .antMatchers(
                        "/h2-console/**","/api/v1/member/login","/api/v1/member"
                ).permitAll();

        http.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);


        // 로그인 된 유저(토큰 이 있는),권한 이 있는 접근
        http.authorizeRequests()
                .antMatchers("/api/v1/auth")
                .hasAnyAuthority( "ROLE_MEMBER", "ROLE_ADMIN");

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
