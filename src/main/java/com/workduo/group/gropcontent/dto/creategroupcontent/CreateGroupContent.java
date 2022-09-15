package com.workduo.group.gropcontent.dto.creategroupcontent;

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
    public static class Request {
        @NotNull(message = "제목은 필수 입력 사항입니다.")
        private String title;

        @NotNull(message = "내용은 필수 입력 사항입니다.")
        private String content;

        private boolean noticeYn;

        @Min(value = 0, message = "정렬값은 최소 0 입니다.")
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
