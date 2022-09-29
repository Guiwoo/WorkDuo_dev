package com.workduo.member.content.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class MemberContentDetailDto {
    private Long id;
    private String title;
    private String content;
    private Long memberId;
    private String username;
    private String nickname;
    private String profileImg;
    private boolean deletedYn;
    private LocalDateTime createdAt;
    private Long countLike;
    private List<MemberContentImageDto> memberContentImages;
    private Page<MemberContentCommentDto> comments;

    public static MemberContentDetailDto from(
            MemberContentListDto listDto,
            Page<MemberContentCommentDto> comments){
        return MemberContentDetailDto.builder()
                .id(listDto.getId())
                .title(listDto.getTitle())
                .content(listDto.getContent())
                .memberId(listDto.getMemberId())
                .username(listDto.getUsername())
                .nickname(listDto.getNickname())
                .profileImg(listDto.getProfileImg())
                .deletedYn(listDto.isDeletedYn())
                .createdAt(listDto.getCreatedAt())
                .countLike(listDto.getCount())
                .memberContentImages(listDto.getMemberContentImages())
                .comments(comments)
                .build();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response{
        private MemberContentDetailDto result;

        public static Response from(
                MemberContentDetailDto contents
        ){
            return Response.builder()
                    .result(contents)
                    .build();
        }
    }
}
