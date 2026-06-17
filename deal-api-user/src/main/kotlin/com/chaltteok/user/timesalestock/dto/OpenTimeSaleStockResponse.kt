package com.chaltteok.user.timesalestock.dto

import com.chaltteok.core.domain.TimeSaleStock
import com.chaltteok.core.domain.enums.TimeSaleStockStatus
import java.time.LocalDate
import java.time.LocalDateTime

data class OpenTimeSaleStockResponse(
    val uuid: String,
    val productUuid: String,
    val productName: String,
    val price: Long,
    val status: TimeSaleStockStatus,
    val saleDate: LocalDate,
    val remainStock: Int,
    val totalStock: Int,
    val startAt: LocalDateTime?,
    val endAt: LocalDateTime?,
    val maxPurchaseCount: Int?,
) {
    companion object {
        fun from(timeSaleStock: TimeSaleStock) = OpenTimeSaleStockResponse(
            uuid = timeSaleStock.stockUuid,
            productUuid = timeSaleStock.product.productUuid,
            productName = timeSaleStock.product.name,
            price = timeSaleStock.salePrice.toLong(),
            status = timeSaleStock.status,
            saleDate = timeSaleStock.saleDate,
            remainStock = timeSaleStock.remainStock,
            totalStock = timeSaleStock.totalQty,
            startAt = timeSaleStock.startAt,
            endAt = timeSaleStock.endAt,
            maxPurchaseCount = timeSaleStock.maxPurchaseCount,
        )
    }
}
