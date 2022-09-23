package com.workduo.member.content.service.impl;

import com.workduo.common.CommonRequestContext;
import com.workduo.error.member.exception.MemberException;
import com.workduo.error.member.type.MemberErrorCode;
import com.workduo.member.content.dto.*;
import com.workduo.member.content.entity.MemberContent;
import com.workduo.member.content.repository.MemberContentRepository;
import com.workduo.member.content.repository.query.impl.MemberContentQueryRepositoryImpl;
import com.workduo.member.contentimage.repository.MemberContentImageRepository;
import com.workduo.member.member.entity.Member;
import com.workduo.member.member.repository.MemberRepository;
import com.workduo.util.AwsS3Utils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
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
    @Mock
    private MemberContentQueryRepositoryImpl memberContentQueryRepository;

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

    @Nested
    @DisplayName("멤버 피드 리스트 테스트")
    class TestGetMemberContentList{
        // Point !! 로그인 안된 유저 또한 모두 볼수 있어야 함
        // 따라서 성공테스트만 작성 예정
        @Test
        @DisplayName("멤버 피드 생성 성공")
        public void successCreateMemberFeed() throws Exception{
            MemberContentImageDto mcid = MemberContentImageDto.builder()
                    .id(1L)
                    .imagePath("aws/s3/member/content/somewhere")
                    .build();
            MemberContentListDto c = MemberContentListDto.builder()
                    .id(13L)
                    .title("test title")
                    .content("test content")
                    .memberId(1L)
                    .username("user")
                    .profileImg("aws/s3/somewhere")
                    .deletedYn(false)
                    .createdAt(LocalDateTime.now())
                    .count(3L)
                    .memberContentImages(new ArrayList<>(List.of(mcid)))
                    .build();
            List<MemberContentListDto> list = new ArrayList<>(List.of(c));
            Page<MemberContentListDto> plist = new PageImpl<>(list);
            PageRequest pageRequest = PageRequest.of(0, 5);

            given(memberContentQueryRepository.findByContentList(pageRequest)).willReturn(
                    plist
            );
            Page<MemberContentListDto> contentList = memberContentService.getContentList(pageRequest);

            verify(memberContentQueryRepository,times(1)).findByContentList(any());

            assertThat(contentList.getSize()).isEqualTo(1);
            assertThat(contentList.isLast()).isTrue();
            contentList.forEach(
                    (x)->{
                        assertThat(x.getTitle()).isEqualTo("test title");
                    }
            );
        }
    }

    @Nested
    @DisplayName("멤버 피드 상세 테스트")
    class TestGetMemberContentDetail{
        @Test
        @DisplayName("멤버 피드 상세 실팰 [삭제됨]")
        public void failMemberFeedDetailDeletedAlready() throws Exception{
            //given
            doReturn(false).when(memberContentRepository).existsById(any());
            //when
            MemberException exception = assertThrows(MemberException.class,
                    ()-> memberContentService.getContentDetail(any()));

            //then
            assertEquals(MemberErrorCode.MEMBER_CONTENT_DELETED, exception.getErrorCode());
        }
        @Test
        public void successMemberFeedDetail() throws Exception{
            //given
            doReturn(true).when(memberContentRepository).existsById(any());
            doReturn(MemberContentListDto.builder().title("test").build())
                    .when(memberContentQueryRepository).getContentDetail(any());
            doReturn(new PageImpl<>(List.of(MemberContentCommentDto.builder().build())))
                    .when(memberContentQueryRepository).getCommentByContent(any(), any());
            //when
            MemberContentDetailDto contentDetail = memberContentService.getContentDetail(1L);
            //then
            verify(memberContentRepository,times(1)).existsById(any());
            verify(memberContentQueryRepository,times(1)).getContentDetail(any());
            verify(memberContentQueryRepository,times(1)).getCommentByContent(any(),any());
            assertEquals("test", contentDetail.getTitle());
        }
    }
}