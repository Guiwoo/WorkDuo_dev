package com.workduo.member.member.service.impl;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.workduo.area.siggarea.entity.SiggArea;
import com.workduo.area.siggarea.repository.SiggAreaRepository;
import com.workduo.common.CommonRequestContext;
import com.workduo.configuration.jwt.memberrefreshtoken.repository.MemberRefreshTokenRepository;
import com.workduo.error.member.exception.MemberException;
import com.workduo.group.group.repository.GroupJoinMemberRepository;
import com.workduo.group.group.service.GroupService;
import com.workduo.group.groupmetting.repository.GroupMeetingParticipantRepository;
import com.workduo.member.content.repository.query.MemberContentQueryRepository;
import com.workduo.member.content.repository.query.impl.MemberContentQueryRepositoryImpl;
import com.workduo.member.member.dto.MemberChangePassword;
import com.workduo.member.member.dto.MemberCreate;
import com.workduo.member.member.dto.MemberEdit;
import com.workduo.member.member.dto.MemberLogin;
import com.workduo.member.member.entity.Member;
import com.workduo.member.member.repository.*;
import com.workduo.member.member.type.MemberStatus;
import com.workduo.member.membercalendar.repository.MemberCalendarRepository;
import com.workduo.sport.sport.entity.Sport;
import com.workduo.sport.sport.repository.SportRepository;
import com.workduo.util.AwsS3Provider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.awt.print.Pageable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.workduo.error.member.type.MemberErrorCode.*;
import static com.workduo.member.member.type.MemberStatus.MEMBER_STATUS_STOP;
import static com.workduo.member.member.type.MemberStatus.MEMBER_STATUS_WITHDRAW;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("MEMBER SERVICE 테스트")
class MemberServiceImplTest {
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private ExistMemberRepository existMemberRepository;
    @Mock
    private MemberRoleRepository memberRoleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private CommonRequestContext commonRequestContext;
    @Mock
    private MemberActiveAreaRepository memberActiveAreaRepository;
    @Mock
    private SiggAreaRepository siggAreaRepository;
    @Mock
    private InterestedSportRepository interestedSportRepository;
    @Mock
    private AwsS3Provider awsS3Provider;
    @Mock
    private SportRepository sportRepository;
    @Mock
    private GroupService groupService;
    @Mock
    private MemberRefreshTokenRepository memberRefreshTokenRepository;
    @Mock
    private MemberCalendarRepository memberCalendarRepository;
    @Mock
    private GroupJoinMemberRepository groupJoinMemberRepository;
    @Mock
    private GroupMeetingParticipantRepository groupMeetingParticipantRepository;
    @Mock
    private MemberContentQueryRepositoryImpl memberContentQueryRepository;
    @InjectMocks
    MemberServiceImpl memberService;
    @Nested
    @DisplayName("로그인 메서드 테스트")
    class TestLoginMethod{
        @Test
        @DisplayName("유저 검증 [메일이 없는경우]")
        void failAuthenticateDoesNotExistEmail(){
            MemberLogin.Request req = MemberLogin.Request.builder()
                    .email("abc")
                    .password("something")
                    .build();

            //when
            MemberException exception =  assertThrows(MemberException.class,
                    ()->memberService.authenticateUser(req));
            //then
            assertEquals(MEMBER_EMAIL_ERROR,exception.getErrorCode());
        }

        @Test
        @DisplayName("유저 검증 [정지된 회원]")
        void failAuthenticateBannedUser(){
            MemberLogin.Request req = MemberLogin.Request.builder()
                    .email("abc")
                    .password("something")
                    .build();
            //given
            given(memberRepository.findByEmail(any())).willReturn(
                    Optional.of(Member.builder().memberStatus(MEMBER_STATUS_STOP).build())
            );
            //when
            MemberException exception =  assertThrows(MemberException.class,
                    ()->memberService.authenticateUser(req));
            //then
            assertEquals(MEMBER_STOP_ERROR,exception.getErrorCode());
        }

