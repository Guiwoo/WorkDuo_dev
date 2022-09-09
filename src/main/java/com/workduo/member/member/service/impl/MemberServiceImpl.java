package com.workduo.member.member.service.impl;

import com.workduo.error.member.exception.MemberException;
import com.workduo.error.member.type.MemberErrorCode;
import com.workduo.member.member.dto.auth.MemberAuthDto;
import com.workduo.member.member.dto.auth.MemberAuthenticateDto;
import com.workduo.member.member.dto.MemberLoginDto;
import com.workduo.member.member.dto.auth.PrincipalDetails;
import com.workduo.member.member.entity.Member;
import com.workduo.member.member.repository.MemberRepository;
import com.workduo.member.member.service.MemberService;
import com.workduo.member.memberrole.entity.MemberRole;
import com.workduo.member.memberrole.repository.MemberRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

import static com.workduo.member.member.type.MemberStatus.*;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final MemberRoleRepository memberRoleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_EMAIL_ERROR));
        List<MemberRole> roles = memberRoleRepository.findByMember(member);
        MemberAuthDto authDto = MemberAuthDto.builder()
                .username(member.getUsername())
                .password(member.getPassword())
                .roles(roles)
                .build();

        return new PrincipalDetails(authDto);
    }

    //authentication 구현 다시
    @Transactional
    public MemberAuthenticateDto authenticateUser(MemberLoginDto.Request member){
        //이메일검증
        Member oM = memberRepository.findByEmail(member.getEmail()).
                orElseThrow(()->new MemberException(MemberErrorCode.MEMBER_EMAIL_ERROR));
        //정지된 회원
        if(oM.getMemberStatus() == MEMBER_STATUS_STOP){
            throw new MemberException(MemberErrorCode.MEMBER_STOP_ERROR);
        }
        // 탈퇴한 회원
        if(oM.getMemberStatus() == MEMBER_STATUS_WITHDRAW){
            throw new MemberException(MemberErrorCode.MEMBER_WITHDRAW_ERROR);
        }
//        // 패스워드검증
//        if (!passwordEncoder.matches(member.getPassword(), oM.getPassword())) {
//            throw new MemberException(MemberErrorCode.MEMBER_PASSWORD_ERROR);
//        }

        List<MemberRole> all = memberRoleRepository.findByMember(oM);

        return MemberAuthenticateDto.builder().email(oM.getEmail()).roles(all).build();
    }
}
