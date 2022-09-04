package com.workduo.group.gropcontent.dto.creategroupcontent;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class CreateGroupContent {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        @NotNull
        private Long groupId;

        @NotNull
        private String title;

        @NotNull
        private String content;

        private int maxParticipant;
        private LocalDateTime meetingDate;
        private String location;
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