        @Test
        @DisplayName("유저 검증 [탈퇴한 회원]")
        void failAuthenticateWithdrawUser(){
            MemberLogin.Request req = MemberLogin.Request.builder()
                    .email("abc")
                    .password("something")
                    .build();
            //given
            given(memberRepository.findByEmail(any())).willReturn(
                    Optional.of(Member.builder().memberStatus(MemberStatus.MEMBER_STATUS_WITHDRAW).build())
            );
            //when
            MemberException exception =  assertThrows(MemberException.class,
                    ()->memberService.authenticateUser(req));
            //then
            assertEquals(MEMBER_WITHDRAW_ERROR,exception.getErrorCode());
        }

        @Test
        @DisplayName("유저 검증 [비밀번호가 다름]")
        void failAuthenticatePasswordDifferent(){
            MemberLogin.Request req = MemberLogin.Request.builder()
                    .email("test@test.com")
                    .password("something")
                    .build();
            //given
            given(memberRepository.findByEmail(any())).willReturn(
                    Optional.of(Member.builder().memberStatus(MemberStatus.MEMBER_STATUS_ING).build())
            );
            given(passwordEncoder.matches(any(),any())).willReturn(false);
            //when
            MemberException exception =  assertThrows(MemberException.class,
                    ()->memberService.authenticateUser(req));
            //then
            assertEquals(MEMBER_PASSWORD_ERROR,exception.getErrorCode());
        }

        @Test
        @DisplayName("유저 롤 받아오기")
        void successGetMemberRole(){
            MemberLogin.Request req = MemberLogin.Request.builder()
                    .email("test@test.com")
                    .password("something")
                    .build();
            Member m = Member.builder().memberStatus(MemberStatus.MEMBER_STATUS_ING).build();
            //given
            given(memberRepository.findByEmail(any())).willReturn(
                    Optional.of(m)
            );
            given(passwordEncoder.matches(any(),any())).willReturn(true);
            memberService.authenticateUser(req);
            verify(memberRoleRepository,times(1)).findByMember(m);
        }
    }

    @Nested
    @DisplayName("회원가입 메서드 테스트")
    class TestSignInMethod{
        List<String> sggList = new ArrayList<>(List.of("1"));
        List<Integer> sportList = new ArrayList<>(List.of(1));
        MemberCreate.Request createReqeust = MemberCreate.Request
                .builder()
                .email("test@test.com")
                .password("12345abc@")
                .username("test")
                .phoneNumber("1")
                .siggAreaList(sggList)
                .nickname("feelingGood")
                .sportList(sportList)
                .build();

        @Test
        @DisplayName("회원가입 실패[이메일 폼 형태가 아닌경우]")
        void emailFormCheckFail(){
            createReqeust.setEmail("amIEmail?");
            //when
            MemberException exception = assertThrows(
                    MemberException.class,
                    ()->memberService.createUser(createReqeust)
            );
            //then
            assertEquals(MEMBER_EMAIL_FORM,exception.getErrorCode());
        }

        @Test
        @DisplayName("회원가입 실패[이메일 중복인경우]")
        void emailDuplicateCheckFail(){
            //given
            doReturn(true).when(existMemberRepository).existsByMemberEmail(any());
            //when
            MemberException exception = assertThrows(
                    MemberException.class,
                    ()->memberService.createUser(createReqeust)
            );
            //then
            assertEquals(MEMBER_EMAIL_DUPLICATE,exception.getErrorCode());
        }

        @Test
        @DisplayName("회원가입 실패[닉네임 중복인경우]")
        void nicknameDuplicateCheckFail(){
            doReturn(true).when(memberRepository).existsByNickname(any());
            //when
            MemberException exception = assertThrows(
                    MemberException.class,
                    ()->memberService.createUser(createReqeust)
            );
            //then
            assertEquals(MEMBER_NICKNAME_DUPLICATE,exception.getErrorCode());
        }
        @Test
        @DisplayName("회원가입 실패[전화번호 중복인경우]")
        void mobileDuplicateCheckFail(){
            doReturn(true).when(memberRepository).existsByPhoneNumber(any());
            //when
            MemberException exception = assertThrows(
                    MemberException.class,
                    ()->memberService.createUser(createReqeust)
            );
            //then
            assertEquals(MEMBER_PHONE_DUPLICATE,exception.getErrorCode());
        }

