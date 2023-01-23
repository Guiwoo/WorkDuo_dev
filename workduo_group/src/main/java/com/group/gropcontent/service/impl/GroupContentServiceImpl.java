package com.group.gropcontent.service.impl;

import com.core.domain.common.CommonRequestContext;
import com.core.domain.group.entity.Group;
import com.core.domain.group.entity.GroupJoinMember;
import com.core.domain.group.type.GroupJoinMemberStatus;
import com.core.domain.group.type.GroupStatus;
import com.core.domain.groupContent.dto.GroupContentCommentDto;
import com.core.domain.groupContent.dto.GroupContentDto;
import com.core.domain.groupContent.dto.GroupContentImageDto;
import com.core.domain.groupContent.dto.UpdateGroupContent;
import com.core.domain.groupContent.entity.*;
import com.core.domain.member.entity.Member;
import com.core.domain.member.repository.MemberRepository;
import com.core.error.group.exception.GroupException;
import com.core.error.group.type.GroupErrorCode;
import com.core.error.member.exception.MemberException;
import com.core.error.member.type.MemberErrorCode;
import com.core.util.AwsS3Provider;
import com.group.gropcontent.dto.createGroupContentComment.CreateComment;
import com.group.gropcontent.dto.creategroupcontent.CreateGroupContent;
import com.group.gropcontent.dto.detailgroupcontent.DetailGroupContentDto;
import com.group.gropcontent.dto.updategroupcontentcomment.UpdateComment;
import com.group.gropcontent.repository.*;
import com.group.gropcontent.repository.query.GroupContentQueryRepository;
import com.group.gropcontent.service.GroupContentService;
import com.group.group.repository.GroupJoinMemberRepository;
import com.group.group.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupContentServiceImpl implements GroupContentService {

    private final GroupContentRepository groupContentRepository;
    private final GroupContentLikeRepository groupContentLikeRepository;
    private final GroupContentImageRepository groupContentImageRepository;
    private final GroupJoinMemberRepository groupJoinMemberRepository;
    private final GroupContentCommentRepository groupContentCommentRepository;
    private final GroupContentCommentLikeRepository groupContentCommentLikeRepository;
    private final GroupContentQueryRepository groupContentQueryRepository;
    private final MemberRepository memberRepository;
    private final GroupRepository groupRepository;
    private final AwsS3Provider awsS3Provider;
    private final CommonRequestContext context;
    private final EntityManager entityManager;

    /**
     * 그룹 피드 리스트
     * @param pageable
     * @param groupId
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public Page<GroupContentDto> groupContentList(Pageable pageable, Long groupId) {
        Member member = getMember(context.getMemberEmail());
        Group group = getGroup(groupId);

        commonGroupContentValidate(member, group);

        return groupContentQueryRepository.findByGroupContentList(pageable, groupId);
    }

    /**
     * 그룹 피드 생성
     * @param groupId
     * @param request
     */
    @Override
    @Transactional
    public void createGroupContent(
            Long groupId,
            CreateGroupContent.Request request,
            List<MultipartFile> multipartFiles) {
        Member member = getMember(context.getMemberEmail());
        Group group = getGroup(groupId);

        commonGroupContentValidate(member, group);

        GroupContent groupContent = GroupContent.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .deletedYn(false)
                .group(group)
                .member(member)
                .noticeYn(request.isNoticeYn())
                .sortValue(request.getSortValue())
                .build();

        groupContentRepository.save(groupContent);
        entityManager.flush();

        if (multipartFiles != null) {
            String path = generatePath(groupId, groupContent.getId());
            List<String> files = awsS3Provider.uploadFile(multipartFiles, path);
            List<GroupContentImage> groupContentImages =
                    GroupContentImage.createGroupContentImage(groupContent, files);

            groupContentImageRepository.saveAll(groupContentImages);
        }
    }

    /**
     * 그룹 피드 상세
     * @param groupId
     * @param groupContentId
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public DetailGroupContentDto detailGroupContent(Long groupId, Long groupContentId) {
        Member member = getMember(context.getMemberEmail());
        Group group = getGroup(groupId);
        GroupContent groupContent = getGroupContent(groupContentId);

        commonGroupContentValidate(member, group);
        detailGroupContentValidate(groupContent);

        GroupContentDto groupContentDto = groupContentQueryRepository.findByGroupContent(groupId, groupContentId)
                .orElseThrow(() -> new GroupException(GroupErrorCode.GROUP_NOT_FOUND_CONTENT));

        List<GroupContentImageDto> groupContentImages =
                groupContentQueryRepository.findByGroupContentImage(groupId, groupContentId);

        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<GroupContentCommentDto> groupContentComments =
                groupContentQueryRepository.findByGroupContentComments(pageRequest, groupId, groupContentId);

        return DetailGroupContentDto.from(
                groupContentDto,
                groupContentImages,
                groupContentComments
        );
    }

    /**
     * 그룹 피드 좋아요
     * @param groupId
     * @param groupContentId
     */
    @Override
    @Transactional
    public void groupContentLike(Long groupId, Long groupContentId) {
        Member member = getMember(context.getMemberEmail());
        Group group = getGroup(groupId);
        GroupContent groupContent = getGroupContent(groupContentId);

        commonGroupContentValidate(member, group);
        groupContentLikeValidate(member, group, groupContent);

        GroupContentLike groupContentLike = GroupContentLike.builder()
                .member(member)
                .groupContent(groupContent)
                .build();

        groupContentLikeRepository.save(groupContentLike);
    }

    /**
     * 그룹 피드 좋아요 취소
     * @param groupId
     * @param groupContentId
     */
    @Override
    @Transactional
    public void groupContentUnLike(Long groupId, Long groupContentId) {
        Member member = getMember(context.getMemberEmail());
        Group group = getGroup(groupId);
        GroupContent groupContent = getGroupContent(groupContentId);

        commonGroupContentValidate(member, group);
        groupContentUnLikeValidate(group, groupContent);

        groupContentLikeRepository.deleteByMemberAndGroupContent(member, groupContent);
    }

    /**
     * 그룹 피드 삭제
     */
    @Override
    @Transactional
    public void groupContentDelete(Long groupId, Long groupContentId) {
        Member member = getMember(context.getMemberEmail());
        Group group = getGroup(groupId);
        GroupContent groupContent = getGroupContent(groupContentId);

        commonGroupContentValidate(member, group);
        groupContentDeleteValidate(member, group, groupContent);

        groupContent.deleteContent();
    }

    /**
     * 그룹 피드 수정
     * @param request
     * @param groupId
     * @param groupContentId
     */
    @Override
    @Transactional
    public void groupContentUpdate(UpdateGroupContent.Request request, Long groupId, Long groupContentId) {
        Member member = getMember(context.getMemberEmail());
        Group group = getGroup(groupId);
        GroupContent groupContent = getGroupContent(groupContentId);

        commonGroupContentValidate(member, group);
        groupContentUpdateValidate(member, group, groupContent);

        groupContent.updateContent(request);
    }

    /**
     * 그룹 피드 댓글 리스트
     * @param pageable
     * @param groupId
     * @param groupContentId
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public Page<GroupContentCommentDto> groupContentCommentList(Pageable pageable, Long groupId, Long groupContentId) {
        Member member = getMember(context.getMemberEmail());
        Group group = getGroup(groupId);
        GroupContent groupContent = getGroupContent(groupContentId);

        commonGroupContentCommentValidate(member, group, groupContent);

        return groupContentQueryRepository.findByGroupContentComments(
                pageable,
                groupId,
                groupContentId
        );
    }

    /**
     * 그룹 피드 댓글 작성
     * @param groupId
     * @param groupContentId
     */
    @Override
    @Transactional
    public void createGroupContentComment(CreateComment.Request request, Long groupId, Long groupContentId) {
        Member member = getMember(context.getMemberEmail());
        Group group = getGroup(groupId);
        GroupContent groupContent = getGroupContent(groupContentId);

        commonGroupContentCommentValidate(member, group, groupContent);

        GroupContentComment comment = GroupContentComment.builder()
                .member(member)
                .groupContent(groupContent)
                .comment(request.getComment())
                .build();

        groupContentCommentRepository.save(comment);
    }

    /**
     * 그룹 피드 댓글 수정
     * @param groupId
     * @param groupContentId
     */
    @Override
    @Transactional
    public void updateGroupContentComment(
            UpdateComment.Request request,
            Long groupId,
            Long groupContentId,
            Long commentId) {
        Member member = getMember(context.getMemberEmail());
        Group group = getGroup(groupId);
        GroupContent groupContent = getGroupContent(groupContentId);

        commonGroupContentCommentValidate(member, group, groupContent);

        GroupContentComment comment = getAuthorComment(commentId, groupContent, member);

        commentValidate(member, comment);

        comment.updateComment(request.getComment());
    }

    /**
     * 그룹 피드 댓글 삭제
     * @param groupId
     * @param groupContentId
     */
    @Override
    @Transactional
    public void deleteGroupContentComment(Long groupId, Long groupContentId, Long commentId) {
        Member member = getMember(context.getMemberEmail());
        Group group = getGroup(groupId);
        GroupContent groupContent = getGroupContent(groupContentId);

        commonGroupContentCommentValidate(member, group, groupContent);

        GroupContentComment comment = getAuthorComment(commentId, groupContent, member);

        commentValidate(member, comment);

        comment.deleteComment();
    }

    /**
     * 그룹 피드 댓글 좋아요
     * @param groupId
     * @param groupContentId
     * @param commentId
     */
    @Override
    @Transactional
    public void groupContentCommentLike(Long groupId, Long groupContentId, Long commentId) {
        Member member = getMember(context.getMemberEmail());
        Group group = getGroup(groupId);
        GroupContent groupContent = getGroupContent(groupContentId);

        commonGroupContentCommentValidate(member, group, groupContent);
        GroupContentComment comment = getComment(commentId, groupContent);
        commentLikeValidate(member, comment);

        boolean exists = groupContentCommentLikeRepository.existsByGroupContentCommentAndMember(comment, member);
        if (exists) {
            throw new GroupException(GroupErrorCode.GROUP_ALREADY_LIKE);
        }

        GroupContentCommentLike commentLike = GroupContentCommentLike.builder()
                .groupContentComment(comment)
                .member(member)
                .build();

        groupContentCommentLikeRepository.save(commentLike);
    }

    /**
     * 그룹 피드 댓글 좋아요 취소
     * @param groupId
     * @param groupContentId
     * @param commentId
     */
    @Override
    @Transactional
    public void groupContentCommentUnLike(Long groupId, Long groupContentId, Long commentId) {
        Member member = getMember(context.getMemberEmail());
        Group group = getGroup(groupId);
        GroupContent groupContent = getGroupContent(groupContentId);

        commonGroupContentCommentValidate(member, group, groupContent);
        GroupContentComment comment = getComment(commentId, groupContent);
        commentLikeValidate(member, comment);

        groupContentCommentLikeRepository.deleteByGroupContentCommentAndMember(comment, member);
    }

    private void commentLikeValidate(Member member, GroupContentComment groupContentComment) {
        if (groupContentComment.isDeletedYn()) {
            throw new GroupException(GroupErrorCode.GROUP_ALREADY_DELETE_COMMENT);
        }
    }

    private void commentValidate(Member member, GroupContentComment groupContentComment) {
        if (member.getId() != groupContentComment.getMember().getId()) {
            throw new GroupException(GroupErrorCode.GROUP_NOT_SAME_AUTHOR);
        }

        if (groupContentComment.isDeletedYn()) {
            throw new GroupException(GroupErrorCode.GROUP_ALREADY_DELETE_COMMENT);
        }
    }

    private void commonGroupContentCommentValidate(Member member, Group group, GroupContent groupContent) {
        if (groupContent.isDeletedYn()) {
            throw new GroupException(GroupErrorCode.GROUP_ALREADY_DELETE_CONTENT);
        }

        if (groupContent.getGroup().getId() != group.getId()) {
            throw new GroupException(GroupErrorCode.GROUP_NOT_FOUND_CONTENT);
        }

        boolean exists = groupJoinMemberRepository.existsByGroupAndMember(group, member);
        if (!exists) {
            throw new GroupException(GroupErrorCode.GROUP_NOT_FOUND_USER);
        }

        if (group.getGroupStatus() != GroupStatus.GROUP_STATUS_ING) {
            throw new GroupException(GroupErrorCode.GROUP_ALREADY_DELETE_GROUP);
        }

        GroupJoinMember groupJoinMember = getGroupJoinMember(member, group);
        if (groupJoinMember.getGroupJoinMemberStatus() != GroupJoinMemberStatus.GROUP_JOIN_MEMBER_STATUS_ING) {
            throw new GroupException(GroupErrorCode.GROUP_ALREADY_WITHDRAW);
        }
    }

    private void commonGroupContentValidate(Member member, Group group) {
        if (group.getGroupStatus() != GroupStatus.GROUP_STATUS_ING) {
            throw new GroupException(GroupErrorCode.GROUP_ALREADY_DELETE_GROUP);
        }

        boolean exists = groupJoinMemberRepository.existsByGroupAndMember(group, member);
        if (!exists) {
            throw new GroupException(GroupErrorCode.GROUP_NOT_FOUND_USER);
        }

        GroupJoinMember groupJoinMember = getGroupJoinMember(member, group);
        if (groupJoinMember.getGroupJoinMemberStatus() != GroupJoinMemberStatus.GROUP_JOIN_MEMBER_STATUS_ING) {
            throw new GroupException(GroupErrorCode.GROUP_ALREADY_WITHDRAW);
        }
    }

    private void groupContentUpdateValidate (Member member, Group group, GroupContent groupContent) {
        if (groupContent.isDeletedYn()) {
            throw new GroupException(GroupErrorCode.GROUP_ALREADY_DELETE_CONTENT);
        }

        if (groupContent.getGroup().getId() != group.getId()) {
            throw new GroupException(GroupErrorCode.GROUP_NOT_FOUND_CONTENT);
        }

        if (member.getId() != groupContent.getMember().getId()) {
            throw new GroupException(GroupErrorCode.GROUP_NOT_SAME_AUTHOR);
        }
    }

    private void groupContentDeleteValidate(Member member, Group group, GroupContent groupContent) {
        if (groupContent.isDeletedYn()) {
            throw new GroupException(GroupErrorCode.GROUP_ALREADY_DELETE_CONTENT);
        }

        if (groupContent.getGroup().getId() != group.getId()) {
            throw new GroupException(GroupErrorCode.GROUP_NOT_FOUND_CONTENT);
        }

        if (member.getId() != groupContent.getMember().getId()) {
            throw new GroupException(GroupErrorCode.GROUP_NOT_SAME_AUTHOR);
        }
    }

    private void groupContentLikeValidate(Member member, Group group, GroupContent groupContent) {

        if (groupContent.isDeletedYn()) {
            throw new GroupException(GroupErrorCode.GROUP_ALREADY_DELETE_CONTENT);
        }

        if (groupContent.getGroup().getId() != group.getId()) {
            throw new GroupException(GroupErrorCode.GROUP_NOT_FOUND_CONTENT);
        }

        boolean exists = groupContentLikeRepository.existsByMemberAndGroupContent(member, groupContent);
        if (exists) {
            throw new GroupException(GroupErrorCode.GROUP_ALREADY_LIKE);
        }
    }

    private void groupContentUnLikeValidate(Group group, GroupContent groupContent) {

        if (groupContent.isDeletedYn()) {
            throw new GroupException(GroupErrorCode.GROUP_ALREADY_DELETE_CONTENT);
        }

        if (groupContent.getGroup().getId() != group.getId()) {
            throw new GroupException(GroupErrorCode.GROUP_NOT_FOUND_CONTENT);
        }
    }

    private void detailGroupContentValidate(GroupContent groupContent) {

        if (groupContent.isDeletedYn()) {
            throw new GroupException(GroupErrorCode.GROUP_ALREADY_DELETE_CONTENT);
        }
    }

    private Member getMember(String memberEmail) {
        return memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_EMAIL_ERROR));
    }

    private Group getGroup(Long groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupException(GroupErrorCode.GROUP_NOT_FOUND));
    }

    private GroupJoinMember getGroupJoinMember(Member member, Group group) {
        return groupJoinMemberRepository.findByMemberAndGroup(member, group)
                .orElseThrow(() -> new GroupException(GroupErrorCode.GROUP_NOT_FOUND_USER));
    }

    private GroupContent getGroupContent(Long groupContentId) {
        return groupContentRepository.findById(groupContentId)
                .orElseThrow(() -> new GroupException(GroupErrorCode.GROUP_NOT_FOUND_CONTENT));
    }

    private GroupContentComment getAuthorComment(Long commentId, GroupContent groupContent, Member member) {
        return groupContentCommentRepository.findByGroupContentCommentIdGroupContentAndMember(commentId, groupContent, member)
                .orElseThrow(() -> new GroupException(GroupErrorCode.GROUP_NOT_FOUND_COMMENT));
    }

    private GroupContentComment getComment(Long commentId, GroupContent groupContent) {
        return groupContentCommentRepository.findByIdAndGroupContent(commentId, groupContent)
                .orElseThrow(() -> new GroupException(GroupErrorCode.GROUP_NOT_FOUND_COMMENT));
    }

    public String generatePath(Long groupId, Long groupContentId) {
        return "com/group/" + groupId + "/content/" + groupContentId + "/";
    }
}
