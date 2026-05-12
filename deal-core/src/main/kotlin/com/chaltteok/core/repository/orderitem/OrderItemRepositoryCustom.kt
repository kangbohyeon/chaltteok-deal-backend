package com.chaltteok.core.repository.orderitem

import com.chaltteok.core.repository.orderitem.dto.TopProductAgg
import java.time.LocalDateTime

interface OrderItemRepositoryCustom {
    fun findTopProducts(from: LocalDateTime, to: LocalDateTime, limit: Int): List<TopProductAgg>
}