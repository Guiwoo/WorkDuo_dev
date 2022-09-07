package com.workduo.group.group.service;

import com.workduo.group.group.dto.CreateGroup;

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
}
