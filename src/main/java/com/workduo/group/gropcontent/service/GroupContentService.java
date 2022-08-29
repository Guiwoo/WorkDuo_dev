package com.workduo.group.gropcontent.service;

import com.workduo.group.gropcontent.dto.creategroupcontent.CreateGroupContent;
import com.workduo.group.gropcontent.dto.creategroupcontent.CreateGroupContentDto;

public interface GroupContentService {

    CreateGroupContentDto createGroupContent(
            CreateGroupContent.Request request,
            Long memberId);
}
