package com.chaltteok.core.repository.orderitem

import com.chaltteok.core.domain.OrderItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface OrderItemRepository : JpaRepository<OrderItem, Long>, OrderItemRepositoryCustom {
    @Query("SELECT oi FROM OrderItem oi JOIN FETCH oi.product WHERE oi.order.id IN :orderIds")
    fun findByOrderIdsWithProduct(orderIds: List<Long>): List<OrderItem>
}