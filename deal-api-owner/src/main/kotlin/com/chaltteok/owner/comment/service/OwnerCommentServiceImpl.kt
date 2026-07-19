package com.chaltteok.owner.comment.service

import com.chaltteok.common.exception.BusinessException
import com.chaltteok.core.domain.Comment
import com.chaltteok.core.domain.enums.AttachmentType
import com.chaltteok.core.repository.attachment.AttachmentRepository
import com.chaltteok.core.repository.comment.CommentRepository
import com.chaltteok.core.repository.user.UserRepository
import com.chaltteok.owner.comment.dto.OwnerCommentPageResponse
import com.chaltteok.owner.comment.dto.OwnerCommentResponse
import com.chaltteok.owner.comment.dto.OwnerReplyRequest
import com.chaltteok.owner.comment.enums.OwnerCommentErrorCode
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OwnerCommentServiceImpl(
    private val commentRepository: CommentRepository,
    private val userRepository: UserRepository,
    private val attachmentRepository: AttachmentRepository,
) : OwnerCommentService {

    @Transactional(readOnly = true)
    override fun getAll(page: Int, size: Int): OwnerCommentPageResponse {
        val pageable = PageRequest.of(page, size, Sort.by("createdAt").descending())
        val rootPage = commentRepository.findRootCommentsPagedForOwner(pageable)
        val roots = rootPage.content
        val replies = if (roots.isNotEmpty())
            commentRepository.findRepliesByParentIds(roots.mapNotNull { it.id })
        else emptyList()

        val userIds = (roots + replies)
            .filter { !it.isOwnerReply }
            .map { it.userId }
            .distinct()
        val uuidMap = userRepository.findAllById(userIds).associate { it.id!! to it.userUuid }

        val allUuids = (roots + replies).map { it.commentUuid }
        val attachmentMap = if (allUuids.isNotEmpty()) {
            attachmentRepository.findAllByReferenceUuidInAndAttachmentType(allUuids, AttachmentType.COMMENT.name)
                .groupBy { it.referenceUuid!! }
        } else emptyMap()

        val replyMap = replies.groupBy { it.parentId }
        val content = roots.map { root ->
            OwnerCommentResponse.fromWithReplies(root, replyMap[root.id] ?: emptyList(), uuidMap, attachmentMap)
        }
        return OwnerCommentPageResponse(content, rootPage.totalElements, rootPage.totalPages, page, size)
    }

    @Transactional
    override fun delete(commentUuid: String) {
        val comment = commentRepository.findByCommentUuid(commentUuid)
            ?: throw BusinessException(OwnerCommentErrorCode.COMMENT_NOT_FOUND)
        if (comment.parentId == null) {
            val replies = commentRepository.findRepliesByParentIds(listOf(comment.id ?: error("comment id null")))
            if (replies.isNotEmpty()) {
                replies.forEach { attachmentRepository.deleteByReferenceUuidAndAttachmentType(it.commentUuid, AttachmentType.COMMENT.name) }
                commentRepository.deleteAll(replies)
            }
        }
        attachmentRepository.deleteByReferenceUuidAndAttachmentType(commentUuid, AttachmentType.COMMENT.name)
        commentRepository.delete(comment)
    }

    @Transactional
    override fun updateReply(commentUuid: String, request: OwnerReplyRequest): OwnerCommentResponse {
        val reply = findOwnerReply(commentUuid)
        reply.content = request.content
        return OwnerCommentResponse.from(reply)
    }

    @Transactional
    override fun deleteReply(commentUuid: String) {
        val reply = findOwnerReply(commentUuid)
        commentRepository.delete(reply)
    }

    // 존재하지 않는 UUID와 일반 댓글 UUID 모두 REPLY_NOT_FOUND로 통일 — UUID 존재 여부 노출 방지
    private fun findOwnerReply(commentUuid: String): Comment {
        val reply = commentRepository.findByCommentUuid(commentUuid)
            ?: throw BusinessException(OwnerCommentErrorCode.REPLY_NOT_FOUND)
        if (!reply.isOwnerReply) throw BusinessException(OwnerCommentErrorCode.REPLY_NOT_FOUND)
        return reply
    }

    @Transactional
    override fun reply(commentUuid: String, request: OwnerReplyRequest): OwnerCommentResponse {
        val parent = commentRepository.findByCommentUuid(commentUuid)
            ?: throw BusinessException(OwnerCommentErrorCode.COMMENT_NOT_FOUND)
        val reply = commentRepository.save(
            Comment(
                product = parent.product,
                userId = 0L,
                content = request.content,
                parentId = parent.id,
                isOwnerReply = true,
            )
        )
        return OwnerCommentResponse.from(reply)
    }
}
