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
    GROUP_NOT_FOUND_COMMENT(HttpStatus.BAD_REQUEST, "그룹에 해당하는 댓글이 없습니다."),
    GROUP_MAXIMUM_EXCEEDED(HttpStatus.BAD_REQUEST, "그룹생성 최대개수를 초과하였습니다."),
    GROUP_MAXIMUM_PARTICIPANT(HttpStatus.BAD_REQUEST, "그룹의 정원이 초과하였습니다."),
    GROUP_LEADER_NOT_WITHDRAW(HttpStatus.BAD_REQUEST, "그룹장은 그룹을 탈퇴할 수 없습니다."),
    GROUP_ALREADY_WITHDRAW(HttpStatus.BAD_REQUEST, "이미 탈퇴한 그룹입니다."),
    GROUP_ALREADY_DELETE_GROUP(HttpStatus.BAD_REQUEST, "이미 삭제된 그룹입니다."),
    GROUP_ALREADY_PARTICIPANT(HttpStatus.BAD_REQUEST, "이미 가입된 그룹입니다."),
    GROUP_ALREADY_LIKE(HttpStatus.BAD_REQUEST, "이미 좋아요를 누르셨습니다."),
    GROUP_ALREADY_DELETE_CONTENT(HttpStatus.BAD_REQUEST, "이미 삭제된 게시글 입니다."),
    GROUP_ALREADY_DELETE_COMMENT(HttpStatus.BAD_REQUEST, "이미 삭제된 댓글 입니다."),
    GROUP_NOT_LEADER(HttpStatus.BAD_REQUEST, "그룹의 생성자가 아니므로 해제 권한이 없습니다."),
    GROUP_NOT_SAME_AUTHOR(HttpStatus.BAD_REQUEST, "작성자가 아닙니다."),
    GROUP_MEETING_START_TIME_IS_AFTER(HttpStatus.BAD_REQUEST, "모임의 끝나는 시간이 시작 시간보다 빠를 수 없습니다."),
    GROUP_MEETING_TIME_NOT_HOUR(HttpStatus.BAD_REQUEST, "모임의 시작 시간과 끝나는 시간이 정각이 아닙니다."),
    GROUP_MEETING_DUPLICATION(HttpStatus.BAD_REQUEST, "동시간대에 참여한 모임이 있거나 생성한 모임이 있습니다."),
    GROUP_MEETING_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 모임은 없는 모임입니다."),
    GROUP_MEETING_ALREADY_DELETE(HttpStatus.BAD_REQUEST, "해당 모임은 삭제된 모임입니다."),
    GROUP_MEETING_LESS_THEN_PARTICIPANT(HttpStatus.BAD_REQUEST, "모임의 정원이 모임의 참여 인원보다 적을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
