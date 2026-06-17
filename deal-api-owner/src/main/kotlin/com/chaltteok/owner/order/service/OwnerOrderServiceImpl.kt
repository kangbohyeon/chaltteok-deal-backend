package com.chaltteok.owner.order.service

import com.chaltteok.core.domain.OrderItem
import com.chaltteok.core.domain.enums.OrderStatus
import com.chaltteok.core.repository.order.OrderRepository
import com.chaltteok.core.repository.orderitem.OrderItemRepository
import com.chaltteok.core.repository.payment.PaymentRepository
import com.chaltteok.owner.order.dto.OwnerOrderDetailResponse
import com.chaltteok.owner.order.dto.OwnerOrderListResponse
import com.chaltteok.owner.order.dto.OwnerOrderSummaryResponse
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDate

@Service
class OwnerOrderServiceImpl(
    private val orderRepository: OrderRepository,
    private val orderItemRepository: OrderItemRepository,
    private val paymentRepository: PaymentRepository,
) : OwnerOrderService {

    @Transactional(readOnly = true)
    override fun getOrderDetail(orderNumber: String): OwnerOrderDetailResponse {
        val order = orderRepository.findByOrderNumber(orderNumber)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "주문을 찾을 수 없습니다: $orderNumber")
        val items = orderItemRepository.findByOrderIdWithProduct(order.id!!)
        val payment = paymentRepository.findByOrderId(order.id!!)
        return OwnerOrderDetailResponse.from(order, items, payment)
    }

    @Transactional(readOnly = true)
    override fun getOrders(status: OrderStatus?, startDate: LocalDate?, endDate: LocalDate?, page: Int, size: Int): OwnerOrderListResponse {
        val safePage = page.coerceAtLeast(0)
        val safeSize = size.coerceIn(1, 100)
        val pageable = PageRequest.of(safePage, safeSize)

        val orderPage = orderRepository.findAllByOwnerFilter(status, startDate, endDate, pageable)

        val orderIds = orderPage.content.mapNotNull { it.id }
        val itemsByOrderId = fetchItemsByOrderId(orderIds)

        val content = orderPage.content.map { order ->
            val items = itemsByOrderId[order.id] ?: emptyList()
            OwnerOrderSummaryResponse(
                orderNumber = order.orderNumber,
                productName = buildProductName(items),
                totalPrice = order.totalPrice,
                status = order.status.name,
                orderedAt = order.orderedAt,
                itemCount = items.size,
            )
        }

        return OwnerOrderListResponse(
            content = content,
            totalElements = orderPage.totalElements,
            totalPages = orderPage.totalPages,
            currentPage = orderPage.number,
            pageSize = orderPage.size,
        )
    }

    private fun fetchItemsByOrderId(orderIds: List<Long>): Map<Long?, List<OrderItem>> =
        orderItemRepository.findByOrderIdsWithProduct(orderIds).groupBy { it.order?.id }

    private fun buildProductName(items: List<OrderItem>): String {
        val firstName = items.firstOrNull()?.product?.name ?: "-"
        return if (items.size > 1) "$firstName 외 ${items.size - 1}건" else firstName
    }
}