        @Test
        @DisplayName("회원가입 실패[비밀번호 정책 위반] [패스워드 길이]")
        void passwordPolicyCheckFail(){
            createReqeust.setPassword("1");
            //when
            MemberException exception = assertThrows(
                    MemberException.class,
                    ()->memberService.createUser(createReqeust)
            );
            //then
            assertEquals(MEMBER_PASSWORD_POLICY,exception.getErrorCode());
        }

        @Test
        @DisplayName("회원가입 실패[비밀번호 정책 위반] [문자 만 있는경우]")
        void passwordPolicyCheckFailOnlyLetters(){
            createReqeust.setPassword("abcabcabc");
            //when
            MemberException exception = assertThrows(
                    MemberException.class,
                    ()->memberService.createUser(createReqeust)
            );
            //then
            assertEquals(MEMBER_PASSWORD_POLICY,exception.getErrorCode());
        }

        @Test
        @DisplayName("회원가입 실패[비밀번호 정책 위반] [숫자 만 있는경우]")
        void passwordPolicyCheckFailOnlyNumbers(){
            createReqeust.setPassword("1231231123");
            //when
            MemberException exception = assertThrows(
                    MemberException.class,
                    ()->memberService.createUser(createReqeust)
            );
            //then
            assertEquals(MEMBER_PASSWORD_POLICY,exception.getErrorCode());
        }

        @Test
        @DisplayName("회원가입 실패[비밀번호 정책 위반] [특수문자가 없는 경우]")
        void passwordPolicyCheckFailWithoutSpecialCharacter(){
            createReqeust.setPassword("abccsdfasbc");
            //when
            MemberException exception = assertThrows(
                    MemberException.class,
                    ()->memberService.createUser(createReqeust)
            );
            //then
            assertEquals(MEMBER_PASSWORD_POLICY,exception.getErrorCode());
        }

        @Test
        @DisplayName("회원가입 실패[지역 이 데이터 상에 없는경우]")
        void siggCheckFail(){
            given(siggAreaRepository.existsBySgg(any())).willReturn(false);
            //when
            MemberException exception = assertThrows(
                    MemberException.class,
                    ()->memberService.createUser(createReqeust)
            );
            //then
            assertEquals(MEMBER_SIGG_ERROR,exception.getErrorCode());
        }

        @Test
        @DisplayName("회원가입 실패[운동 이 데이터 상에 없는경우]")
        void sportCheckFail(){
            given(siggAreaRepository.existsBySgg(any())).willReturn(true);
            //when
            MemberException exception = assertThrows(
                    MemberException.class,
                    ()->memberService.createUser(createReqeust)
            );
            //then
            assertEquals(MEMBER_SPORT_ERROR,exception.getErrorCode());
        }

        @Test
        @DisplayName("회원가입 성공")
        void successSignIn(){
            given(siggAreaRepository.existsBySgg(any())).willReturn(true);
            given(siggAreaRepository.findBySgg(any())).willReturn(Optional.of(SiggArea.builder().build()));
            given(sportRepository.existsById(any())).willReturn(true);
            given(sportRepository.findById(any())).willReturn(Optional.of(Sport.builder().build()));
            //when
            memberService.createUser(createReqeust);
            //then
            verify(memberRepository,times(1)).save(any());
            verify(existMemberRepository,times(1)).save(any());
        }
    }

    @Nested
    @DisplayName("회원정보 수정 메서드 테스트")
    class TestEditMethod{
        List<String> sggList = new ArrayList<>(List.of("1"));
        List<Integer> sportList = new ArrayList<>(List.of(1));
        MemberEdit.Request editRequest = MemberEdit.Request
                .builder()
                .username("test")
                .phoneNumber("1")
                .siggAreaList(sggList)
                .nickname("feelingGood")
                .sportList(sportList)
                .build();

