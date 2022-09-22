package com.workduo.group.gropcontent.service.impl;

import com.workduo.common.CommonRequestContext;
import com.workduo.error.group.exception.GroupException;
import com.workduo.error.member.exception.MemberException;
import com.workduo.group.gropcontent.dto.createGroupContentComment.CreateComment;
import com.workduo.group.gropcontent.dto.creategroupcontent.CreateGroupContent;
import com.workduo.group.gropcontent.dto.detailgroupcontent.DetailGroupContentDto;
import com.workduo.group.gropcontent.dto.detailgroupcontent.GroupContentCommentDto;
import com.workduo.group.gropcontent.dto.detailgroupcontent.GroupContentDto;
import com.workduo.group.gropcontent.dto.detailgroupcontent.GroupContentImageDto;
import com.workduo.group.gropcontent.dto.updategroupcontent.UpdateContent;
import com.workduo.group.gropcontent.dto.updategroupcontentcomment.UpdateComment;
import com.workduo.group.gropcontent.entity.GroupContent;
import com.workduo.group.gropcontent.entity.GroupContentComment;
import com.workduo.group.gropcontent.entity.GroupContentImage;
import com.workduo.group.gropcontent.entity.GroupContentLike;
import com.workduo.group.gropcontent.repository.GroupContentCommentRepository;
import com.workduo.group.gropcontent.repository.GroupContentImageRepository;
import com.workduo.group.gropcontent.repository.GroupContentLikeRepository;
import com.workduo.group.gropcontent.repository.GroupContentRepository;
import com.workduo.group.gropcontent.repository.query.GroupContentQueryRepository;
import com.workduo.group.gropcontent.service.GroupContentService;
import com.workduo.group.group.entity.Group;
import com.workduo.group.group.entity.GroupJoinMember;
import com.workduo.group.group.repository.GroupJoinMemberRepository;
import com.workduo.group.group.repository.GroupRepository;
import com.workduo.member.member.entity.Member;
import com.workduo.member.member.repository.MemberRepository;
import com.workduo.util.AwsS3Utils;
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

