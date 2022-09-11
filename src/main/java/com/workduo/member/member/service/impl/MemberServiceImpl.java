package com.workduo.member.member.service.impl;

import com.workduo.area.siggarea.entity.SiggArea;
import com.workduo.area.siggarea.repository.SiggAreaRepository;
import com.workduo.common.CommonRequestContext;
import com.workduo.error.member.exception.MemberException;
import com.workduo.error.member.type.MemberErrorCode;
import com.workduo.member.area.entity.MemberActiveArea;
import com.workduo.member.area.repository.MemberActiveAreaRepository;
import com.workduo.member.existmember.entity.ExistMember;
import com.workduo.member.existmember.repository.ExistMemberRepository;
import com.workduo.member.interestedsport.entity.MemberInterestedSport;
import com.workduo.member.interestedsport.repository.InterestedSportRepository;
import com.workduo.member.member.dto.MemberCreate;
import com.workduo.member.member.dto.MemberEdit;
import com.workduo.member.member.dto.MemberLogin;
import com.workduo.member.member.dto.auth.MemberAuthDto;
import com.workduo.member.member.dto.auth.MemberAuthenticateDto;
import com.workduo.member.member.dto.auth.PrincipalDetails;
import com.workduo.member.member.entity.Member;
import com.workduo.member.member.repository.MemberRepository;
import com.workduo.member.member.service.MemberService;
import com.workduo.member.memberrole.entity.MemberRole;
import com.workduo.member.memberrole.repository.MemberRoleRepository;
import com.workduo.member.memberrole.type.MemberRoleType;
import com.workduo.sport.sport.entity.Sport;
import com.workduo.sport.sport.repository.SportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.workduo.member.member.type.MemberStatus.MEMBER_STATUS_STOP;
import static com.workduo.member.member.type.MemberStatus.MEMBER_STATUS_WITHDRAW;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final ExistMemberRepository existMemberRepository;
    private final MemberRoleRepository memberRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final CommonRequestContext commonRequestContext;
    private final MemberActiveAreaRepository memberActiveAreaRepository;
    private final SiggAreaRepository siggAreaRepository;
    private final InterestedSportRepository interestedSportRepository;
    private final SportRepository sportRepository;

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
        // Need to add aws s3 img file path ,or default path should be added
        Member m = MemberCreate.createReqToMember(create);
        m.setPassword(passwordEncoder.encode(create.getPassword()));
        memberRepository.save(m);
        // 롤 저장
        MemberRole r = MemberCreate.createReqToMemberRole(m,MemberRoleType.ROLE_MEMBER);
        memberRoleRepository.save(r);
        // 이메일 테이블 저장
        ExistMember e = MemberCreate.createReqToExistMember(create.getEmail());
        existMemberRepository.save(e);
        //시도 저장
        saveActiveArea(m,create.getSiggAreaList());
        //운동 저장
        saveInterestedSport(m,create.getSportList());
    }

    @Override
    public void editUser(MemberEdit.Request edit) {
        Member m = memberRepository.findByEmail(commonRequestContext.getMemberEmail())
                .orElseThrow(()->new MemberException(MemberErrorCode.MEMBER_EMAIL_ERROR));
        if(!Objects.equals(commonRequestContext.getMemberEmail(), m.getEmail())){
            throw new MemberException(MemberErrorCode.MEMBER_ERROR_NEED_LOGIN);
        }
        validationEditDate(edit,m);
        MemberEdit.editReqToUpdateMember(edit,m);
        memberRepository.save(m);
        //지역 변경 해줘야함 테이블에서 찾아서 지울꺼 지우고 업데이트할꺼 하고
        updateActiveArea(m,edit.getSiggAreaList());
        //운동 변경
        updateInterestedSport(m,edit.getSportList());
    }

    private void saveActiveArea(Member m, List<String> siggList){
        siggList.forEach(
                (id)->{
                    MemberActiveArea a = MemberActiveArea.builder()
                            .siggArea(getSiggArea(id))
                            .member(m)
                            .build();
                    memberActiveAreaRepository.save(a);
                }
        );
    }
    private void updateActiveArea(Member m,List<String> siggList){
        List<MemberActiveArea> active = memberActiveAreaRepository.findAllByMember(m);
        List<String> activeList = new ArrayList<>();
        active.forEach(
                (act)-> {
                    activeList.add(act.getSiggArea().getSgg());
                    if(!siggList.contains(act.getSiggArea().getSgg())){
                        memberActiveAreaRepository.delete(act);
                    }
                }
        );
        siggList.forEach(
                (sigg)->{
                    if(!activeList.contains(sigg)){
                        MemberActiveArea a = MemberActiveArea.builder()
                                .member(m)
                                .siggArea(getSiggArea(sigg))
                                .build();
                        memberActiveAreaRepository.save(a);
                    }
                }
        );
    }
    private void saveInterestedSport(Member m, List<Integer> sportList){
        sportList.forEach(
                (id)->{
                    MemberInterestedSport i = MemberInterestedSport.builder()
                            .member(m)
                            .sport(getSport(id))
                            .build();
                    interestedSportRepository.save(i);
                }
        );
    }
    private void updateInterestedSport(Member m, List<Integer> sportList){
        List<MemberInterestedSport> active = interestedSportRepository.findAllByMember(m);
        List<Integer> activeList = new ArrayList<>();
        active.forEach(
                (act)-> {
                    activeList.add(act.getSport().getId());
                    if(!sportList.contains(act.getSport().getId())){
                        interestedSportRepository.delete(act);
                    }
                }
        );
        sportList.forEach(
                (id)->{
                    if(!activeList.contains(id)){
                        com.workduo.member.interestedsport.entity.MemberInterestedSport i = com.workduo.member.interestedsport.entity.MemberInterestedSport.builder()
                                .member(m)
                                .sport(getSport(id))
                                .build();
                        interestedSportRepository.save(i);
                    }
                }
        );
    }
    private Sport getSport(int id){
        return sportRepository.findById(id)
                .orElseThrow(()->new MemberException(MemberErrorCode.MEMBER_SPORT_ERROR));
    }
    private SiggArea getSiggArea(String id){
        return siggAreaRepository.findBySgg(id)
                .orElseThrow(()->new MemberException(MemberErrorCode.MEMBER_SIGG_ERROR));
    }
    private boolean sportsCheck(List<Integer> sportList){
        if(sportList == null) return false;
        for (int integer : sportList) {
            if(!sportRepository.existsById(integer)) return false;
        }
        return true;
    }
    private boolean siggCheck(List<String> siggCheck){
        if(siggCheck == null) return false;
        for (String sgg : siggCheck) {
            if(!siggAreaRepository.existsBySgg(sgg)) return false;
        }
        return true;
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
    private void validationEditDate(MemberEdit.Request edit,Member m){
        if(!edit.getNickname().equals(m.getNickname())
                && nickNameDuplicateCheck(edit.getNickname())){
            throw new MemberException(MemberErrorCode.MEMBER_NICKNAME_DUPLICATE);
        }
        if(!edit.getPhoneNumber().equals(m.getPhoneNumber())
                && phoneNumberDuplicateCheck(edit.getPhoneNumber())){
            throw new MemberException(MemberErrorCode.MEMBER_PHONE_DUPLICATE);
        }
        if(!siggCheck(edit.getSiggAreaList())){
            throw new MemberException(MemberErrorCode.MEMBER_SIGG_ERROR);
        }
        if(!sportsCheck(edit.getSportList())){
            throw new MemberException(MemberErrorCode.MEMBER_SPORT_ERROR);
        }
    }
    private boolean passwordPolicyCheck(String password){
        final String reg = "^(?=.*[0-9])(?=.*[a-zA-z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$";
        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(password);
        return m.matches();
    }
    private boolean phoneNumberDuplicateCheck(String phoneNumber){
        //01012341234 형태로 들어올시
        return memberRepository.existsByPhoneNumber(phoneNumber);
    }
    private boolean nickNameDuplicateCheck(String nickname){
        return memberRepository.existsByNickname(nickname);
    }
    private boolean emailDuplicateCheck(String email){
        final String reg = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(email);
        if(!m.matches()){
            throw new MemberException(MemberErrorCode.MEMBER_EMAIL_FORM);
        };
        return existMemberRepository.existsByMemberEmail(email);
    }
}