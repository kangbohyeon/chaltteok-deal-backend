package com.chaltteok.user.comment.dto

import com.chaltteok.core.domain.Comment
import java.time.LocalDateTime

class CommentResponse(
    val commentUuid: String,
    val userId: Long,
    val content: String,
    val rating: Int?,
    val isSecret: Boolean,
    val isOwnerReply: Boolean,
    val replies: List<CommentResponse>,
    val createdAt: LocalDateTime,
    val isMine: Boolean,
) {
    companion object {
        fun from(
            comment: Comment,
            replies: List<Comment> = emptyList(),
            requestingUserId: Long?,
            isOwner: Boolean = false,
        ): CommentResponse {
            val secretMasked = comment.isSecret && requestingUserId != comment.userId && !isOwner
            return CommentResponse(
                commentUuid = comment.commentUuid,
                userId = comment.userId,
                content = if (secretMasked) "비밀 댓글입니다." else comment.content,
                rating = comment.rating,
                isSecret = comment.isSecret,
                isOwnerReply = comment.isOwnerReply,
                replies = replies.map { from(it, emptyList(), requestingUserId, isOwner) },
                createdAt = comment.createdAt,
                isMine = requestingUserId == comment.userId,
            )
        }
    }
}
