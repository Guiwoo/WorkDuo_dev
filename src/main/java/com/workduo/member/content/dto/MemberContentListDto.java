package com.workduo.member.content.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class MemberContentListDto {
    private Long id;
    private String title;
    private String content;
    private boolean noticeYn;
    private int sortValue;
    private Long memberId;
    private String username;
    private String nickname;
    private String profileImg;
    private boolean deletedYn;
    private LocalDateTime createdAt;
    private Long count;
    private List<MemberContentImageDto> memberContentImages;

    public static MemberContentListDto from(
            MemberContentDto memberContentDto,
            List<MemberContentImageDto> memberContentImageDto
    ){
        return MemberContentListDto.builder()
                .id(memberContentDto.getId())
                .title(memberContentDto.getTitle())
                .content(memberContentDto.getContent())
                .noticeYn(memberContentDto.isNoticeYn())
                .sortValue(memberContentDto.getSortValue())
                .memberId(memberContentDto.getMemberId())
                .username(memberContentDto.getUsername())
                .nickname(memberContentDto.getNickname())
                .profileImg(memberContentDto.getProfileImg())
                .deletedYn(memberContentDto.isDeletedYn())
                .createdAt(memberContentDto.getCreatedAt())
                .count(memberContentDto.getCount())
                .memberContentImages(memberContentImageDto)
                .build();

    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response{
        private String success;
        private Page<MemberContentListDto> result;

        public static Response from(
                Page<MemberContentListDto> lists
        ){
            return Response.builder()
                    .success("T")
                    .result(lists)
                    .build();
        }
    }
}
