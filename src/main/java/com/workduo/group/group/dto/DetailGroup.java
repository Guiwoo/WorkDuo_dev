package com.workduo.group.group.dto;

import com.workduo.area.sidoarea.entity.SidoArea;
import com.workduo.area.siggarea.entity.SiggArea;
import com.workduo.sport.sport.entity.Sport;
import com.workduo.sport.sportcategory.entity.SportCategory;
import lombok.*;

import java.util.HashMap;
import java.util.Map;

public class DetailGroup {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private String success;
        private GroupDto result;

        public static Response from(GroupDto groupDto) {

            return Response.builder()
                    .success("T")
                    .result(groupDto)
                    .build();
        }

    }
}
