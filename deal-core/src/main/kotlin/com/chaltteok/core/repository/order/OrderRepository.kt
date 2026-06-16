package com.chaltteok.core.repository.order

import com.chaltteok.core.domain.Order
import com.chaltteok.core.domain.enums.OrderStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface OrderRepository : JpaRepository<Order, Long>, OrderRepositoryCustom {
    fun findByUser_IdOrderByOrderedAtDesc(userId: Long): List<Order>
    fun findByOrderNumber(orderNumber: String): Order?
    fun findByOrderNumberAndUser_Id(orderNumber: String, userId: Long): Optional<Order>
    fun findAllByOrderByOrderedAtDesc(pageable: Pageable): Page<Order>
    fun findAllByStatusOrderByOrderedAtDesc(status: OrderStatus, pageable: Pageable): Page<Order>
}
