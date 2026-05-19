package com.chaltteok.user.comment.dto

class CommentPageResponse(
    val content: List<CommentResponse>,
    val totalElements: Long,
    val totalPages: Int,
    val currentPage: Int,
    val pageSize: Int,
)
