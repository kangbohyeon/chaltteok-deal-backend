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
        ): CommentResponse = CommentResponse(
            commentUuid = comment.commentUuid,
            userId = comment.userId,
            content = comment.content,
            rating = comment.rating,
            isSecret = comment.isSecret,
            isOwnerReply = comment.isOwnerReply,
            replies = replies.map { from(it, emptyList(), requestingUserId) },
            createdAt = comment.createdAt,
            isMine = requestingUserId == comment.userId,
        )
    }
}
