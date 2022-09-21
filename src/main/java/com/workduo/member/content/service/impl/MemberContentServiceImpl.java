package com.workduo.member.content.service.impl;

import com.workduo.common.CommonRequestContext;
import com.workduo.error.member.exception.MemberException;
import com.workduo.error.member.type.MemberErrorCode;
import com.workduo.member.content.dto.ContentCreate;
import com.workduo.member.content.dto.MemberContentListDto;
import com.workduo.member.content.entity.MemberContent;
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
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Objects;

import static com.workduo.error.member.type.MemberErrorCode.MEMBER_EMAIL_ERROR;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberContentServiceImpl implements MemberContentService {
    private final CommonRequestContext commonRequestContext;
    private final EntityManager em;
    private final MemberRepository memberRepository;
    private final MemberContentRepository memberContentRepository;
    private final MemberContentImageRepository memberContentImageRepository;
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
    private Member getMember(String memberEmail) {
        return memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new MemberException(MEMBER_EMAIL_ERROR));
    }
}
