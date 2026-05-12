package com.chaltteok.owner.dashboard.dto

class TopProductsResponse(
    val products: List<TopProductItem>,
)

class TopProductItem(
    val productUuid: String,
    val productName: String,
    val totalQty: Long,
    val totalRevenue: Long,
)
