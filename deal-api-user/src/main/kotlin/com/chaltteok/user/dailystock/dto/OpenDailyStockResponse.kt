package com.chaltteok.user.dailystock.dto

import com.chaltteok.core.domain.DailyStock
import java.time.LocalDate

data class OpenDailyStockResponse(
    val id: Long,
    val productName: String,
    val price: Long,
    val saleDate: LocalDate,
    val remainStock: Int,
    val totalStock: Int,
) {
    companion object {
        fun from(dailyStock: DailyStock) = OpenDailyStockResponse(
            id = dailyStock.id!!,
            productName = dailyStock.product.name,
            price = dailyStock.product.price.toLong(),
            saleDate = dailyStock.saleDate,
            remainStock = dailyStock.remainStock,
            totalStock = dailyStock.totalQty,
        )
    }
}
