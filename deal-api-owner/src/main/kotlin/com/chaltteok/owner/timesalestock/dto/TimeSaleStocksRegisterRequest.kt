package com.chaltteok.owner.timesalestock.dto

import com.chaltteok.core.domain.Product
import com.chaltteok.core.domain.TimeSaleStock
import com.chaltteok.core.domain.enums.TimeSaleStockStatus
import com.chaltteok.owner.timesalestock.enums.TimeSaleStockType
import jakarta.validation.constraints.NotNull
import java.time.LocalDate
import java.time.LocalDateTime

data class TimeSaleStocksRegisterRequest(
    @field:NotNull(message = "option id must not be null")
    val optionId: String,

    @field:NotNull(message = "sale date must not be null")
    val saleDate: LocalDate,

    val stockType: TimeSaleStockType? = TimeSaleStockType.NORMAL,

    val salePrice: Int?,

    @field:NotNull(message = "sale amount must not be null")
    val totalQty: Int,

    val startAt: LocalDateTime? = null,
    val endAt: LocalDateTime? = null,
    val maxPurchaseCount: Int? = null,
) {
    init {
        if ((stockType ?: TimeSaleStockType.NORMAL) == TimeSaleStockType.TIMESALE) {
            require(startAt != null) { "startAt is required for TIMESALE stock" }
            require(endAt != null) { "endAt is required for TIMESALE stock" }
        }
    }

    fun toTimeSaleStockEntity(product: Product, salePrice: Int, status: TimeSaleStockStatus = TimeSaleStockStatus.OPEN): TimeSaleStock {
        val finalStockType = (this.stockType ?: TimeSaleStockType.NORMAL).name

        return TimeSaleStock(
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
