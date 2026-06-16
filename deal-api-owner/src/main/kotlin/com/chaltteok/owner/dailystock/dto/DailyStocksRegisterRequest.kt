package com.chaltteok.owner.dailystock.dto

import com.chaltteok.core.domain.DailyStock
import com.chaltteok.core.domain.Product
import com.chaltteok.core.domain.enums.DailyStockStatus
import com.chaltteok.owner.dailystock.enums.DailyStockType
import jakarta.validation.constraints.NotNull
import java.time.LocalDate
import java.time.LocalDateTime

data class DailyStocksRegisterRequest(
    @field:NotNull(message = "option id must not be null")
    val optionId: String,

    @field:NotNull(message = "sale date must not be null")
    val saleDate: LocalDate,

    val stockType: DailyStockType? = DailyStockType.NORMAL,

    val salePrice: Int?,

    @field:NotNull(message = "sale amount must not be null")
    val totalQty: Int,

    val startAt: LocalDateTime? = null,
    val endAt: LocalDateTime? = null,
    val maxPurchaseCount: Int? = null,
) {
    init {
        if ((stockType ?: DailyStockType.NORMAL) == DailyStockType.TIMESALE) {
            require(startAt != null) { "startAt is required for TIMESALE stock" }
            require(endAt != null) { "endAt is required for TIMESALE stock" }
        }
    }

    fun toDailyStockEntity(product: Product, salePrice: Int, status: DailyStockStatus = DailyStockStatus.OPEN): DailyStock {
        val finalStockType = (this.stockType ?: DailyStockType.NORMAL).name

        return DailyStock(
            product = product,
            saleDate = saleDate,
            stockType = finalStockType,
            salePrice = salePrice,
            totalQty = totalQty,
            remainStock = totalQty,
            status = status,
            startAt = startAt,
            endAt = endAt,
            maxPurchaseCount = maxPurchaseCount,
        )
    }
}