package com.workduo.error.group.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum GroupErrorCode {
    GROUP_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당하는 그룹이 없습니다."),
    GROUP_NOT_FOUND_USER(HttpStatus.BAD_REQUEST, "그룹에 해당 유저가 없습니다."),
    GROUP_NOT_FOUND_CONTENT(HttpStatus.BAD_REQUEST, "그룹에 해당하는 게시글이 없습니다."),
    GROUP_MAXIMUM_EXCEEDED(HttpStatus.BAD_REQUEST, "그룹생성 최대개수를 초과하였습니다."),
    GROUP_MAXIMUM_PARTICIPANT(HttpStatus.BAD_REQUEST, "그룹의 정원이 초과하였습니다."),
    GROUP_LEADER_NOT_WITHDRAW(HttpStatus.BAD_REQUEST, "그룹장은 그룹을 탈퇴할 수 없습니다."),
    GROUP_ALREADY_WITHDRAW(HttpStatus.BAD_REQUEST, "이미 탈퇴한 그룹입니다."),
    GROUP_ALREADY_DELETE_GROUP(HttpStatus.BAD_REQUEST, "이미 삭제된 그룹입니다."),
    GROUP_ALREADY_PARTICIPANT(HttpStatus.BAD_REQUEST, "이미 가입된 그룹입니다."),
    GROUP_ALREADY_LIKE(HttpStatus.BAD_REQUEST, "이미 좋아요를 누르셨습니다."),
    GROUP_ALREADY_DELETE_CONTENT(HttpStatus.BAD_REQUEST, "이미 삭제된 게시글 입니다."),
    GROUP_NOT_LEADER(HttpStatus.BAD_REQUEST, "그룹의 생성자가 아니므로 해제 권한이 없습니다."),
    GROUP_NOT_SAME_CONTENT_AUTHOR(HttpStatus.BAD_REQUEST, "게시글의 작성자가 아닙니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
