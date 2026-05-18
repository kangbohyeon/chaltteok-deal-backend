package com.chaltteok.owner.comment.service

import com.chaltteok.common.exception.BusinessException
import com.chaltteok.core.domain.Comment
import com.chaltteok.core.repository.comment.CommentRepository
import com.chaltteok.owner.comment.dto.OwnerCommentResponse
import com.chaltteok.owner.comment.dto.OwnerReplyRequest
import com.chaltteok.owner.comment.enums.OwnerCommentErrorCode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OwnerCommentServiceImpl(
    private val commentRepository: CommentRepository,
) : OwnerCommentService {

    @Transactional(readOnly = true)
    override fun getAll(): List<OwnerCommentResponse> =
        commentRepository.findAllOrderByCreatedAtDesc().map { OwnerCommentResponse.from(it) }

    @Transactional
    override fun delete(commentUuid: String) {
        val comment = commentRepository.findByCommentUuid(commentUuid)
            ?: throw BusinessException(OwnerCommentErrorCode.COMMENT_NOT_FOUND)
        commentRepository.delete(comment)
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
