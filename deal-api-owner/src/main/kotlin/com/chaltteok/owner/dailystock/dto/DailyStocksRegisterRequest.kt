package com.chaltteok.owner.dto

import com.chaltteok.owner.enums.StockType
import jakarta.validation.constraints.NotNull
import java.time.LocalDate

data class DailyStocksRegisterRequest(
    @field:NotNull(message = "option_id must not be null")
    val optionId : String,

    @field:NotNull(message = "sale_date must not be null")
    val saleDate : LocalDate,

    val stockType : String = StockType.NORMAL.name
    
) {
}