import static com.workduo.error.group.type.GroupErrorCode.*;
import static com.workduo.error.member.type.MemberErrorCode.MEMBER_EMAIL_ERROR;
import static com.workduo.group.group.type.GroupJoinMemberStatus.GROUP_JOIN_MEMBER_STATUS_ING;
import static com.workduo.group.group.type.GroupStatus.GROUP_STATUS_ING;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupContentServiceImpl implements GroupContentService {

    private final GroupContentRepository groupContentRepository;
    private final GroupContentLikeRepository groupContentLikeRepository;
    private final GroupContentImageRepository groupContentImageRepository;
    private final GroupJoinMemberRepository groupJoinMemberRepository;
    private final GroupContentCommentRepository groupContentCommentRepository;
    private final GroupContentQueryRepository groupContentQueryRepository;
    private final MemberRepository memberRepository;
    private final GroupRepository groupRepository;
    private final AwsS3Utils awsS3Utils;
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
            List<String> files = awsS3Utils.uploadFile(multipartFiles, path);
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
                .orElseThrow(() -> new GroupException(GROUP_NOT_FOUND_CONTENT));

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
    public void groupContentUpdate(UpdateContent.Request request, Long groupId, Long groupContentId) {
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

        GroupContentComment comment = getComment(commentId, groupContent, member);

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

        GroupContentComment comment = getComment(commentId, groupContent, member);

        commentValidate(member, comment);

        comment.deleteComment();
    }

    private void commentValidate(Member member, GroupContentComment groupContentComment) {
        if (member.getId() != groupContentComment.getMember().getId()) {
            throw new GroupException(GROUP_NOT_SAME_AUTHOR);
        }

        if (groupContentComment.isDeletedYn()) {
            throw new GroupException(GROUP_ALREADY_DELETE_COMMENT);
        }
    }

    private void commonGroupContentCommentValidate(Member member, Group group, GroupContent groupContent) {
        if (groupContent.isDeletedYn()) {
            throw new GroupException(GROUP_ALREADY_DELETE_CONTENT);
        }

        if (groupContent.getGroup().getId() != group.getId()) {
            throw new GroupException(GROUP_NOT_FOUND_CONTENT);
        }

        boolean exists = groupJoinMemberRepository.existsByGroupAndMember(group, member);
        if (!exists) {
            throw new GroupException(GROUP_NOT_FOUND_USER);
        }

        if (group.getGroupStatus() != GROUP_STATUS_ING) {
            throw new GroupException(GROUP_ALREADY_DELETE_GROUP);
        }

        GroupJoinMember groupJoinMember = getGroupJoinMember(member, group);
        if (groupJoinMember.getGroupJoinMemberStatus() != GROUP_JOIN_MEMBER_STATUS_ING) {
            throw new GroupException(GROUP_ALREADY_WITHDRAW);
        }
    }

    private void commonGroupContentValidate(Member member, Group group) {
        if (group.getGroupStatus() != GROUP_STATUS_ING) {
            throw new GroupException(GROUP_ALREADY_DELETE_GROUP);
        }

        boolean exists = groupJoinMemberRepository.existsByGroupAndMember(group, member);
        if (!exists) {
            throw new GroupException(GROUP_NOT_FOUND_USER);
        }

        GroupJoinMember groupJoinMember = getGroupJoinMember(member, group);
        if (groupJoinMember.getGroupJoinMemberStatus() != GROUP_JOIN_MEMBER_STATUS_ING) {
            throw new GroupException(GROUP_ALREADY_WITHDRAW);
        }
    }

    private void groupContentUpdateValidate (Member member, Group group, GroupContent groupContent) {
        if (groupContent.isDeletedYn()) {
            throw new GroupException(GROUP_ALREADY_DELETE_CONTENT);
        }

        if (groupContent.getGroup().getId() != group.getId()) {
            throw new GroupException(GROUP_NOT_FOUND_CONTENT);
        }

        if (member.getId() != groupContent.getMember().getId()) {
            throw new GroupException(GROUP_NOT_SAME_AUTHOR);
        }
    }

    private void groupContentDeleteValidate(Member member, Group group, GroupContent groupContent) {
        if (groupContent.isDeletedYn()) {
            throw new GroupException(GROUP_ALREADY_DELETE_CONTENT);
        }

        if (groupContent.getGroup().getId() != group.getId()) {
            throw new GroupException(GROUP_NOT_FOUND_CONTENT);
        }

        if (member.getId() != groupContent.getMember().getId()) {
            throw new GroupException(GROUP_NOT_SAME_AUTHOR);
        }
    }

    private void groupContentLikeValidate(Member member, Group group, GroupContent groupContent) {

        if (groupContent.isDeletedYn()) {
            throw new GroupException(GROUP_ALREADY_DELETE_CONTENT);
        }

        if (groupContent.getGroup().getId() != group.getId()) {
            throw new GroupException(GROUP_NOT_FOUND_CONTENT);
        }

        boolean exists = groupContentLikeRepository.existsByMemberAndGroupContent(member, groupContent);
        if (exists) {
            throw new GroupException(GROUP_ALREADY_LIKE);
        }
    }

    private void groupContentUnLikeValidate(Group group, GroupContent groupContent) {

        if (groupContent.isDeletedYn()) {
            throw new GroupException(GROUP_ALREADY_DELETE_CONTENT);
        }

        if (groupContent.getGroup().getId() != group.getId()) {
            throw new GroupException(GROUP_NOT_FOUND_CONTENT);
        }
    }

    private void detailGroupContentValidate(GroupContent groupContent) {

        if (groupContent.isDeletedYn()) {
            throw new GroupException(GROUP_ALREADY_DELETE_CONTENT);
        }
    }

    private Member getMember(String memberEmail) {
        return memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new MemberException(MEMBER_EMAIL_ERROR));
    }

    private Group getGroup(Long groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupException(GROUP_NOT_FOUND));
    }

    private GroupJoinMember getGroupJoinMember(Member member, Group group) {
        return groupJoinMemberRepository.findByMemberAndGroup(member, group)
                .orElseThrow(() -> new GroupException(GROUP_NOT_FOUND_USER));
    }

    private GroupContent getGroupContent(Long groupContentId) {
        return groupContentRepository.findById(groupContentId)
                .orElseThrow(() -> new GroupException(GROUP_NOT_FOUND_CONTENT));
    }

    private GroupContentComment getComment(Long commentId, GroupContent groupContent, Member member) {
        return groupContentCommentRepository.findByGroupContentAndMember(commentId, groupContent, member)
                .orElseThrow(() -> new GroupException(GROUP_NOT_FOUND_COMMENT));
    }

    public String generatePath(Long groupId, Long groupContentId) {
        return "group/" + groupId + "/content/" + groupContentId + "/";
    }
}
