package com.chaltteok.user.comment.service

import com.chaltteok.user.comment.dto.CommentPageResponse
import com.chaltteok.user.comment.dto.CommentRequest
import com.chaltteok.user.comment.dto.CommentResponse
import com.chaltteok.user.comment.dto.ReplyRequest

interface UserCommentService {
    fun getComments(productUuid: String, requestingUserId: Long?, page: Int, size: Int): CommentPageResponse
    fun create(productUuid: String, userId: Long, request: CommentRequest): CommentResponse
    fun reply(commentUuid: String, userId: Long, request: ReplyRequest): CommentResponse
    fun update(commentUuid: String, userId: Long, request: CommentRequest): CommentResponse
    fun delete(commentUuid: String, userId: Long)
}
