package com.workduo.sport.sport.dto;

import com.workduo.sport.sport.entity.Sport;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SportDto {

    private Integer id;
    private String name;
    private String emojiPath;

    public static SportDto fromEntity(Sport sport) {
        return SportDto.builder()
                .id(sport.getId())
                .name(sport.getName())
                .emojiPath(sport.getEmojiPath())
                .build();
    }
}
