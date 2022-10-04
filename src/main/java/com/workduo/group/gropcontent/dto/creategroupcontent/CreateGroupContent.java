package com.workduo.group.gropcontent.dto.creategroupcontent;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CreateGroupContent {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(name = "CreateGroupContent")
    public static class Request {
        @NotNull(message = "제목은 필수 입력 사항입니다.")
        @Schema(example = "Update Title YO",description = "제목 업데이트")
        private String title;

        @NotNull(message = "내용은 필수 입력 사항입니다.")
        @Schema(example = "HolyWak, Is there hell ?",description = "컨탠트 업데이트")
        private String content;

        @Schema(example = "false",description = "공지사항")
        private boolean noticeYn;

        @Min(value = 0, message = "정렬값은 최소 0 입니다.")
        @Schema(example = "0",description = "정렬값 을 줄수 있습니다.")
        private int sortValue;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private String title;
        private String content;
        private LocalDateTime createdAt;

        public static Response from(CreateGroupContentDto createGroupContentDto) {
            return Response.builder()
                    .id(createGroupContentDto.getId())
                    .title(createGroupContentDto.getTitle())
                    .content(createGroupContentDto.getContent())
                    .createdAt(createGroupContentDto.getCreatedAt())
                    .build();
        }
    }
}
