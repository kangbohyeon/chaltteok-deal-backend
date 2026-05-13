package com.chaltteok.core.repository.order

import com.chaltteok.core.domain.Order
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface OrderRepository : JpaRepository<Order, Long>, OrderRepositoryCustom {
    fun findByUser_IdOrderByOrderedAtDesc(userId: Long): List<Order>
    fun findByOrderNumberAndUser_Id(orderNumber: String, userId: Long): Optional<Order>
}