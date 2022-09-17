package com.workduo.member.content.service;

import com.workduo.member.content.dto.ContentCreate;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MemberContentService {
    void createContent(ContentCreate.Request req, List<MultipartFile> multipartFiles);
}
