package com.workduo.member.member.service.impl;

import com.workduo.area.siggarea.repository.SiggAreaRepository;
import com.workduo.error.member.exception.MemberException;
import com.workduo.error.member.type.MemberErrorCode;
import com.workduo.member.existmember.entity.ExistMember;
import com.workduo.member.existmember.repository.ExistMemberRepository;
import com.workduo.member.member.dto.MemberCreate;
import com.workduo.member.member.dto.MemberLogin;
import com.workduo.member.member.dto.auth.MemberAuthDto;
import com.workduo.member.member.dto.auth.MemberAuthenticateDto;
import com.workduo.member.member.dto.auth.PrincipalDetails;
import com.workduo.member.member.entity.Member;
import com.workduo.member.member.repository.MemberRepository;
import com.workduo.member.member.service.MemberService;
import com.workduo.member.memberrole.entity.MemberRole;
import com.workduo.member.memberrole.repository.MemberRoleRepository;
import com.workduo.sport.sport.repository.SportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.workduo.member.member.type.MemberStatus.*;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final ExistMemberRepository existMemberRepository;
    private final MemberRoleRepository memberRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final SportRepository sportRepository;
    private final SiggAreaRepository siggAreaRepository;

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

    @Transactional(readOnly = true)
    public MemberAuthenticateDto authenticateUser(MemberLogin.Request member){
        Member oM = memberRepository.findByEmail(member.getEmail()).
                orElseThrow(()->new MemberException(MemberErrorCode.MEMBER_EMAIL_ERROR));
        if(oM.getMemberStatus() == MEMBER_STATUS_STOP){
            throw new MemberException(MemberErrorCode.MEMBER_STOP_ERROR);
        }
        if(oM.getMemberStatus() == MEMBER_STATUS_WITHDRAW){
            throw new MemberException(MemberErrorCode.MEMBER_WITHDRAW_ERROR);
        }
        if (!passwordEncoder.matches(member.getPassword(), oM.getPassword())) {
            throw new MemberException(MemberErrorCode.MEMBER_PASSWORD_ERROR);
        }

        List<MemberRole> all = memberRoleRepository.findByMember(oM);

        return MemberAuthenticateDto.builder().email(oM.getEmail()).roles(all).build();
    }

    @Override
    public void createUser(MemberCreate.Request create) {
        validationCreateData(create);
        // For Member Table
        Member m = Member.builder()
                .email(create.getEmail())
                .password(passwordEncoder.encode(create.getPassword()))
                .username(create.getUsername())
                .nickname(create.getNickname())
                .phoneNumber(create.getPhoneNumber())
                .memberStatus(MEMBER_STATUS_ING)
                .build();
        memberRepository.save(m);

        // For Exist Member Table
        ExistMember e = ExistMember.builder()
                .memberEmail(create.getEmail())
                .build();
        existMemberRepository.save(e);

    }
    private void  validationCreateData(MemberCreate.Request create) {
        if(emailDuplicateCheck(create.getEmail())){
            throw new MemberException(MemberErrorCode.MEMBER_EMAIL_DUPLICATE);
        }
        if(nickNameDuplicateCheck(create.getNickname())){
            throw new MemberException(MemberErrorCode.MEMBER_NICKNAME_DUPLICATE);
        }
        if(phoneNumberDuplicateCheck(create.getPhoneNumber())){
            throw new MemberException(MemberErrorCode.MEMBER_PHONE_DUPLICATE);
        }
        if(!passwordPolicyCheck(create.getPassword())){
            throw new MemberException(MemberErrorCode.MEMBER_PASSWORD_POLICY);
        }
        if(!siggCheck(create.getSiggAreaList())){
            throw new MemberException(MemberErrorCode.MEMBER_SIGG_ERROR);
        }
        if(!sportsCheck(create.getSportList())){
            throw new MemberException(MemberErrorCode.MEMBER_SPORT_ERROR);
        }
    }

    public boolean sportsCheck(List<Integer> sportList){
        if(sportList == null) return false;
        for (int integer : sportList) {
            if(!sportRepository.existsById(integer)) return false;
        }
        return true;
    }
    public boolean siggCheck(List<Integer> siggCheck){
        if(siggCheck == null) return false;
        for (int integer : siggCheck) {
            if(!siggAreaRepository.existsById(integer)) return false;
        }
        return true;
    }
    public boolean passwordPolicyCheck(String password){
        final String reg = "^(?=.*[0-9])(?=.*[a-zA-z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$";
        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(password);
        return m.matches();
    }
    public boolean phoneNumberDuplicateCheck(String phoneNumber){
        //01012341234 형태로 들어올시
        return memberRepository.existsByPhoneNumber(phoneNumber);
    }
    public boolean nickNameDuplicateCheck(String nickname){
        return memberRepository.existsByNickname(nickname);
    }
    public boolean emailDuplicateCheck(String email){
        final String reg = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(email);
        if(!m.matches()){
            throw new MemberException(MemberErrorCode.MEMBER_EMAIL_FORM);
        };
        return existMemberRepository.existsByMemberEmail(email);
    }
}
