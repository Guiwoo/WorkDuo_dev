package com.workduo.member.member.dto;

import com.core.domain.member.entity.ExistMember;
import com.core.domain.member.entity.Member;
import com.core.domain.member.entity.MemberRole;
import com.core.domain.member.type.MemberRoleType;
import com.core.domain.member.type.MemberStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

public class MemberCreate {
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(name = "MemberCreate")
    public static class Request {

        @NotNull(message = "이메일 은 필수 입력 사항 입니다.")
        @Schema(description = "이메일",example = "이메일@hotmail.com")
        private String email;

        @NotNull(message = "비밀번호 는 필수 입력 사항 입니다.")
        @Schema(description = "소문자 1,숫자 1,특수문자 1 을 포함한 최소 8문자~20문자",example = "1q2w3e4r@")
        private String password;

        @NotNull(message = "유저이름 은 필수 입력 사항 입니다.")
        @Schema(description = "유저이름",example = "한규빈")
        private String username; // 유저 이름

        @NotNull(message = "핸드폰 번호 는 필수 입력 사항 입니다.")
        @Schema(description = "-를 뱬 11자리 입니다.",example = "01012341234")
        private String phoneNumber; // 핸드폰

        @NotNull(message = "닉네임 은 필수 입력 사항 입니다.")
        @Schema(description = "서비스 사용간 보여지는 닉네임 입니다.",example = "규정뱅이")
        private String nickname; // 별명

        //지역3개
        @NotNull(message = "지역 은 최소 1개 이상 선택해야 합니다.")
        @Size(min = 1,max = 3)
        @Schema(description = "지역 선택 입니다. 1~3 개 선택 가능합니다. 지역의 아이디를 넣어주어야 합니다." ,example = "[11140]")
        private List<String> siggAreaList;
        //운동3게
        @NotNull(message = "스포츠 는 최소 1개 이상 선택해야 합니다.")
        @Size(min = 1,max = 3)
        @Schema(description = "스포츠 선택 입니다. 1~3 개 선택 가능합니다. 스포츠 아이디를 넣어주어야 합니다." ,example = "[1,2]")
        private List<Integer> sportList;
    }
    public static Member createReqToMember(Request req) {
        return Member.builder()
                .email(req.getEmail())
                .username(req.getUsername())
                .nickname(req.getNickname())
                .phoneNumber(req.getPhoneNumber())
                .memberStatus(MemberStatus.MEMBER_STATUS_ING)
                .build();
    }
    public static MemberRole createReqToMemberRole(Member m, MemberRoleType t){
        return MemberRole.builder()
                .member(m)
                .memberRole(t)
                .build();
    }
    public static ExistMember createReqToExistMember(String email){
        return ExistMember.builder().memberEmail(email).build();
    }
}