        @Test
        @DisplayName("회원정보 수정 실패[이메일 이 없는경우]")
        void emailDoesNotExist(){
            given(commonRequestContext.getMemberEmail()).willReturn("");
            //when
            MemberException exception = assertThrows(
                    MemberException.class,
                    ()-> memberService.editUser(editRequest)
            );
            //then
            assertEquals(MEMBER_EMAIL_ERROR,exception.getErrorCode());
        }

        @Test
        @DisplayName("회원정보 수정 실패[토큰 과 이메일 정보 다른경우]")
        void tokenMailDoesNotEqualMemberEmail(){
            Member m = Member.builder().build();
            doReturn(Optional.of(m)).when(memberRepository).findByEmail(any());
            given(commonRequestContext.getMemberEmail()).willReturn("");
            //when
            MemberException exception = assertThrows(
                    MemberException.class,
                    ()-> memberService.editUser(editRequest)
            );
            //then
            assertEquals(MEMBER_ERROR_NEED_LOGIN,exception.getErrorCode());
        }
        @Test
        @DisplayName("회원정보 수정 실패[닉네임 중복된 경우]")
        void nicknameDuplicateCheckFail(){
            Member m = Member.builder().email("test").build();
            doReturn(Optional.of(m)).when(memberRepository).findByEmail(any());
            given(commonRequestContext.getMemberEmail()).willReturn("test");
            doReturn(true).when(memberRepository).existsByNickname(any());
            //when
            MemberException exception = assertThrows(
                    MemberException.class,
                    ()-> memberService.editUser(editRequest)
            );
            //then
            assertEquals(MEMBER_NICKNAME_DUPLICATE,exception.getErrorCode());
        }

        @Test
        @DisplayName("회원정보 수정 실패[전화번호 중복된 경우]")
        void phoneNumberDuplicateCheckFail(){
            Member m = Member.builder().email("test").build();
            doReturn(Optional.of(m)).when(memberRepository).findByEmail(any());
            given(commonRequestContext.getMemberEmail()).willReturn("test");
            doReturn(true).when(memberRepository).existsByPhoneNumber(any());
            //when
            MemberException exception = assertThrows(
                    MemberException.class,
                    ()-> memberService.editUser(editRequest)
            );
            //then
            assertEquals(MEMBER_PHONE_DUPLICATE,exception.getErrorCode());
        }

        @Test
        @DisplayName("회원정보 수정 실패[수정 지역이 데이터에 없는 경우]")
        void doesNotExistSiggData(){
            Member m = Member.builder().email("test").build();
            doReturn(Optional.of(m)).when(memberRepository).findByEmail(any());
            given(commonRequestContext.getMemberEmail()).willReturn("test");
            given(siggAreaRepository.existsBySgg(any())).willReturn(false);
            //when
            MemberException exception = assertThrows(
                    MemberException.class,
                    ()-> memberService.editUser(editRequest)
            );
            //then
            assertEquals(MEMBER_SIGG_ERROR,exception.getErrorCode());
        }

        @Test
        @DisplayName("회원정보 수정 실패[수정 운동이 데이터에 없는 경우]")
        void doesNotExistSportData(){
            Member m = Member.builder().email("test").build();
            doReturn(Optional.of(m)).when(memberRepository).findByEmail(any());
            given(commonRequestContext.getMemberEmail()).willReturn("test");
            given(siggAreaRepository.existsBySgg(any())).willReturn(true);

            //when
            MemberException exception = assertThrows(
                    MemberException.class,
                    ()-> memberService.editUser(editRequest)
            );
            //then
            assertEquals(MEMBER_SPORT_ERROR,exception.getErrorCode());
        }

