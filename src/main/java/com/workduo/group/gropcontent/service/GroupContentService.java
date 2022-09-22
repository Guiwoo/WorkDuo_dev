package com.workduo.group.gropcontent.service;

import com.workduo.group.gropcontent.dto.createGroupContentComment.CreateComment;
import com.workduo.group.gropcontent.dto.creategroupcontent.CreateGroupContent;
import com.workduo.group.gropcontent.dto.detailgroupcontent.DetailGroupContentDto;
import com.workduo.group.gropcontent.dto.detailgroupcontent.GroupContentCommentDto;
import com.workduo.group.gropcontent.dto.detailgroupcontent.GroupContentDto;
import com.workduo.group.gropcontent.dto.updategroupcontent.UpdateContent;
import com.workduo.group.gropcontent.dto.updategroupcontentcomment.UpdateComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface GroupContentService {

    /**
     * 그룹 피드 리스트
     * @param pageable
     * @return
     */
    Page<GroupContentDto> groupContentList(Pageable pageable, Long groupId);

    /**
     * 그룹 피드 생성
     * @param groupId
     * @param request
     */
    void createGroupContent(
            Long groupId,
            CreateGroupContent.Request request,
            List<MultipartFile> multipartFiles);

    /**
     * 그룹 피드 상세
     * @param groupId
     * @param groupContentId
     * @return
     */
    DetailGroupContentDto detailGroupContent(Long groupId, Long groupContentId);

    /**
     * 그룹 피드 좋아요
     * @param groupId
     * @param groupContentId
     */
    void groupContentLike(Long groupId, Long groupContentId);

    /**
     * 그룹 피드 좋아요 취소
     * @param groupId
     * @param groupContentId
     */
    void groupContentUnLike(Long groupId, Long groupContentId);

    /**
     * 그룹 피드 삭제
     * @param groupId
     * @param groupContentId
     */
    void groupContentDelete(Long groupId, Long groupContentId);

    /**
     * 그룹 피드 수정
     * @param request
     * @param groupId
     * @param groupContentId
     */
    void groupContentUpdate(UpdateContent.Request request , Long groupId, Long groupContentId);

    /**
     * 그룹 피드 댓글 리스트
     * @param pageable
     * @param groupId
     * @param groupContentId
     * @return
     */
    Page<GroupContentCommentDto> groupContentCommentList(
            Pageable pageable,
            Long groupId,
            Long groupContentId);

    /**
     * 그룹 피드 댓글 작성
     * @param groupId
     * @param groupContentId
     */
    void createGroupContentComment(
            CreateComment.Request request,
            Long groupId,
            Long groupContentId);

    /**
     * 그룹 피드 댓글 수정
     * @param groupId
     * @param groupContentId
     */
    void updateGroupContentComment(
            UpdateComment.Request request,
            Long groupId,
            Long groupContentId,
            Long commentId);

    /**
     * 그룹 피드 댓글 삭제
     * @param groupId
     * @param groupContentId
     */
    void deleteGroupContentComment(Long groupId, Long groupContentId, Long commentId);
}
