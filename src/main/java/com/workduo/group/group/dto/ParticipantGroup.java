package com.workduo.group.group.dto;

import lombok.*;
import org.springframework.data.domain.Page;

public class ParticipantGroup {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private String success;
        private Page<GroupParticipantsDto> result;

        public static ParticipantGroup.Response from(Page<GroupParticipantsDto> groupParticipantsDto) {
            return Response.builder()
                    .success("T")
                    .result(groupParticipantsDto)
                    .build();
        }
    }
}