        @Test
        @DisplayName("회원정보 수정 성공")
        void successEditProfile(){
            Member m = Member.builder().profileImg("aws.com/hehe/i/updated ?").email("test").build();
            doReturn(Optional.of(m)).when(memberRepository).findByEmail(any());
            given(commonRequestContext.getMemberEmail()).willReturn("test");
            given(siggAreaRepository.existsBySgg(any())).willReturn(true);
            given(siggAreaRepository.findBySgg(any())).willReturn(Optional.of(SiggArea.builder().build()));
            given(sportRepository.existsById(any())).willReturn(true);
            given(sportRepository.findById(any())).willReturn(Optional.of(Sport.builder().build()));
//            given(awsS3Provider.deleteFile(any())).willReturn(true);
//            given(awsS3Provider.uploadFile(any(),any()))
//                    .willReturn(List.of("hehe/i/updated ?"));

            memberService.editUser(editRequest);
            //then
            verify(sportRepository,times(sportList.size())).findById(any());
            verify(siggAreaRepository,times(sggList.size())).findBySgg(any());
//            verify(awsS3Provider,times(1)).uploadFile(any(), any());
        }
    }
    @Nested
    @DisplayName("이미지 업데이트 테스트")
    class testImgUpdate{

        MockMultipartFile image = new MockMultipartFile(
                "multipartFiles",
                "imagefile.jpeg",
                "image/jpeg",
                "<<jpeg data>>".getBytes()
        );

        @Test
        @DisplayName("이미지 업데이트 실패[aws s3 업로드 실패시 ]")
        void failUploadToS3(){
            Member m = Member.builder().profileImg("aws.com/hehe/i/updated ?").email("test").build();
            doReturn(Optional.of(m)).when(memberRepository).findByEmail(any());
            given(commonRequestContext.getMemberEmail()).willReturn("test");

            //when
            AmazonS3Exception exception = assertThrows(
                    AmazonS3Exception.class,
                    ()-> memberService.updateImage(image)
            );
            //then
            assertEquals("파일 업로드에 실패하였습니다.",exception.getErrorMessage());
        }

        @Test
        @DisplayName("회원정보 수정 실패[aws s3 업로드 실패시 ]")
        void successUploadToS3(){
            Member m = Member.builder().profileImg("aws.com/hehe/i/updated ?").email("test").build();
            doReturn(Optional.of(m)).when(memberRepository).findByEmail(any());
            given(commonRequestContext.getMemberEmail()).willReturn("test");
            given(awsS3Provider.deleteFile(any())).willReturn(true);
            given(awsS3Provider.uploadFile(any(),any()))
                    .willReturn(List.of("hehe/i/updated ?"));

            memberService.updateImage(image);
            //then
            verify(awsS3Provider,times(1)).uploadFile(any(), any());
        }

    }
    @Nested
    @DisplayName("비밀번호 수정 메서드 테스트")
    class updatePassword{
        MemberChangePassword.Request req = MemberChangePassword.Request
                .builder()
                .password("123")
                .build();
        @Test
        @DisplayName("비밀번호 수정 실패 [토큰 과 이메일 정보 다른경우]")
        void failChangePasswordTokenDiff(){
            Member m = Member.builder().build();
            doReturn(Optional.of(m)).when(memberRepository).findByEmail(any());
            given(commonRequestContext.getMemberEmail()).willReturn("");
            //when
            MemberException exception = assertThrows(
                    MemberException.class,
                    ()-> memberService.changePassword(req)
            );
            //then
            assertEquals(MEMBER_ERROR_NEED_LOGIN,exception.getErrorCode());
        }

        @Test
        @DisplayName("비밀번호 수정 실패 [이전 비밀번호 와 동일한 경우]")
        void failChangePasswordServeSamePassword(){
            Member m = Member.builder().email("test").build();
            doReturn(Optional.of(m)).when(memberRepository).findByEmail(any());
            given(commonRequestContext.getMemberEmail()).willReturn("test");
            given(passwordEncoder.matches(any(),any())).willReturn(true);
            //when
            MemberException exception = assertThrows(
                    MemberException.class,
                    ()-> memberService.changePassword(req)
            );
            //then
            assertEquals(MEMBER_PASSWORD_DUPLICATE,exception.getErrorCode());
        }


