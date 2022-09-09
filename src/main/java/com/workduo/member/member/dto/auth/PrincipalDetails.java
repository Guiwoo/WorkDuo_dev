package com.workduo.member.member.dto.auth;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class PrincipalDetails implements UserDetails {

    private MemberAuthDto memberAuthDto;

    public PrincipalDetails(MemberAuthDto memberAuthDto) {
        this.memberAuthDto = memberAuthDto;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<String> parseRoll =
                this.memberAuthDto.getRoles().stream()
                        .map(role -> role.getRole().toString())
                        .collect(Collectors.toList());

        // SimpleGrantedAuthority 스프링 시큐리티 롤 관련 기능
        return parseRoll.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }


}
