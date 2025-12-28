package com.chaltteok.owner.dailystock.dto

import com.chaltteok.core.domain.DailyStock
import com.chaltteok.core.domain.ProductOption
import com.chaltteok.owner.dailystock.enums.DailyStockStatusType
import com.chaltteok.owner.dailystock.enums.DailyStockType
import jakarta.validation.constraints.NotNull
import java.time.LocalDate

data class DailyStocksRegisterRequest(
    @field:NotNull(message = "option id must not be null")
    val optionId: String,

    @field:NotNull(message = "sale date must not be null")
    val saleDate: LocalDate,

    val stockType: DailyStockType? = DailyStockType.NORMAL,

    val salePrice: Int?,

    @field:NotNull(message = "sale amount must not be null")
    val totalQty: Int,

    val status: String? = DailyStockStatusType.OPEN.name
) {
    fun toDailyStockEntity(productOption: ProductOption, salePrice: Int): DailyStock {
        val finalStockType = (this.stockType ?: DailyStockType.NORMAL).name

        return DailyStock(
            optionId = productOption.id as Long,
            saleDate = saleDate,
            stockType = finalStockType,
            salePrice = salePrice,
            totalQty = totalQty,
            currentQty = totalQty,
        )
    }
}