        @Test
        @DisplayName("회원가입 실패[비밀번호 정책 위반] [패스워드 길이]")
        void passwordPolicyCheckFail(){
            Member m = Member.builder().email("test").build();
            doReturn(Optional.of(m)).when(memberRepository).findByEmail(any());
            given(commonRequestContext.getMemberEmail()).willReturn("test");
            //when
            MemberException exception = assertThrows(
                    MemberException.class,
                    ()->memberService.changePassword(req)
            );
            //then
            assertEquals(MEMBER_PASSWORD_POLICY,exception.getErrorCode());
        }

        @Test
        @DisplayName("비밀번호 수정 실패[비밀번호 정책 위반] [문자 만 있는경우]")
        void passwordPolicyCheckFailOnlyLetters(){
            req.setPassword("aaaaaaaaaaa");
            Member m = Member.builder().email("test").build();
            doReturn(Optional.of(m)).when(memberRepository).findByEmail(any());
            given(commonRequestContext.getMemberEmail()).willReturn("test");
            //when
            MemberException exception = assertThrows(
                    MemberException.class,
                    ()->memberService.changePassword(req)
            );
            //then
            assertEquals(MEMBER_PASSWORD_POLICY,exception.getErrorCode());
        }

        @Test
        @DisplayName("비밀번호 수정 실패[비밀번호 정책 위반] [숫자 만 있는경우]")
        void passwordPolicyCheckFailOnlyNumbers(){
            req.setPassword("123123123123");
            Member m = Member.builder().email("test").build();
            doReturn(Optional.of(m)).when(memberRepository).findByEmail(any());
            given(commonRequestContext.getMemberEmail()).willReturn("test");
            //when
            MemberException exception = assertThrows(
                    MemberException.class,
                    ()->memberService.changePassword(req)
            );
            //then
            assertEquals(MEMBER_PASSWORD_POLICY,exception.getErrorCode());
        }

        @Test
        @DisplayName("비밀번호 수정 실패[비밀번호 정책 위반] [특수문자가 없는 경우]")
        void passwordPolicyCheckFailWithoutSpecialCharacter(){
            req.setPassword("abcd1234");
            Member m = Member.builder().email("test").build();
            doReturn(Optional.of(m)).when(memberRepository).findByEmail(any());
            given(commonRequestContext.getMemberEmail()).willReturn("test");
            //when
            MemberException exception = assertThrows(
                    MemberException.class,
                    ()->memberService.changePassword(req)
            );
            //then
            assertEquals(MEMBER_PASSWORD_POLICY,exception.getErrorCode());
        }

        @Test
        @DisplayName("비밀번호 성공")
        void successChangePassword(){
            req.setPassword("abcd1234@");
            Member m = Member.builder().email("test").build();
            doReturn(Optional.of(m)).when(memberRepository).findByEmail(any());
            given(commonRequestContext.getMemberEmail()).willReturn("test");
            //when
            memberService.changePassword(req);
            //then
            verify(passwordEncoder,times(1)).matches(any(),any());
            verify(passwordEncoder,times(1)).encode(any());
        }
    }

