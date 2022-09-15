package com.workduo.group.gropcontent.service;

import com.workduo.group.gropcontent.dto.creategroupcontent.CreateGroupContent;
import com.workduo.group.gropcontent.dto.creategroupcontent.CreateGroupContentDto;
import com.workduo.group.gropcontent.dto.detailgroupcontent.DetailGroupContentDto;
import com.workduo.group.gropcontent.entity.GroupContent;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface GroupContentService {

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
     * @param groupContentId
     * @return
     */
    DetailGroupContentDto detailGroupContent(Long groupContentId);
}
