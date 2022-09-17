package com.workduo.member.content.service.impl;

import com.workduo.common.CommonRequestContext;
import com.workduo.error.member.exception.MemberException;
import com.workduo.error.member.type.MemberErrorCode;
import com.workduo.member.content.dto.ContentCreate;
import com.workduo.member.content.entity.MemberContent;
import com.workduo.member.content.repository.MemberContentRepository;
import com.workduo.member.contentimage.repository.MemberContentImageRepository;
import com.workduo.member.member.entity.Member;
import com.workduo.member.member.repository.MemberRepository;
import com.workduo.util.AwsS3Utils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MEMBER CONTENT SERVICE 테스트")
class MemberContentServiceImplTest {

    @Mock
    private MemberRepository memberRepository;
    @Mock
    private MemberContentRepository memberContentRepository;
    @Mock
    private MemberContentImageRepository memberContentImageRepository;
    @Mock
    private CommonRequestContext commonRequestContext;
    @Mock
    private EntityManager em;
    @Mock
    private AwsS3Utils awsS3Utils;

    @InjectMocks
    MemberContentServiceImpl memberContentService;

    @Nested
    @DisplayName("멤버 피드 생성 테스트")
    class TestMemberCreate{
        MockMultipartFile img = new MockMultipartFile(
                "multipartFiles",
                "imageFile.jpeg",
                "image/jpeg",
                "<<jpeg data>>".getBytes()
        );

        List<MultipartFile> list = new ArrayList<>(List.of(img));
        ContentCreate.Request req = ContentCreate.Request.builder()
                .title("This is Test")
                .content("Any Problems?")
                .sortValue(0)
                .build();

        @Test
        @DisplayName("멤버 피드 생성 실패[토큰 과 이메일 정보 다른경우]")
        public void tokenMailDoesNotEqualMemberEmail() throws Exception{
            Member m = Member.builder().build();
            doReturn(Optional.of(m)).when(memberRepository).findByEmail(any());
            given(commonRequestContext.getMemberEmail()).willReturn("");
            //when
            MemberException exception = assertThrows(
                    MemberException.class,
                    ()-> memberContentService.createContent(req,list)
            );
            //then
            assertEquals(MemberErrorCode.MEMBER_ERROR_NEED_LOGIN,exception.getErrorCode());
        }

        @Test
        @DisplayName("멤버 피드 생성 성공")
        @WithMockUser
        public void successCreateMemberFeed() throws Exception{
            Member m = Member.builder().email("abc").build();
            doReturn("abc").when(commonRequestContext).getMemberEmail();
            doReturn(Optional.of(m)).when(memberRepository).findByEmail(any());

            doReturn(new ArrayList<String>()).when(awsS3Utils).uploadFile(any(),any());
            doNothing().when(em).flush();
            memberContentService.createContent(req,list);

            verify(memberContentRepository,times(1)).save(any());
            verify(memberContentImageRepository,times(1)).saveAll(any());
        }
    }
}