    @Nested
    @DisplayName("회원탈퇴 매서드 테스트")
    class withdraw{
        @Test
        @DisplayName("회원탈퇴 실패 [토큰과 이메일 정보 다른경우]")
        void failToWithdrawDiffEmail(){
            Member m = Member.builder().build();
            doReturn(Optional.of(m)).when(memberRepository).findByEmail(any());
            given(commonRequestContext.getMemberEmail()).willReturn("");
            //when
            MemberException exception = assertThrows(
                    MemberException.class,
                    ()-> memberService.withdraw(groupService)
            );
            //then
            assertEquals(MEMBER_ERROR_NEED_LOGIN,exception.getErrorCode());
        }
        @Test
        @DisplayName("회원탈퇴 실패 [이미 정지된 회원]")
        void failToWithdrawAlreadyStop(){
            //given
            Member m = Member.builder()
                    .memberStatus(MEMBER_STATUS_STOP)
                    .email("abc")
                    .build();
            doReturn(Optional.of(m)).when(memberRepository).findByEmail(any());
            given(commonRequestContext.getMemberEmail()).willReturn("abc");
            //when
            MemberException exception = assertThrows(
                    MemberException.class,
                    ()-> memberService.withdraw(groupService)
            );
            //then
            assertEquals(MEMBER_STOP_ERROR,exception.getErrorCode());
        }
        @Test
        @DisplayName("회원탈퇴 실패 [이미 탈퇴 회원]")
        void failToWithdrawAlreadyWithdraw(){
            //given
            Member m = Member.builder()
                    .memberStatus(MEMBER_STATUS_STOP)
                    .email("abc")
                    .build();
            doReturn(Optional.of(m)).when(memberRepository).findByEmail(any());
            given(commonRequestContext.getMemberEmail()).willReturn("abc");
            //when
            MemberException exception = assertThrows(
                    MemberException.class,
                    ()-> memberService.withdraw(groupService)
            );
            //then
            assertEquals(MEMBER_STOP_ERROR,exception.getErrorCode());
        }

        @Test
        @DisplayName("회원 탈퇴 성공")
        void successWithdrawMember(){
            //given
            Member m = Member.builder().email("abc").build();
            doReturn(Optional.of(m)).when(memberRepository).findByEmail(any());
            given(commonRequestContext.getMemberEmail()).willReturn("abc");
            //when
            memberService.withdraw(groupService);
            //then
            verify(memberRefreshTokenRepository,times(1))
                    .deleteById(any());
            verify(existMemberRepository,times(1))
                    .deleteById(any());
            verify(memberCalendarRepository,times(1))
                    .deleteByMember(any());
            verify(groupMeetingParticipantRepository,times(1))
                    .deleteByMember(any());
        }

    }

    @Nested
    @DisplayName("회원정보 받아오기 메서드 테스트")
    class getMember{
        Long memberId = 13L;
        PageRequest pageRequest = PageRequest.of(0, 20);
        @Test
        @DisplayName("회원정보 받아오기 메서드 실패 [존재하지 않는 Id] ")
        public void fail1() throws Exception{
            MemberException exception =  assertThrows(
                    MemberException.class,
                    ()-> memberService.getUser(memberId,pageRequest)
            );
            assertThat(exception.getErrorCode())
                    .isEqualTo(MEMBER_EMAIL_ERROR);
        }

        @Test
        @DisplayName("회원정보 받아오기 메서드 실패 [회원 정지된 경우] ")
        public void fail2() throws Exception{
            Member build = Member.builder()
                    .memberStatus(MEMBER_STATUS_STOP)
                    .build();
            given(memberRepository.findByEmail(any()))
                    .willReturn(Optional.of(build));

            MemberException exception =  assertThrows(
                    MemberException.class,
                    ()-> memberService.getUser(memberId,pageRequest)
            );
            assertThat(exception.getErrorCode())
                    .isEqualTo(MEMBER_STOP_ERROR);
        }

        @Test
        @DisplayName("회원정보 받아오기 메서드 실패 [회원 탈퇴한 경우] ")
        public void fail3() throws Exception{
            Member build = Member.builder()
                    .memberStatus(MEMBER_STATUS_WITHDRAW)
                    .build();
            given(memberRepository.findByEmail(any()))
                    .willReturn(Optional.of(build));

            MemberException exception =  assertThrows(
                    MemberException.class,
                    ()-> memberService.getUser(memberId,pageRequest)
            );
            assertThat(exception.getErrorCode())
                    .isEqualTo(MEMBER_WITHDRAW_ERROR);
        }

        @Test
        @DisplayName("회원정보 받아오기 메서드 성공")
        public void success () throws Exception{
            //given
            Member build = Member.builder().build();

            given(memberRepository.findByEmail(any()))
                    .willReturn(Optional.of(build));
            //when
            memberService.getUser(memberId,pageRequest);
            //then
            verify(memberContentQueryRepository,times(1))
                    .getMemberContentList(any(),any());
        }
    }
}