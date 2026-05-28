package com.chaltteok.owner.comment.dto

import com.chaltteok.core.domain.Comment
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

class OwnerCommentResponse(
    val commentId: Long,
    val commentUuid: String,
    val productUuid: String,
    val productName: String,
    val userUuid: String,
    val content: String,
    val rating: Int?,
    @get:JsonProperty("isSecret") val isSecret: Boolean,
    @get:JsonProperty("isOwnerReply") val isOwnerReply: Boolean,
    val parentId: Long?,
    val replies: List<OwnerCommentResponse>,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun from(comment: Comment, userUuid: String = "") = OwnerCommentResponse(
            commentId = comment.id!!,
            commentUuid = comment.commentUuid,
            productUuid = comment.product.productUuid,
            productName = comment.product.name,
            userUuid = userUuid,
            content = comment.content,
            rating = comment.rating,
            isSecret = comment.isSecret,
            isOwnerReply = comment.isOwnerReply,
            parentId = comment.parentId,
            replies = emptyList(),
            createdAt = comment.createdAt,
        )

        fun fromWithReplies(
            comment: Comment,
            replies: List<Comment>,
            uuidMap: Map<Long, String>,
        ) = OwnerCommentResponse(
            commentId = comment.id!!,
            commentUuid = comment.commentUuid,
            productUuid = comment.product.productUuid,
            productName = comment.product.name,
            userUuid = uuidMap[comment.userId] ?: "",
            content = comment.content,
            rating = comment.rating,
            isSecret = comment.isSecret,
            isOwnerReply = comment.isOwnerReply,
            parentId = comment.parentId,
            replies = replies.map { from(it, if (it.isOwnerReply) "" else uuidMap[it.userId] ?: "") },
            createdAt = comment.createdAt,
        )
    }
}
