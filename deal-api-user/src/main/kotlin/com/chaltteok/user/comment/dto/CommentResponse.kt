package com.chaltteok.user.comment.dto

import com.chaltteok.core.domain.Comment
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

class CommentResponse(
    val commentUuid: String,
    val nickname: String?,
    val content: String,
    val rating: Int?,
    @get:JsonProperty("isSecret") val isSecret: Boolean,
    @get:JsonProperty("isOwnerReply") val isOwnerReply: Boolean,
    val replies: List<CommentResponse>,
    val createdAt: LocalDateTime,
    @get:JsonProperty("isMine") val isMine: Boolean,
) {
    companion object {
        fun from(
            comment: Comment,
            replies: List<Comment> = emptyList(),
            requestingUserId: Long?,
            nicknameMap: Map<Long, String> = emptyMap(),
        ): CommentResponse = CommentResponse(
            commentUuid = comment.commentUuid,
            nickname = if (comment.isOwnerReply) null else nicknameMap[comment.userId],
            content = comment.content,
            rating = comment.rating,
            isSecret = comment.isSecret,
            isOwnerReply = comment.isOwnerReply,
            replies = replies.map { from(it, emptyList(), requestingUserId, nicknameMap) },
            createdAt = comment.createdAt,
            isMine = requestingUserId == comment.userId,
        )
    }
}
