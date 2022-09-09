package com.workduo.member.member.service.impl;

import com.workduo.error.member.exception.MemberException;
import com.workduo.error.member.type.MemberErrorCode;
import com.workduo.member.member.dto.MemberAuthenticateDto;
import com.workduo.member.member.dto.MemberDto;
import com.workduo.member.member.dto.MemberLoginDto;
import com.workduo.member.member.entity.Member;
import com.workduo.member.member.repository.MemberRepository;
import com.workduo.member.member.service.MemberService;
import com.workduo.member.memberrole.entity.MemberRole;
import com.workduo.member.memberrole.repository.MemberRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.workduo.member.member.type.MemberStatus.*;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final MemberRoleRepository memberRoleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }

    @Override
    public MemberDto findMemberByEmail(String email) {
        Member oM = memberRepository.findByEmail(email).
                orElseThrow(()->new MemberException(MemberErrorCode.MEMBER_EMAIL_ERROR));

        return MemberDto.fromEntity(oM);
    }

    //authentication 구현 다시
    public MemberAuthenticateDto authenticateUser(MemberLoginDto.Request member){
        //이메일검증
        Member oM = memberRepository.findByEmail(member.getEmail()).
                orElseThrow(()->new MemberException(MemberErrorCode.MEMBER_EMAIL_ERROR));
        if(oM.getMemberStatus() == MEMBER_STATUS_STOP){
            throw new MemberException(MemberErrorCode.MEMBER_STOP_ERROR);
        }
        if(oM.getMemberStatus() == MEMBER_STATUS_WITHDRAW){
            throw new MemberException(MemberErrorCode.MEMBER_WITHDRAW_ERROR)
        }
        //패스워드검증
        if (!passwordEncoder.matches(member.getPassword(), oM.getPassword())) {
            throw new MemberException(MemberErrorCode.MEMBER_PASSWORD_ERROR);
        }

        //롤도 한번에 다가져와
        List<MemberRole> lists = memberRoleRepository.findAllByMember(oM);
        //다 통과시 보내줘 어떻게 ? 멤버 아아디 이메일 이름 롤 그리고 몰라
        return MemberAuthenticateDto.builder()
                .email(oM.getEmail())
                .roles(lists)
                .build();
    }
}
