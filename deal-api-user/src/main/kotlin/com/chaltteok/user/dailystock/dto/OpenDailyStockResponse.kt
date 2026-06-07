package com.chaltteok.user.dailystock.dto

import com.chaltteok.core.domain.DailyStock
import java.time.LocalDate
import java.time.LocalDateTime

data class OpenDailyStockResponse(
    val uuid: String,
    val productUuid: String,
    val productName: String,
    val price: Long,
    val saleDate: LocalDate,
    val remainStock: Int,
    val totalStock: Int,
    val startAt: LocalDateTime?,
    val endAt: LocalDateTime?,
    val maxPurchaseCount: Int,
) {
    companion object {
        fun from(dailyStock: DailyStock) = OpenDailyStockResponse(
            uuid = dailyStock.stockUuid,
            productUuid = dailyStock.product.productUuid,
            productName = dailyStock.product.name,
            price = dailyStock.product.price.toLong(),
            saleDate = dailyStock.saleDate,
            remainStock = dailyStock.remainStock,
            totalStock = dailyStock.totalQty,
            startAt = dailyStock.startAt,
            endAt = dailyStock.endAt,
            maxPurchaseCount = dailyStock.maxPurchaseCount,
        )
    }
}
