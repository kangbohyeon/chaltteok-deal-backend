package com.chaltteok.owner.dailystock.dto

import com.chaltteok.core.domain.DailyStock
import java.time.LocalDate
import java.time.LocalDateTime

data class OwnerDailyStockListResponse(
    val uuid: String,
    val productUuid: String,
    val productName: String,
    val optionUuid: String,
    val saleDate: LocalDate,
    val stockType: String,
    val salePrice: Int,
    val totalQty: Int,
    val remainStock: Int,
    val status: String,
    val startAt: LocalDateTime?,
    val endAt: LocalDateTime?,
    val maxPurchaseCount: Int,
) {
    companion object {
        fun from(stock: DailyStock, optionUuid: String) = OwnerDailyStockListResponse(
            uuid = stock.stockUuid,
            productUuid = stock.product.productUuid,
            productName = stock.product.name,
            optionUuid = optionUuid,
            saleDate = stock.saleDate,
            stockType = stock.stockType,
            salePrice = stock.salePrice,
            totalQty = stock.totalQty,
            remainStock = stock.remainStock,
            status = stock.status.name,
            startAt = stock.startAt,
            endAt = stock.endAt,
            maxPurchaseCount = stock.maxPurchaseCount,
        )
    }
}
