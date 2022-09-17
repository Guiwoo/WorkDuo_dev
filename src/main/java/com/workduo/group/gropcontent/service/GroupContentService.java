package com.workduo.group.gropcontent.service;

import com.workduo.group.gropcontent.dto.creategroupcontent.CreateGroupContent;
import com.workduo.group.gropcontent.dto.creategroupcontent.CreateGroupContentDto;
import com.workduo.group.gropcontent.dto.detailgroupcontent.DetailGroupContentDto;
import com.workduo.group.gropcontent.dto.detailgroupcontent.GroupContentDto;
import com.workduo.group.gropcontent.entity.GroupContent;
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
}
