package com.chaltteok.owner.inquiry.dto

class OwnerInquiryPageResponse(
    val content: List<OwnerInquiryResponse>,
    val totalElements: Long,
    val totalPages: Int,
    val currentPage: Int,
    val pageSize: Int,
)
