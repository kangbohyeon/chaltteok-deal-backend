package com.chaltteok.owner.comment.service

import com.chaltteok.owner.comment.dto.OwnerCommentResponse
import com.chaltteok.owner.comment.dto.OwnerReplyRequest

interface OwnerCommentService {
    fun getAll(): List<OwnerCommentResponse>
    fun delete(commentUuid: String)
    fun reply(commentUuid: String, request: OwnerReplyRequest): OwnerCommentResponse
}
