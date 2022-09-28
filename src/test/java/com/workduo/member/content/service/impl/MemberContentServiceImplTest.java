package com.workduo.member.content.service.impl;

import com.workduo.common.CommonRequestContext;
import com.workduo.error.member.exception.MemberException;
import com.workduo.error.member.type.MemberErrorCode;
import com.workduo.member.content.dto.*;
import com.workduo.member.content.entity.MemberContent;
import com.workduo.member.content.entity.MemberContentComment;
import com.workduo.member.content.repository.MemberContentCommentLikeRepository;
import com.workduo.member.content.repository.MemberContentCommentRepository;
import com.workduo.member.content.repository.MemberContentLikeRepository;
import com.workduo.member.content.repository.MemberContentRepository;
import com.workduo.member.content.repository.query.impl.MemberContentQueryRepositoryImpl;
import com.workduo.member.contentimage.entitiy.MemberContentImage;
import com.workduo.member.contentimage.repository.MemberContentImageRepository;
import com.workduo.member.member.entity.Member;
import com.workduo.member.member.repository.MemberRepository;
import com.workduo.util.AwsS3Utils;
import org.hibernate.boot.TempTableDdlTransactionHandling;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.workduo.member.content.dto.ContentCommentCreate.*;
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
    private MemberContentLikeRepository memberContentLikeRepository;
    @Mock
    private MemberContentCommentRepository memberContentCommentRepository;
    @Mock
    private MemberContentCommentLikeRepository memberContentCommentLikeRepository;
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
            doReturn(MemberContentDto.builder().title("test").build())
                    .when(memberContentQueryRepository).getContentDetail(any());
            doReturn(new ArrayList<>())
                    .when(memberContentQueryRepository).getByMemberContent(any());
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

    @Nested
    @DisplayName("멤버 피드 수정 테스트")
    class TestUpdateContent{
        Long contentId = 169L;
        ContentUpdate.Request req = ContentUpdate.Request.builder()
                .title("Holy")
                .content("Moly")
                .build();
        @Test
        @DisplayName("멤버 피드 수정 실패 [로그인 유저 미 일치]")
        public void failMissingMember() throws Exception{
            //given
            given(commonRequestContext.getMemberEmail()).willReturn("True-Lover");
            //when
            MemberException exception = assertThrows(MemberException.class,
                    ()->memberContentService.contentUpdate(contentId,req));
            //then
            assertEquals(MemberErrorCode.MEMBER_EMAIL_ERROR,exception.getErrorCode());
        }

        @Test
        @DisplayName("멤버 피드 수정 실패 [피드가 없는 경우]")
        public void doesNotExistContent() throws Exception{
            //given
            doReturn(Optional.of(Member.builder().build()))
                    .when(memberRepository).findByEmail(any());
            //when
            MemberException exception = assertThrows(MemberException.class,
                    ()->memberContentService.contentUpdate(contentId,req));
            //then
            assertEquals(MemberErrorCode.MEMBER_CONTENT_DOES_NOT_EXIST
                    ,exception.getErrorCode());
        }

        @Test
        @DisplayName("멤버 피드 수정 실패 [피드 작성자가 아닌 경우]")
        public void doNotHaveAuthorization() throws Exception{
            //given
            given(commonRequestContext.getMemberEmail()).willReturn("True-Lover");
            doReturn(Optional.of(Member.builder().email("True-Lover").build()))
                    .when(memberRepository).findByEmail(any());
            doReturn(Optional.of(MemberContent.builder().build()))
                    .when(memberContentRepository).findById(any());
            //when
            MemberException exception = assertThrows(MemberException.class,
                    ()->memberContentService.contentUpdate(contentId,req));
            //then
            assertEquals(MemberErrorCode.MEMBER_CONTENT_AUTHORIZATION
                    ,exception.getErrorCode());
        }

        @Test
        @DisplayName("멤버 피드 수정 실패 [피드가 삭제된 게시글 인 경우]")
        public void feedTerminatedBefore() throws Exception{
            Member m = Member.builder().id(2L).build();
            //given
            doReturn(Optional.of(m))
                    .when(memberRepository).findByEmail(any());
            doReturn(Optional.of(MemberContent.builder().member(m)
                    .deletedYn(true).build()))
                    .when(memberContentRepository).findById(any());
            //when
            MemberException exception = assertThrows(MemberException.class,
                    ()->memberContentService.contentUpdate(contentId,req));
            //then
            assertEquals(MemberErrorCode.MEMBER_CONTENT_DELETED
                    ,exception.getErrorCode());
        }

        @Test
        @DisplayName("멤버 피드 수정 성공")
        public void success() throws Exception{
            Member m = Member.builder().id(2L).build();
            MemberContent build = MemberContent.builder().member(m)
                    .deletedYn(false).build();

            MemberContent spy = spy(build);

            //given
            doReturn(Optional.of(m))
                    .when(memberRepository).findByEmail(any());
            doReturn(Optional.of(spy))
                    .when(memberContentRepository).findById(any());
            //when
            memberContentService.contentUpdate(contentId,req);
            //then
            verify(commonRequestContext,times(1)).getMemberEmail();
            verify(memberContentRepository,times(1)).findById(any());
            verify(spy,times(1)).updateContent(any());
        }
    }

    @Nested
    @DisplayName("멤버 피드 삭제 테스트")
    class TestDeleteContent{
        Long contentId = 169L;
        Member m = Member.builder().id(2L).build();
        ContentUpdate.Request req = ContentUpdate.Request.builder()
                .title("Holy")
                .content("Moly")
                .build();
        @Test
        @DisplayName("멤버 피드 삭제 실패 [로그인 유저 미 일치]")
        public void doseNotMatchUser(){
            //given
            given(commonRequestContext.getMemberEmail()).willReturn("True-Lover");
            //when
            MemberException exception = assertThrows(MemberException.class,
                    ()->memberContentService.contentDelete(contentId));
            //then
            assertEquals(MemberErrorCode.MEMBER_EMAIL_ERROR,exception.getErrorCode());
        }

        @Test
        @DisplayName("멤버 피드 삭제 실패 [피드 작성자가 아닌 경우]")
        public void doNotHaveAuthorization() throws Exception{
            //given
            given(commonRequestContext.getMemberEmail()).willReturn("True-Lover");
            doReturn(Optional.of(Member.builder().email("True-Lover").build()))
                    .when(memberRepository).findByEmail(any());
            doReturn(Optional.of(MemberContent.builder().build()))
                    .when(memberContentRepository).findById(any());
            //when
            MemberException exception = assertThrows(MemberException.class,
                    ()->memberContentService.contentDelete(contentId));
            //then
            assertEquals(MemberErrorCode.MEMBER_CONTENT_AUTHORIZATION
                    ,exception.getErrorCode());
        }

        @Test
        @DisplayName("멤버 피드 삭제 실패 [피드가 삭제된 게시글 인 경우]")
        public void feedTerminatedBefore() throws Exception{
            //given
            doReturn(Optional.of(m))
                    .when(memberRepository).findByEmail(any());
            doReturn(Optional.of(MemberContent.builder().member(m)
                    .deletedYn(true).build()))
                    .when(memberContentRepository).findById(any());
            //when
            MemberException exception = assertThrows(MemberException.class,
                    ()->memberContentService.contentDelete(contentId));
            //then
            assertEquals(MemberErrorCode.MEMBER_CONTENT_DELETED
                    ,exception.getErrorCode());
        }

        @Test
        @DisplayName("맴버 피드 삭제 성공")
        public void feedTerminate() throws Exception{
            //given
            MemberContent build = MemberContent.builder().member(m)
                    .deletedYn(false).build();
            MemberContent spy = spy(build);

            doReturn(Optional.of(m))
                    .when(memberRepository).findByEmail(any());
            doReturn(Optional.of(spy))
                    .when(memberContentRepository).findById(any());
            doReturn(new ArrayList<>())
                    .when(memberContentCommentRepository).findAllByMemberContent(any());
            //when
            memberContentService.contentDelete(contentId);

            verify(memberContentLikeRepository,times(1))
                    .deleteAllByMemberContent(any());
            verify(memberContentCommentLikeRepository,times(1))
                    .deleteAllByMemberContentCommentIn(any());
            verify(memberContentCommentRepository,times(1))
                    .deleteAllByMemberContent(any());
            verify(spy,times(1)).terminate();
        }
    }

    @Nested
    @DisplayName("멤버 피드 좋아요 테스트")
    class TestContentLike{
        Long contentId = 169L;
        Member m = Member.builder().id(2L).build();
        @Test
        @DisplayName("멤버 피드 좋아요 실패 [로그인 유저 미 일치]")
        public void doesNotMatchUser() throws Exception {
            //given
            given(commonRequestContext.getMemberEmail()).willReturn("True-Lover");
            //when
            MemberException exception = assertThrows(MemberException.class,
                    ()->memberContentService.contentLike(contentId));
            //then
            assertEquals(MemberErrorCode.MEMBER_EMAIL_ERROR,exception.getErrorCode());
        }

        @Test
        @DisplayName("멤버 피드 좋아요 실패 [피드가 삭제된 경우]")
        public void feedDeleted() throws Exception{
            //given
            doReturn(Optional.of(Member.builder().build()))
                    .when(memberRepository).findByEmail(any());
            doReturn(Optional.of(MemberContent.builder().deletedYn(true).build()))
                    .when(memberContentRepository).findById(any());
            //when
            MemberException exception = assertThrows(MemberException.class,
                    ()-> memberContentService.contentLike(12L) );
            //then
            assertEquals(MemberErrorCode.MEMBER_CONTENT_DELETED,
                    exception.getErrorCode());
        }

        @Test
        @DisplayName("멤버 피드 좋아요 실패 [피드가 존재하지 않는 경우]")
        public void feedDoesNotExist() throws Exception{
            //given
            doReturn(Optional.of(Member.builder().build()))
                    .when(memberRepository).findByEmail(any());
            //when
            MemberException exception = assertThrows(MemberException.class,
                    ()-> memberContentService.contentLike(12L) );
            //then
            assertEquals(MemberErrorCode.MEMBER_CONTENT_DOES_NOT_EXIST,
                    exception.getErrorCode());
        }

        @Test
        @DisplayName("멤버 피드 좋아요 실패 [이미 피드 를 좋아요 한 경우]")
        public void feedLikedAlready() throws Exception{
            //given
            doReturn(Optional.of(Member.builder().build()))
                    .when(memberRepository).findByEmail(any());
            doReturn(Optional.of(MemberContent.builder().build()))
                    .when(memberContentRepository).findById(any());
            given(memberContentLikeRepository.existsByMemberAndMemberContent(any(),any()))
                    .willReturn(true);
            //when
            MemberException exception = assertThrows(MemberException.class,
                    ()-> memberContentService.contentLike(12L) );
            //then
            assertEquals(MemberErrorCode.MEMBER_CONTENT_LIKE_ALREADY,
                    exception.getErrorCode());
        }

        @Test
        @DisplayName("멤버 피드 좋아요 성공")
        public void feedLikeSuccess() throws Exception{
            //given
            doReturn(Optional.of(Member.builder().build()))
                    .when(memberRepository).findByEmail(any());
            doReturn(Optional.of(MemberContent.builder().build()))
                    .when(memberContentRepository).findById(any());
            given(memberContentLikeRepository.existsByMemberAndMemberContent(any(),any()))
                    .willReturn(false);
            //when
            memberContentService.contentLike(12L);
            //then
            verify(memberContentLikeRepository,times(1)).save(any());
        }
    }

    @Nested
    @DisplayName("멤버 피드 좋아요 취소 테스트")
    class TestContentLikeCancel{
        Long contentId = 169L;
        Member m = Member.builder().id(2L).build();

        @Test
        @DisplayName("멤버 피드 좋아요 취소 실패 [로그인 유저 미 일치]")
        public void doesNotMatchUser() throws Exception {
            //given
            given(commonRequestContext.getMemberEmail()).willReturn("True-Lover");
            //when
            MemberException exception = assertThrows(MemberException.class,
                    ()->memberContentService.contentLikeCancel(contentId));
            //then
            assertEquals(MemberErrorCode.MEMBER_EMAIL_ERROR,exception.getErrorCode());
        }

        @Test
        @DisplayName("멤버 피드 좋아요 취소 실패 [피드 가 존재하지 않는 경우]")
        public void doesNotExistFeed() throws Exception{
            doReturn(Optional.of(Member.builder().build()))
                    .when(memberRepository).findByEmail(any());
            //when
            MemberException exception = assertThrows(MemberException.class,
                    ()->memberContentService.contentLikeCancel(contentId));
            //then
            assertEquals(MemberErrorCode.MEMBER_CONTENT_DOES_NOT_EXIST
                    ,exception.getErrorCode());
        }

        @Test
        @DisplayName("멤버 피드 좋아요 취소 실패 [피드가 삭제된 경우]")
        public void feedDeleted() throws Exception{
            //given
            doReturn(Optional.of(Member.builder().build()))
                    .when(memberRepository).findByEmail(any());
            doReturn(Optional.of(MemberContent.builder().deletedYn(true).build()))
                    .when(memberContentRepository).findById(any());
            //when
            MemberException exception = assertThrows(MemberException.class,
                    ()-> memberContentService.contentLikeCancel(12L) );
            //then
            assertEquals(MemberErrorCode.MEMBER_CONTENT_DELETED,
                    exception.getErrorCode());
        }

        @Test
        @DisplayName("멤버 피드 좋아요 취소 실패 [피드 를 좋아요 하지 않은 경우]")
        public void didNotLikeFeedBefore() throws Exception{
            //given
            doReturn(Optional.of(Member.builder().build()))
                    .when(memberRepository).findByEmail(any());
            doReturn(Optional.of(MemberContent.builder().build()))
                    .when(memberContentRepository).findById(any());
            given(memberContentLikeRepository.existsByMemberAndMemberContent(any(),any()))
                    .willReturn(false);
            //when
            MemberException exception = assertThrows(MemberException.class,
                    ()-> memberContentService.contentLikeCancel(12L) );
            //then
            assertEquals(MemberErrorCode.MEMBER_CONTENT_LIKE_DOES_NOT_EXIST,
                    exception.getErrorCode());
        }

        @Test
        @DisplayName("멤버 피드 좋아요 취소 성공")
        public void successFeedCancel() throws Exception{
            //given
            doReturn(Optional.of(Member.builder().build()))
                    .when(memberRepository).findByEmail(any());
            doReturn(Optional.of(MemberContent.builder().build()))
                    .when(memberContentRepository).findById(any());
            doReturn(true).when(memberContentLikeRepository)
                    .existsByMemberAndMemberContent(any(),any());
            //when
            memberContentService.contentLikeCancel(12L);
            //then
            verify(memberContentLikeRepository,times(1))
                    .deleteByMemberAndMemberContent(any(),any());
        }
    }

    @Nested
    @DisplayName("멤버 피드 댓글 등록 테스트")
    class MemberFeedContent{
        Long contentId = 169L;
        Request req = Request.builder()
                .comment("Comment Create Test").build();
        @Test
        @DisplayName("멤버 피드 댓글 등록 실패 [로그인 유저 미 일치]")
        public void doesNotMatchUser() throws Exception {
            //given
            given(commonRequestContext.getMemberEmail()).willReturn("True-Lover");
            //when
            MemberException exception = assertThrows(MemberException.class,
                    ()->memberContentService.contentCommentCreate(req,contentId));
            //then
            assertEquals(MemberErrorCode.MEMBER_EMAIL_ERROR,exception.getErrorCode());
        }

        @Test
        @DisplayName("멤버 피드 댓글 등록 실패 [피드 가 존재하지 않는 경우]")
        public void doesNotExistFeed() throws Exception{
            doReturn(Optional.of(Member.builder().build()))
                    .when(memberRepository).findByEmail(any());
            //when
            MemberException exception = assertThrows(MemberException.class,
                    ()->memberContentService.contentCommentCreate(req,contentId));
            //then
            assertEquals(MemberErrorCode.MEMBER_CONTENT_DOES_NOT_EXIST
                    ,exception.getErrorCode());
        }

        @Test
        @DisplayName("멤버 피드 댓글 등록 실패 [피드가 삭제된 경우]")
        public void feedDeleted() throws Exception{
            //given
            doReturn(Optional.of(Member.builder().build()))
                    .when(memberRepository).findByEmail(any());
            doReturn(Optional.of(MemberContent.builder().deletedYn(true).build()))
                    .when(memberContentRepository).findById(any());
            //when
            MemberException exception = assertThrows(MemberException.class,
                    ()-> memberContentService.contentCommentCreate(req,12L) );
            //then
            assertEquals(MemberErrorCode.MEMBER_CONTENT_DELETED,
                    exception.getErrorCode());
        }

        @Test
        @DisplayName("멤버 피드 댓글 등록 성공")
        public void contentCommentCreateSuccess() throws Exception{
            //given
            Member m = Member.builder().id(11L).build();
            MemberContent mc = MemberContent.builder().id(contentId).build();
            doReturn(Optional.of(m))
                    .when(memberRepository).findByEmail(any());
            doReturn(Optional.of(mc))
                    .when(memberContentRepository).findById(any());
            ArgumentCaptor<MemberContentComment> captor =
                    ArgumentCaptor.forClass(MemberContentComment.class);
            //when
            memberContentService.contentCommentCreate(req,contentId);

            //then
            verify(memberContentCommentRepository,times(1))
                    .save(captor.capture());
            assertThat(m).isEqualTo(captor.getValue().getMember());
            assertThat(mc).isEqualTo(captor.getValue().getMemberContent());
            assertThat(req.getComment()).isEqualTo(captor.getValue().getContent());
        }
    }

    @Nested
    @DisplayName("멤버 피드 댓글 리스트 테스트")
    class MemberCommentList{
        PageRequest pageRequest = PageRequest.of(0, 20);
        @Test
        @DisplayName("멤버 피드 댓글 리스트 실패 [피드 가 존재하지 않는 경우]")
        public void doesNotExistFeed() throws Exception{
            //when
            MemberException exception = assertThrows(MemberException.class,
                    ()->memberContentService.getContentCommentList(12L,pageRequest));
            //then
            assertEquals(MemberErrorCode.MEMBER_CONTENT_DOES_NOT_EXIST
                    ,exception.getErrorCode());
        }

        @Test
        @DisplayName("멤버 피드 댓글 리스트 실패 [피드가 삭제된 경우]")
        public void feedDeleted() throws Exception{
            //given
            doReturn(Optional.of(MemberContent.builder().deletedYn(true).build()))
                    .when(memberContentRepository).findById(any());
            //when
            MemberException exception = assertThrows(MemberException.class,
                    ()-> memberContentService.getContentCommentList(12L,pageRequest) );
            //then
            assertEquals(MemberErrorCode.MEMBER_CONTENT_DELETED,
                    exception.getErrorCode());
        }

        @Test
        @DisplayName("멤버 피드 댓글 리스트 성공")
        public void sucess() throws Exception{
            //given
            doReturn(Optional.of(MemberContent.builder().build()))
                    .when(memberContentRepository).findById(any());
            //when
            memberContentService.getContentCommentList(12L,pageRequest);
            //then
            verify(memberContentQueryRepository,times(1))
                    .getCommentByContent(any(),any());
        }
    }

    @Nested
    @DisplayName("멤버 피드 댓글 업데이트 테스트")
    class MemberFeedCommentUpdate{
        Long contentId = 169L;
        Long commentId = 73L;
        ContentCommentUpdate.Request req =
                ContentCommentUpdate.Request.builder()
                        .comment("Update Test").build();
        Member m = Member.builder().build();
        MemberContent mc =MemberContent.builder().build();

        @Test
        @DisplayName("멤버 피드 댓글 업데이트 실패 [로그인 유저 미 일치]")
        public void doesNotMatchLogInUser() throws Exception{
            //given
            given(commonRequestContext.getMemberEmail()).willReturn("True-Lover");
            //when
            MemberException exception = assertThrows(MemberException.class,
                    ()->memberContentService.contentCommentUpdate(contentId,
                            commentId,req));
            //then
            assertEquals(MemberErrorCode.MEMBER_EMAIL_ERROR,exception.getErrorCode());
        }

        @Test
        @DisplayName("멤버 피드 댓글 업데이트 실패 [피드 가 존재하지 않는 경우]")
        public void doesNotExistFeed() throws Exception{
            //given
            doReturn(Optional.of(m))
                    .when(memberRepository).findByEmail(any());
            //when
            MemberException exception = assertThrows(MemberException.class,
                    ()->memberContentService.contentCommentUpdate(contentId,
                            commentId,req));
            //then
            assertEquals(MemberErrorCode.MEMBER_CONTENT_DOES_NOT_EXIST,exception.getErrorCode());
        }

        @Test
        @DisplayName("멤버 피드 댓글 업데이트 실패 [피드가 삭제된 경우]")
        public void hasDeletedFeed() throws Exception{
            //given
            doReturn(Optional.of(m))
                    .when(memberRepository).findByEmail(any());
            doReturn(Optional.of(MemberContent.builder().deletedYn(true).build()))
                    .when(memberContentRepository).findById(any());
            //when
            MemberException exception = assertThrows(MemberException.class,
                    ()->memberContentService.contentCommentUpdate(contentId,
                            commentId,req));
            //then
            assertEquals(MemberErrorCode.MEMBER_CONTENT_DELETED,exception.getErrorCode());
        }
        @Test
        @DisplayName("멤버 피드 댓글 업데이트 실패 [댓글 이 존재하지 않는 경우]")
        public void doesNotExistComment() throws Exception{
            //given
            doReturn(Optional.of(m))
                    .when(memberRepository).findByEmail(any());
            doReturn(Optional.of(MemberContent.builder().build()))
                    .when(memberContentRepository).findById(any());
            //when
            MemberException exception = assertThrows(MemberException.class,
                    ()->memberContentService.contentCommentUpdate(contentId,
                            commentId,req));
            //then
            assertEquals(MemberErrorCode.MEMBER_COMMENT_DOES_NOT_EXIST,exception.getErrorCode());
        }

        @Test
        @DisplayName("멤버 피드 댓글 업데이트 실패 [댓글 이 삭제된 경우]")
        public void hasDeletedComment() throws Exception{
            //given
            doReturn(Optional.of(m))
                    .when(memberRepository).findByEmail(any());
            doReturn(Optional.of(mc)).when(memberContentRepository).findById(any());
            doReturn(Optional.of(MemberContentComment.builder()
                    .deletedYn(true)
                    .build()))
                    .when(memberContentCommentRepository)
                    .findByIdAndMemberAndMemberContentAndDeletedYn(commentId,m,mc,false);
            //when
            MemberException exception = assertThrows(MemberException.class,
                    ()->memberContentService.contentCommentUpdate(contentId,
                            commentId,req));
            //then
            assertEquals(MemberErrorCode.MEMBER_COMMENT_DELETED,exception.getErrorCode());
        }
//        @Test
//        @DisplayName("멤버 피드 댓글 업데이트 실패 [댓글 소유주 가 아닌 경우]")
//        public void doNotHaveAuthority() throws Exception{
//            //given
//            doReturn(Optional.of(m))
//                    .when(memberRepository).findByEmail(any());
//            doReturn(Optional.of(MemberContent.builder().build()))
//                    .when(memberContentRepository).findById(any());
//            //when
//            MemberException exception = assertThrows(MemberException.class,
//                    ()->memberContentService.contentCommentUpdate(contentId,
//                            commentId,req));
//            //then
//            assertEquals(MemberErrorCode.MEMBER_COMMENT_AUTHORIZATION,exception.getErrorCode());
//        }

        @Test
        @DisplayName("멤버 피드 댓글 업데이트 성공")
        public void success() throws Exception{
            MemberContentComment build = MemberContentComment.builder().build();
            MemberContentComment spy = spy(build);
            //given
            doReturn(Optional.of(m))
                    .when(memberRepository).findByEmail(any());
            doReturn(Optional.of(mc)).when(memberContentRepository).findById(any());
            doReturn(Optional.of(spy))
                    .when(memberContentCommentRepository)
                    .findByIdAndMemberAndMemberContentAndDeletedYn(commentId,m,mc,false);

            //when
            memberContentService.contentCommentUpdate
                    (contentId,commentId,req);
            //then
            verify(spy,times(1)).updateComment(any());
        }
    }

    @Nested
    @DisplayName("멤버 피드 댓글 삭제 테스트")
    class MemberFeedCommentDelete{
        Long contentId = 169L;
        Long commentId = 73L;
        Member m = Member.builder().build();
        MemberContent mc =MemberContent.builder().build();

        @Test
        @DisplayName("멤버 피드 댓글 삭제 실패 [로그인 유저 미 일치]")
        public void doesNotMatchLogInUser() throws Exception{
            //given
            given(commonRequestContext.getMemberEmail()).willReturn("True-Lover");
            //when
            MemberException exception = assertThrows(MemberException.class,
                    ()->memberContentService.contentConmmentDeltet(contentId,
                            commentId));
            //then
            assertEquals(MemberErrorCode.MEMBER_EMAIL_ERROR,exception.getErrorCode());
        }

        @Test
        @DisplayName("멤버 피드 댓글 삭제 실패 [피드 가 존재하지 않는 경우]")
        public void doesNotExistFeed() throws Exception{
            //given
            doReturn(Optional.of(m))
                    .when(memberRepository).findByEmail(any());
            //when
            MemberException exception = assertThrows(MemberException.class,
                    ()->memberContentService.contentConmmentDeltet(contentId,
                            commentId));
            //then
            assertEquals(MemberErrorCode.MEMBER_CONTENT_DOES_NOT_EXIST,exception.getErrorCode());
        }

        @Test
        @DisplayName("멤버 피드 댓글 삭제 실패 [피드가 삭제된 경우]")
        public void hasDeletedFeed() throws Exception{
            //given
            doReturn(Optional.of(m))
                    .when(memberRepository).findByEmail(any());
            doReturn(Optional.of(MemberContent.builder().deletedYn(true).build()))
                    .when(memberContentRepository).findById(any());
            //when
            MemberException exception = assertThrows(MemberException.class,
                    ()->memberContentService.contentConmmentDeltet(contentId,
                            commentId));
            //then
            assertEquals(MemberErrorCode.MEMBER_CONTENT_DELETED,exception.getErrorCode());
        }
        @Test
        @DisplayName("멤버 피드 댓글 삭제 실패 [댓글 이 존재하지 않는 경우]")
        public void doesNotExistComment() throws Exception{
            //given
            doReturn(Optional.of(m))
                    .when(memberRepository).findByEmail(any());
            doReturn(Optional.of(MemberContent.builder().build()))
                    .when(memberContentRepository).findById(any());
            //when
            MemberException exception = assertThrows(MemberException.class,
                    ()->memberContentService.contentConmmentDeltet(contentId,
                            commentId));
            //then
            assertEquals(MemberErrorCode.MEMBER_COMMENT_DOES_NOT_EXIST,exception.getErrorCode());
        }

        @Test
        @DisplayName("멤버 피드 댓글 삭제 실패 [댓글 이 삭제된 경우]")
        public void hasDeletedComment() throws Exception{
            //given
            doReturn(Optional.of(m))
                    .when(memberRepository).findByEmail(any());
            doReturn(Optional.of(mc)).when(memberContentRepository).findById(any());
            doReturn(Optional.of(MemberContentComment.builder()
                    .deletedYn(true)
                    .build()))
                    .when(memberContentCommentRepository)
                    .findByIdAndMemberAndMemberContentAndDeletedYn(commentId,m,mc,false);
            //when
            MemberException exception = assertThrows(MemberException.class,
                    ()->memberContentService.contentConmmentDeltet(contentId,
                            commentId));
            //then
            assertEquals(MemberErrorCode.MEMBER_COMMENT_DELETED,exception.getErrorCode());
        }
        @Test
        @DisplayName("멤버 피드 댓글 삭제 성공")
        public void success() throws Exception{
            MemberContentComment build = MemberContentComment.builder().build();
            MemberContentComment spy = spy(build);
            //given
            doReturn(Optional.of(m))
                    .when(memberRepository).findByEmail(any());
            doReturn(Optional.of(mc)).when(memberContentRepository).findById(any());
            doReturn(Optional.of(spy))
                    .when(memberContentCommentRepository)
                    .findByIdAndMemberAndMemberContentAndDeletedYn(commentId,m,mc,false);
            //when
            memberContentService.contentConmmentDeltet(contentId,commentId);
            //then
            verify(memberContentCommentLikeRepository,times(1))
                    .deleteAllByMemberContentCommentIn(any());
            verify(spy,times(1)).terminate();
        }
    }
}