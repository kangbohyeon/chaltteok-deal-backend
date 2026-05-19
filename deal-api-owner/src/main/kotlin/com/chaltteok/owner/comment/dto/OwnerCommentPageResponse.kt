package com.chaltteok.owner.comment.dto

class OwnerCommentPageResponse(
    val content: List<OwnerCommentResponse>,
    val totalElements: Long,
    val totalPages: Int,
    val currentPage: Int,
    val pageSize: Int,
)
