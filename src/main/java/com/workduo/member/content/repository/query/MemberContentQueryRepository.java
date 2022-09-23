package com.workduo.member.content.repository.query;

import com.workduo.member.content.dto.MemberContentCommentDto;
import com.workduo.member.content.dto.MemberContentDto;
import com.workduo.member.content.dto.MemberContentImageDto;
import com.workduo.member.content.dto.MemberContentListDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MemberContentQueryRepository {
    Page<MemberContentListDto> findByContentList(Pageable pageable);
    List<MemberContentImageDto> getByMemberContent(Long memberContentId);
    List<MemberContentImageDto> getAllImageByMemberContent(List<Long> memberContentIdList);
    MemberContentDto getContentDetail(Long memberContentId);
    Page<MemberContentCommentDto> getCommentByContent(Long memberContentId, Pageable pageable);
}
