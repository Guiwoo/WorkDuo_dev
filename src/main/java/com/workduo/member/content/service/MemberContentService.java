package com.workduo.member.content.service;

import com.workduo.member.content.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MemberContentService {
    void createContent(ContentCreate.Request req, List<MultipartFile> multipartFiles);
    Page<MemberContentListDto> getContentList(Pageable pageable);
    MemberContentDetailDto getContentDetail(Long memberContentId);

    void contentUpdate(Long memberContentId, ContentUpdate.Request req);

    void contentDelete(Long contentId);

    void contentLike(Long contentId);

    void contentLikeCancel(Long contentId);

    void contentCommentCreate(ContentCommentCreate.Request req, Long contentId);

    Page<MemberContentCommentDto> getContentCommentList(Long memberContentId, Pageable pageable);

    void contentCommentUpdate(Long memberContentId, Long commentId, ContentCommentUpdate.Request req);

    void contentConmmentDeltet(Long memberContentId, Long commentId);
}
