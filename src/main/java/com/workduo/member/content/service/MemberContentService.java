package com.workduo.member.content.service;

import com.workduo.member.content.dto.ContentCreate;
import com.workduo.member.content.dto.MemberContentListDto;
import com.workduo.member.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MemberContentService {
    void createContent(ContentCreate.Request req, List<MultipartFile> multipartFiles);
    Page<MemberContentListDto> getContentList(Pageable pageable);
}
