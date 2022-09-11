package com.workduo.member.member.dto;

import com.workduo.member.member.entity.Member;
import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Map;

public class MemberEdit {
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        @NotNull(message = "유저이름 은 필수 입력 사항 입니다.")
        private String username; // 유저 이름

        @NotNull(message = "핸드폰 번호 는 필수 입력 사항 입니다.")
        private String phoneNumber; // 핸드폰

        @NotNull(message = "닉네임 은 필수 입력 사항 입니다.")
        private String nickname; // 별명

        private String profileImg;//사진
        private String status; //상태메세지

        //지역3개
        @NotNull(message = "지역 은 최소 1개 이상 선택해야 합니다.")
        @Size(min = 1,max = 3)
        private List<String> siggAreaList;
        //운동3게
        @NotNull(message = "스포츠 는 최소 1개 이상 선택해야 합니다.")
        @Size(min = 1,max = 3)
        private List<Integer> sportList;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response{
        private String success;
        private Map<String,String> result;

        public static MemberCreate.Response from(){
            return MemberCreate.Response.builder()
                    .success("T")
                    .result(null)
                    .build();
        }
    }

    public static void editReqToUpdateMember(Request req, Member m){
        m.setUsername(req.getUsername());
        m.setNickname(req.getNickname());
        m.setPhoneNumber(req.getPhoneNumber());
        m.setProfileImg(req.getProfileImg());
        m.setStatus(req.getStatus());
    }
}
