package com.chaltteok.user.comment.service

import com.chaltteok.common.exception.BusinessException
import com.chaltteok.core.domain.Comment
import com.chaltteok.core.repository.comment.CommentRepository
import com.chaltteok.core.repository.product.ProductRepository
import com.chaltteok.user.comment.dto.CommentRequest
import com.chaltteok.user.comment.dto.CommentResponse
import com.chaltteok.user.comment.dto.ReplyRequest
import com.chaltteok.user.comment.enums.CommentErrorCode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserCommentServiceImpl(
    private val commentRepository: CommentRepository,
    private val productRepository: ProductRepository,
) : UserCommentService {

    @Transactional(readOnly = true)
    override fun getComments(productUuid: String, requestingUserId: Long?): List<CommentResponse> {
        val roots = commentRepository.findRootCommentsByProductUuid(productUuid)
            .filter { !it.isSecret || it.userId == requestingUserId }
        if (roots.isEmpty()) return emptyList()
        val replyMap = commentRepository.findRepliesByParentIds(roots.mapNotNull { it.id })
            .filter { !it.isSecret || it.userId == requestingUserId }
            .groupBy { it.parentId }
        return roots.map { root ->
            CommentResponse.from(root, replyMap[root.id] ?: emptyList(), requestingUserId)
        }
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
        return CommentResponse.from(comment, emptyList(), userId)
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
        return CommentResponse.from(reply, emptyList(), userId)
    }

    @Transactional
    override fun update(commentUuid: String, userId: Long, request: CommentRequest): CommentResponse {
        val comment = commentRepository.findByCommentUuid(commentUuid)
            ?: throw BusinessException(CommentErrorCode.COMMENT_NOT_FOUND)
        if (comment.userId != userId) throw BusinessException(CommentErrorCode.COMMENT_ACCESS_DENIED)
        comment.content = request.content
        comment.rating = request.rating
        comment.isSecret = request.isSecret
        return CommentResponse.from(comment, emptyList(), userId)
    }

    @Transactional
    override fun delete(commentUuid: String, userId: Long) {
        val comment = commentRepository.findByCommentUuid(commentUuid)
            ?: throw BusinessException(CommentErrorCode.COMMENT_NOT_FOUND)
        if (comment.userId != userId) throw BusinessException(CommentErrorCode.COMMENT_ACCESS_DENIED)
        commentRepository.delete(comment)
    }
}
