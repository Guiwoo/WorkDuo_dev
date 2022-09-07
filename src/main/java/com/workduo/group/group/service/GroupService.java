package com.workduo.group.group.service;

import com.workduo.group.group.dto.CreateGroup;
import com.workduo.group.group.dto.GroupDto;
import com.workduo.group.group.dto.GroupListDto;

public interface GroupService {

    /**
     * 그룹 생성
     * @param request
     */
    void createGroup(CreateGroup.Request request);

    /**
     * 그룹 해지 - 그룹장만 가능
     * @param groupId
     */
    void deleteGroup(Long groupId);

    /**
     * 그룹 탈퇴 - 그룹장은 불가능
     * @param groupId
     */
    void withdrawGroup(Long groupId);

    /**
     * 그룹 상세
     * @param groupId
     * @return
     */
    GroupDto groupDetail(Long groupId);

    /**
     * 그룹 리스트
     * @param page
     * @param offset
     * @return
     */
    GroupListDto groupList(int page, int offset);

    /**
     * 그룹 좋아요
     * @param groupId
     */
    void groupLike(Long groupId);

    /**
     * 그룹 좋아요 취소
     * @param groupId
     */
    void groupUnLike(Long groupId);
}
