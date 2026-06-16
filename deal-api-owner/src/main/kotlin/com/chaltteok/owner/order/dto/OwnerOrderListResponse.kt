package com.chaltteok.owner.order.dto

class OwnerOrderListResponse(
    val content: List<OwnerOrderSummaryResponse>,
    val totalElements: Long,
    val totalPages: Int,
    val currentPage: Int,
    val pageSize: Int,
)
