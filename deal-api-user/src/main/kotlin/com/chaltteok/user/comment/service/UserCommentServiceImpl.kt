package com.chaltteok.user.comment.service

import com.chaltteok.common.exception.BusinessException
import com.chaltteok.core.domain.Comment
import com.chaltteok.core.domain.enums.AttachmentType
import com.chaltteok.core.repository.attachment.AttachmentRepository
import com.chaltteok.core.repository.comment.CommentRepository
import com.chaltteok.core.repository.product.ProductRepository
import com.chaltteok.core.repository.user.UserRepository
import com.chaltteok.user.comment.dto.CommentPageResponse
import com.chaltteok.user.comment.dto.CommentRequest
import com.chaltteok.user.comment.dto.CommentResponse
import com.chaltteok.user.comment.dto.ReplyRequest
import com.chaltteok.user.comment.enums.CommentErrorCode
import com.chaltteok.user.file.enums.FileErrorCode
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserCommentServiceImpl(
    private val commentRepository: CommentRepository,
    private val productRepository: ProductRepository,
    private val userRepository: UserRepository,
    private val attachmentRepository: AttachmentRepository,
) : UserCommentService {

    @Transactional(readOnly = true)
    override fun getComments(productUuid: String, requestingUserId: Long?, page: Int, size: Int): CommentPageResponse {
        val pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending())
        val rootPage = commentRepository.findRootCommentsByProductUuidPaged(productUuid, pageable)
        val roots = rootPage.content
        if (roots.isEmpty()) return CommentPageResponse(emptyList(), rootPage.totalElements, rootPage.totalPages, page, size)
        val replies = commentRepository.findRepliesByParentIds(roots.mapNotNull { it.id })
        val allComments = roots + replies
        val userIds = allComments.filter { !it.isOwnerReply }.map { it.userId }.distinct()
        val nicknameMap = userRepository.findAllById(userIds).associate { it.id!! to it.nickname }
        val allUuids = allComments.map { it.commentUuid }
        val attachmentMap = if (allUuids.isNotEmpty()) {
            attachmentRepository.findAllByReferenceUuidInAndAttachmentType(allUuids, AttachmentType.COMMENT.name)
                .groupBy { it.referenceUuid!! }
        } else emptyMap()
        val replyMap = replies.groupBy { it.parentId }
        val content = roots.map { root ->
            CommentResponse.from(root, replyMap[root.id] ?: emptyList(), requestingUserId, nicknameMap, attachmentMap)
        }
        return CommentPageResponse(content, rootPage.totalElements, rootPage.totalPages, page, size)
    }

    @Transactional
    override fun create(productUuid: String, userId: Long, request: CommentRequest): CommentResponse {
        val product = productRepository.findByProductUuid(productUuid)
            ?: throw BusinessException(CommentErrorCode.PRODUCT_NOT_FOUND)
        val comment = commentRepository.save(
            Comment(
                product = product,
                userId = userId,
                content = request.content,
                rating = request.rating,
                isSecret = request.isSecret,
            )
        )
        if (request.attachmentUuids.isNotEmpty()) {
            val updated = attachmentRepository.updateReferenceByUuids(
                request.attachmentUuids,
                comment.commentUuid,
                AttachmentType.COMMENT.name
            )
            if (updated != request.attachmentUuids.size) {
                throw BusinessException(FileErrorCode.ATTACHMENT_OWNERSHIP_VIOLATION)
            }
        }
        val nickname = userRepository.findById(userId).map { it.nickname }.orElse(null)
        return CommentResponse.from(comment, emptyList(), userId, if (nickname != null) mapOf(userId to nickname) else emptyMap())
    }

    @Transactional
    override fun reply(commentUuid: String, userId: Long, request: ReplyRequest): CommentResponse {
        val parent = commentRepository.findByCommentUuid(commentUuid)
            ?: throw BusinessException(CommentErrorCode.COMMENT_NOT_FOUND)
        val reply = commentRepository.save(
            Comment(
                product = parent.product,
                userId = userId,
                content = request.content,
                parentId = parent.id,
            )
        )
        val nickname = userRepository.findById(userId).map { it.nickname }.orElse(null)
        return CommentResponse.from(reply, emptyList(), userId, if (nickname != null) mapOf(userId to nickname) else emptyMap())
    }

    @Transactional
    override fun update(commentUuid: String, userId: Long, request: CommentRequest): CommentResponse {
        val comment = commentRepository.findByCommentUuid(commentUuid)
            ?: throw BusinessException(CommentErrorCode.COMMENT_NOT_FOUND)
        if (comment.userId != userId) throw BusinessException(CommentErrorCode.COMMENT_ACCESS_DENIED)
        comment.content = request.content
        comment.rating = request.rating
        comment.isSecret = request.isSecret
        if (request.attachmentUuids.isNotEmpty()) {
            val updated = attachmentRepository.updateReferenceByUuids(
                request.attachmentUuids,
                comment.commentUuid,
                AttachmentType.COMMENT.name
            )
            if (updated != request.attachmentUuids.size) {
                throw BusinessException(FileErrorCode.ATTACHMENT_OWNERSHIP_VIOLATION)
            }
        }
        val nickname = userRepository.findById(userId).map { it.nickname }.orElse(null)
        return CommentResponse.from(comment, emptyList(), userId, if (nickname != null) mapOf(userId to nickname) else emptyMap())
    }

    @Transactional
    override fun delete(commentUuid: String, userId: Long) {
        val comment = commentRepository.findByCommentUuid(commentUuid)
            ?: throw BusinessException(CommentErrorCode.COMMENT_NOT_FOUND)
        if (comment.userId != userId) throw BusinessException(CommentErrorCode.COMMENT_ACCESS_DENIED)
        commentRepository.delete(comment)
    }
}
