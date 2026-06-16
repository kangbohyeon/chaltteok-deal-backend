package com.chaltteok.core.repository.orderitem

import com.chaltteok.core.domain.OrderItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface OrderItemRepository : JpaRepository<OrderItem, Long>, OrderItemRepositoryCustom {
    @Query("SELECT oi FROM OrderItem oi JOIN FETCH oi.product WHERE oi.order.id IN :orderIds")
    fun findByOrderIdsWithProduct(@Param("orderIds") orderIds: List<Long>): List<OrderItem>

    @Query("SELECT oi FROM OrderItem oi JOIN FETCH oi.product WHERE oi.order.id = :orderId")
    fun findByOrderIdWithProduct(@Param("orderId") orderId: Long): List<OrderItem>

    fun existsByProductId(productId: Long): Boolean
}