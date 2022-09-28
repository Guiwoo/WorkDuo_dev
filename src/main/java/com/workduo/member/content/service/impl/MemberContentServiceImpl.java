package com.workduo.member.content.service.impl;

import com.workduo.common.CommonRequestContext;
import com.workduo.error.member.exception.MemberException;
import com.workduo.error.member.type.MemberErrorCode;
import com.workduo.member.content.entity.MemberContentComment;
import com.workduo.member.content.dto.*;
import com.workduo.member.content.entity.MemberContent;
import com.workduo.member.content.entity.MemberContentCommentLike;
import com.workduo.member.content.entity.MemberContentLike;
import com.workduo.member.content.repository.MemberContentCommentLikeRepository;
import com.workduo.member.content.repository.MemberContentCommentRepository;
import com.workduo.member.content.repository.MemberContentLikeRepository;
import com.workduo.member.content.repository.MemberContentRepository;
import com.workduo.member.content.repository.query.impl.MemberContentQueryRepositoryImpl;
import com.workduo.member.content.service.MemberContentService;
import com.workduo.member.contentimage.entitiy.MemberContentImage;
import com.workduo.member.contentimage.repository.MemberContentImageRepository;
import com.workduo.member.member.entity.Member;
import com.workduo.member.member.repository.MemberRepository;
import com.workduo.util.AwsS3Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.workduo.error.member.type.MemberErrorCode.*;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberContentServiceImpl implements MemberContentService {
    private final CommonRequestContext commonRequestContext;
    private final EntityManager em;
    private final MemberRepository memberRepository;
    private final MemberContentRepository memberContentRepository;
    private final MemberContentImageRepository memberContentImageRepository;
    private final MemberContentCommentLikeRepository memberContentCommentLikeRepository;
    private final MemberContentCommentRepository memberContentCommentRepository;
    private final MemberContentLikeRepository memberContentLikeRepository;
    private final AwsS3Utils awsS3Utils;
    private final MemberContentQueryRepositoryImpl memberContentQueryRepository;

    /**
     * 멤버 피드 생성
     * @param req
     * @param multipartFiles
     */
    @Override
    public void createContent(ContentCreate.Request req, List<MultipartFile> multipartFiles) {
        // 에러 1 사용자 같지 않은경우
        Member member = validCheckLoggedInUser();
        // 컨탠트 만들어야지
        MemberContent content = MemberContent.builder()
                .member(member)
                .title(req.getTitle())
                .sortValue(req.getSortValue())
                .noticeYn(false)
                .content(req.getContent())
                .deletedYn(false)
                .build();
        memberContentRepository.save(content);
        em.flush();
        // 이미지 쪽 도 만들어야지
        if (multipartFiles != null) {
            String path = generatePath(member.getId(), content.getId());
            List<String> files = awsS3Utils.uploadFile(multipartFiles, path);
            List<MemberContentImage> contentImages =
                    MemberContentImage.createMemberContentImage(content, files);
            memberContentImageRepository.saveAll(contentImages);
        }
    }

    /**
     * 멤버 피드 리스트 누구나 관람 할수 있어야함
     * @param pageable
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public Page<MemberContentListDto> getContentList(Pageable pageable) {
//        멤버 인 경우라면 ? 추후 고민 해야할 사항 위도 경도 로 넣어서 현재 기준으로 가까운 피드들 들고오기.
//        String memberEmail = commonRequestContext.getMemberEmail();
//        Member member = null;
//        if(memberEmail != null){
//            member = getMember(memberEmail);
//        }
        return memberContentQueryRepository.findByContentList(pageable);
    }

    /**
     * 피드 상세
     * @param memberContentId
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public MemberContentDetailDto getContentDetail(Long memberContentId) {
        //예외 가져오는데 ? 이미 지워진 게시글 이라면 ?
        boolean exists = memberContentRepository.existsById(memberContentId);
        if(!exists){
            throw new MemberException(MEMBER_CONTENT_DELETED);
        }
        PageRequest pageRequest = PageRequest.of(0, 10);
        MemberContentDto contentDetail = memberContentQueryRepository.getContentDetail(memberContentId);
        List<MemberContentImageDto> byMemberContent = memberContentQueryRepository.getByMemberContent(memberContentId);
        Page<MemberContentCommentDto> commentByContent =
                memberContentQueryRepository.getCommentByContent(memberContentId, pageRequest);

        return MemberContentDetailDto.from(
                MemberContentListDto.from(contentDetail,byMemberContent)
                , commentByContent);
    }

    /**
     * 피드 업데이트
     * @param memberContentId
     * @param req
     */
    @Override
    public void contentUpdate(Long memberContentId, ContentUpdate.Request req) {
        Member member = getMember(commonRequestContext.getMemberEmail());
        MemberContent memberContent = getContent(memberContentId);
        validEditCheck(member,memberContent);
        memberContent.updateContent(req);
    }

    /**
     * 멤버 컨탠트 삭제
     * @param contentId
     */
    @Override
    public void contentDelete(Long contentId) {
        Member member = validCheckLoggedInUser();
        MemberContent content = getContent(contentId);
        validEditCheck(member,content);
        List<MemberContentComment> cList =
                memberContentCommentRepository.findAllByMemberContent(content);

        memberContentLikeRepository.
                deleteAllByMemberContent(content);
        memberContentCommentLikeRepository.
                deleteAllByMemberContentCommentIn(cList);
        memberContentCommentRepository.
                deleteAllByMemberContent(content);

        content.terminate();
    }

    /**
     * 피드 좋아욧
     * @param contentId
     */
    @Override
    public void contentLike(Long contentId) {
        Member m = validCheckLoggedInUser();
        MemberContent mc = getContent(contentId);
        validateLike(m, mc);

        memberContentLikeRepository.save(MemberContentLike.builder()
                        .member(m)
                        .memberContent(mc)
                        .build());
    }

    /**
     * 피드 좋아욧 캔슬
     * @param contentId
     */
    @Override
    public void contentLikeCancel(Long contentId) {
        Member m = validCheckLoggedInUser();
        MemberContent mc = getContent(contentId);
        validateLikeCancel(m,mc);

        memberContentLikeRepository.deleteByMemberAndMemberContent(m,mc);
    }

    /**
     * 댓글 작성
     * @param req
     * @param contentId
     */
    @Override
    public void contentCommentCreate(ContentCommentCreate.Request req, Long contentId) {
        Member m = validCheckLoggedInUser();
        MemberContent mc = getContent(contentId);

        MemberContentComment mcc = MemberContentComment.builder()
                .member(m)
                .memberContent(mc)
                .content(req.getComment())
                .deletedYn(false)
                .build();

        memberContentCommentRepository.save(mcc);
    }

    /**
     * 댓글 조회
     * @param memberContentId
     * @param pageable
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public Page<MemberContentCommentDto> getContentCommentList(Long memberContentId, Pageable pageable) {
        MemberContent mc = getContent(memberContentId);
        return memberContentQueryRepository.getCommentByContent(memberContentId,pageable);
    }

    /**
     * 댓글 업데이트
     * @param memberContentId
     * @param commentId
     * @param req
     */
    @Override
    public void contentCommentUpdate(Long memberContentId, Long commentId, ContentCommentUpdate.Request req) {
        Member m = validCheckLoggedInUser();
        MemberContent mc = getContent(memberContentId);
        MemberContentComment memberContentComment = getMemberContentComment(commentId, m, mc);

        memberContentComment.updateComment(req.getComment());
    }
    /**
     * 댓글 삭제
     * @param memberContentId
     * @param commentId
     */
    @Override
    public void contentCommentDelete(Long memberContentId, Long commentId) {
        Member m = validCheckLoggedInUser();
        MemberContent mc = getContent(memberContentId);
        MemberContentComment memberContentComment = getMemberContentComment(commentId, m, mc);

        memberContentCommentLikeRepository.deleteAllByMemberContentCommentIn(
                new ArrayList<>(List.of(memberContentComment))
        );
        memberContentComment.terminate();
    }

    /**
     * 댓글 좋아요
     * @param contentId
     * @param commentId
     */
    @Override
    public void contentCommentLike(Long contentId, Long commentId) {
        Member m = validCheckLoggedInUser();
        MemberContent mc = getContent(contentId);
        MemberContentComment memberContentComment = getMemberContentComment(commentId, m, mc);

        MemberContentCommentLike build = MemberContentCommentLike.builder()
                .member(m)
                .memberContentComment(memberContentComment)
                .build();

        memberContentCommentLikeRepository.save(build);
    }

    private void isCommentDeleted(MemberContentComment mcc){
        if(mcc.isDeletedYn()){
            throw new MemberException(MEMBER_COMMENT_DELETED);
        }
    }

    @Transactional(readOnly = true)
    protected MemberContentComment getMemberContentComment(Long commentId, Member m, MemberContent mc) {
        MemberContentComment memberContentComment = memberContentCommentRepository.
                findByIdAndMemberAndMemberContentAndDeletedYn(commentId, m, mc,false)
                .orElseThrow(() -> new MemberException(MEMBER_COMMENT_DOES_NOT_EXIST));
        isCommentDeleted(memberContentComment);
        return memberContentComment;
    }


    @Transactional(readOnly = true)
    public void validateLikeCancel(Member m,MemberContent mc){
        boolean exists = memberContentLikeRepository.existsByMemberAndMemberContent(m, mc);
        if(!exists){
            throw new MemberException(MEMBER_CONTENT_LIKE_DOES_NOT_EXIST);
        }
    }

    @Transactional(readOnly = true)
    protected void validateLike(Member m,MemberContent mc){
        boolean exists = memberContentLikeRepository.existsByMemberAndMemberContent(m, mc);
        if(exists){
            throw new MemberException(MEMBER_CONTENT_LIKE_ALREADY);
        }
    }

    @Transactional(readOnly = true)
    public Member validCheckLoggedInUser(){
        Member m = memberRepository.findByEmail(commonRequestContext.getMemberEmail())
                .orElseThrow(()->new MemberException(MemberErrorCode.MEMBER_EMAIL_ERROR));
        if(!Objects.equals(commonRequestContext.getMemberEmail(), m.getEmail())){
            throw new MemberException(MemberErrorCode.MEMBER_ERROR_NEED_LOGIN);
        }
        return m;
    }
    public String generatePath(Long memberId, Long contentId) {
        return "member/" + memberId + "/content/" + contentId + "/";
    }
    @Transactional(readOnly = true)
    protected void validEditCheck(Member m, MemberContent mc){
        //1. 멤버 아이디랑 컨탠트 멤버 아이디랑 다른경우
        if(m != mc.getMember()){
            throw new MemberException(MEMBER_CONTENT_AUTHORIZATION);
        }
        //2. 멤버 컨탠트 가 삭제된 게시글 인 경우
        isContentDeleted(mc);
    }
    private void isContentDeleted(MemberContent mc){
        if(mc.isDeletedYn()){
            throw new MemberException(MEMBER_CONTENT_DELETED);
        }
    }

    @Transactional(readOnly = true)
    protected Member getMember(String memberEmail) {
        return memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new MemberException(MEMBER_EMAIL_ERROR));
    }
    @Transactional(readOnly = true)
    protected MemberContent getContent(Long contentId){
        MemberContent memberContent = memberContentRepository.findById(contentId)
                .orElseThrow(() -> new MemberException(MEMBER_CONTENT_DOES_NOT_EXIST));
        isContentDeleted(memberContent);
        return memberContent;
    }
}
