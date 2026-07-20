package com.chaltteok.owner.comment.service

import com.chaltteok.owner.comment.dto.OwnerCommentPageResponse
import com.chaltteok.owner.comment.dto.OwnerCommentResponse
import com.chaltteok.owner.comment.dto.OwnerReplyRequest

interface OwnerCommentService {
    fun getAll(page: Int, size: Int): OwnerCommentPageResponse
    fun delete(commentUuid: String, ownerId: Long)
    fun reply(commentUuid: String, request: OwnerReplyRequest, ownerId: Long): OwnerCommentResponse
    fun updateReply(commentUuid: String, request: OwnerReplyRequest, ownerId: Long): OwnerCommentResponse
    fun deleteReply(commentUuid: String, ownerId: Long)
}
