package com.group.group.service;

import com.core.domain.group.dto.GroupDto;
import com.core.domain.group.dto.GroupParticipantsDto;
import com.core.domain.group.dto.UpdateGroup;
import com.group.group.dto.CreateGroup;
import com.group.group.dto.GroupThumbnail;
import com.group.group.dto.ListGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface GroupService {

    /**
     * 그룹 생성
     * @param request
     */
    void createGroup(CreateGroup.Request request, List<MultipartFile> multipartFiles);

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
     * @return
     */
    Page<GroupDto> groupList(Pageable pageable, ListGroup.Request condition);

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

    /**
     * 그룹 참여
     * @param groupId
     */
    void groupParticipant(Long groupId);

    /**
     * 그룹 참여자 리스트
     * @param pageable
     * @param groupId
     * @return
     */
    Page<GroupParticipantsDto> groupParticipantList(Pageable pageable, Long groupId);

    /**
     * 그룹 썸네일 수정
     * @param groupId
     * @param multipartFiles
     * @return
     */
    GroupThumbnail groupThumbnailUpdate(Long groupId, List<MultipartFile> multipartFiles);

    /**
     * 그룹 수정
     * @param groupId
     * @param request
     * @return
     */
    UpdateGroup.Response groupUpdate(Long groupId, UpdateGroup.Request request);

}
