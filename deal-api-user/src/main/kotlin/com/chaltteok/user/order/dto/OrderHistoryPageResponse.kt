package com.chaltteok.user.order.dto

data class OrderHistoryPageResponse(
    val content: List<OrderHistoryResponse>,
    val totalElements: Long,
    val totalPages: Int,
    val currentPage: Int,
    val pageSize: Int,
)
