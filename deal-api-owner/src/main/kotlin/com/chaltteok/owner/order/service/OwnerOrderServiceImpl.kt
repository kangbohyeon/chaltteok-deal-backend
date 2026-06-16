package com.chaltteok.owner.order.service

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
    override fun getOrders(status: String?, page: Int, size: Int): OwnerOrderListResponse {
        val pageable = PageRequest.of(page, size)
        val orderPage = if (status != null) {
            val orderStatus = runCatching { OrderStatus.valueOf(status) }
                .getOrElse { throw ResponseStatusException(HttpStatus.BAD_REQUEST, "유효하지 않은 주문 상태: $status") }
            orderRepository.findAllByStatusOrderByOrderedAtDesc(orderStatus, pageable)
        } else {
            orderRepository.findAllByOrderByOrderedAtDesc(pageable)
        }

        val orderIds = orderPage.content.mapNotNull { it.id }
        val itemsByOrderId = orderItemRepository.findByOrderIdsWithProduct(orderIds)
            .groupBy { it.order?.id }

        val content = orderPage.content.map { order ->
            val items = itemsByOrderId[order.id] ?: emptyList()
            val firstName = items.firstOrNull()?.product?.name ?: "-"
            val productName = if (items.size > 1) "$firstName 외 ${items.size - 1}건" else firstName
            OwnerOrderSummaryResponse(
                orderNumber = order.orderNumber,
                productName = productName,
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
}
