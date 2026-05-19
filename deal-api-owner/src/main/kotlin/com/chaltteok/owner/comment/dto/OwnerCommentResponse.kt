package com.chaltteok.owner.comment.dto

import com.chaltteok.core.domain.Comment
import java.time.LocalDateTime

class OwnerCommentResponse(
    val commentId: Long,
    val commentUuid: String,
    val productUuid: String,
    val productName: String,
    val userId: Long,
    val content: String,
    val rating: Int?,
    val isSecret: Boolean,
    val isOwnerReply: Boolean,
    val parentId: Long?,
    val replies: List<OwnerCommentResponse>,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun from(comment: Comment) = OwnerCommentResponse(
            commentId = comment.id!!,
            commentUuid = comment.commentUuid,
            productUuid = comment.product.productUuid,
            productName = comment.product.name,
            userId = comment.userId,
            content = comment.content,
            rating = comment.rating,
            isSecret = comment.isSecret,
            isOwnerReply = comment.isOwnerReply,
            parentId = comment.parentId,
            replies = emptyList(),
            createdAt = comment.createdAt,
        )

        fun fromWithReplies(comment: Comment, replies: List<Comment>) = OwnerCommentResponse(
            commentId = comment.id!!,
            commentUuid = comment.commentUuid,
            productUuid = comment.product.productUuid,
            productName = comment.product.name,
            userId = comment.userId,
            content = comment.content,
            rating = comment.rating,
            isSecret = comment.isSecret,
            isOwnerReply = comment.isOwnerReply,
            parentId = comment.parentId,
            replies = replies.map { from(it) },
            createdAt = comment.createdAt,
        )
    }
}
