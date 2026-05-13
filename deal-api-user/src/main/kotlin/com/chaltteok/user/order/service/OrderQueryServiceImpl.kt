package com.chaltteok.user.order.service

import com.chaltteok.core.repository.order.OrderRepository
import com.chaltteok.core.repository.orderitem.OrderItemRepository
import com.chaltteok.user.order.dto.OrderHistoryItemResponse
import com.chaltteok.user.order.dto.OrderHistoryResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderQueryServiceImpl(
    private val orderRepository: OrderRepository,
    private val orderItemRepository: OrderItemRepository,
) : OrderQueryService {

    @Transactional(readOnly = true)
    override fun getOrderHistory(userId: Long): List<OrderHistoryResponse> {
        val orders = orderRepository.findByUser_IdOrderByOrderedAtDesc(userId)
        if (orders.isEmpty()) return emptyList()

        val orderIds = orders.mapNotNull { it.id }
        val itemsByOrderId = orderItemRepository.findByOrderIdsWithProduct(orderIds)
            .groupBy { it.order?.id }

        return orders.map { order ->
            val items = itemsByOrderId[order.id].orEmpty().map { item ->
                OrderHistoryItemResponse(
                    productName = item.product.name,
                    quantity = item.quantity,
                    price = item.price.toLong(),
                )
            }
            OrderHistoryResponse(
                orderUuid = order.orderUuid,
                totalPrice = order.totalPrice.toLong(),
                status = order.status.name,
                orderedAt = order.orderedAt.toString(),
                items = items,
            )
        }
    }
}
