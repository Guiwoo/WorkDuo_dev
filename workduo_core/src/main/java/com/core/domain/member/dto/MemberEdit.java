package com.core.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

public class MemberEdit {
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(name = "MemberEdit")
    public static class Request {
        @NotNull(message = "유저이름 은 필수 입력 사항 입니다.")
        @Schema(description = "유저이름",example = "한규빈")
        private String username; // 유저 이름

        @NotNull(message = "핸드폰 번호 는 필수 입력 사항 입니다.")
        @Schema(description = "-를 뱬 11자리 입니다.",example = "01012341234")
        private String phoneNumber; // 핸드폰

        @NotNull(message = "닉네임 은 필수 입력 사항 입니다.")
        @Schema(description = "서비스 사용간 보여지는 닉네임 입니다.",example = "규정뱅이")
        private String nickname; // 별명

        @Schema(description = "상태 메시지 입니다.",example = "술이 달다...")
        private String status; //상태메세지

        //지역3개
        @NotNull(message = "지역 은 최소 1개 이상 선택해야 합니다.")
        @Size(min = 1,max = 3)
        @Schema(description = "지역 선택 입니다. 1~3 개 선택 가능합니다. 지역의 아이디를 넣어주어야 합니다." ,example = "11140")
        private List<String> siggAreaList;
        //운동3게
        @NotNull(message = "스포츠 는 최소 1개 이상 선택해야 합니다.")
        @Size(min = 1,max = 3)
        @Schema(description = "스포츠 선택 입니다. 1~3 개 선택 가능합니다. 스포츠 아이디를 넣어주어야 합니다." ,example = "1,2")
        private List<Integer> sportList;
    }
}
