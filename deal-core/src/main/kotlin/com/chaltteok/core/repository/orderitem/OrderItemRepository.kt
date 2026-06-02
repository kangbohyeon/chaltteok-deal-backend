package com.chaltteok.core.repository.orderitem

import com.chaltteok.core.domain.OrderItem
import com.chaltteok.core.domain.enums.OrderStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface SalesCountProjection {
    val productId: Long
    val totalQty: Long
}

interface OrderItemRepository : JpaRepository<OrderItem, Long>, OrderItemRepositoryCustom {
    @Query("SELECT oi FROM OrderItem oi JOIN FETCH oi.product WHERE oi.order.id IN :orderIds")
    fun findByOrderIdsWithProduct(orderIds: List<Long>): List<OrderItem>

    @Query("""
        SELECT oi.product.id AS productId, SUM(oi.quantity) AS totalQty
        FROM OrderItem oi
        WHERE oi.product.id IN :productIds
        AND oi.order.status = :status
        GROUP BY oi.product.id
    """)
    fun sumQuantityByProductIds(
        @Param("productIds") productIds: List<Long>,
        @Param("status") status: OrderStatus,
    ): List<SalesCountProjection>
}