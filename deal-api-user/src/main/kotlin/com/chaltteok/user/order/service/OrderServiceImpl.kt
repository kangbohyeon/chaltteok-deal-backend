package com.chaltteok.user.order.service

import com.chaltteok.common.exception.BusinessException
import com.chaltteok.core.domain.OutboxEvent
import com.chaltteok.core.domain.enums.DailyStockStatus
import com.chaltteok.core.domain.enums.OrderStatus
import com.chaltteok.core.event.OrderCancelledEvent
import com.chaltteok.core.infrastructure.outbox.OutboxEventWriter
import com.chaltteok.core.repository.dailystock.DailyStockRepository
import com.chaltteok.core.repository.eventhistory.EventHistoryRepository
import com.chaltteok.core.repository.order.OrderRepository
import com.chaltteok.core.repository.orderitem.OrderItemRepository
import com.chaltteok.core.repository.payment.PaymentRepository
import com.chaltteok.user.infrastructure.kafka.OrderEventProducer
import com.chaltteok.user.order.dto.OrderHistoryItemResponse
import com.chaltteok.user.order.dto.OrderHistoryPageResponse
import com.chaltteok.user.order.dto.OrderHistoryResponse
import com.chaltteok.user.order.dto.OrderRequest
import com.chaltteok.user.order.dto.OrderResponse
import com.chaltteok.user.order.dto.PaymentInfoResponse
import com.chaltteok.user.order.enums.OrderErrorCode
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeParseException

private val logger = KotlinLogging.logger {}

@Service
class OrderServiceImpl(
    private val dailyStockRepository: DailyStockRepository,
    private val eventHistoryRepository: EventHistoryRepository,
    private val orderRepository: OrderRepository,
    private val orderItemRepository: OrderItemRepository,
    private val paymentRepository: PaymentRepository,
    private val orderEventProducer: OrderEventProducer,
    private val outboxEventWriter: OutboxEventWriter,
) : OrderService {

    @Transactional(readOnly = true)
    override fun placeOrder(userId: Long, request: OrderRequest): OrderResponse {
        val dailyStock = dailyStockRepository.findByStockUuid(request.stockUuid)
            ?: throw BusinessException(OrderErrorCode.DAILY_STOCK_NOT_FOUND)

        if (dailyStock.status != DailyStockStatus.OPEN) {
            throw BusinessException(OrderErrorCode.STOCK_NOT_AVAILABLE)
        }
        if (dailyStock.remainStock < request.quantity) {
            throw BusinessException(OrderErrorCode.INSUFFICIENT_STOCK)
        }

        val maxPurchaseCount = dailyStock.maxPurchaseCount
        if (maxPurchaseCount != null) {
            val participated = eventHistoryRepository.countByUser_IdAndDailyStock_Id(userId, dailyStock.id)
            if (participated + request.quantity > maxPurchaseCount) {
                if (participated == 0L) throw BusinessException(OrderErrorCode.EXCEEDS_MAX_PURCHASE_COUNT)
                throw BusinessException(OrderErrorCode.ALREADY_PARTICIPATED)
            }
        }

        val dailyStockId = dailyStock.id ?: error("DailyStock ID가 null입니다")
        orderEventProducer.sendOrderEvent(userId, dailyStockId)
        logger.info { "타임세일 주문 이벤트 발행 — stockUuid=${request.stockUuid}, userId=$userId" }

        return OrderResponse.pending()
    }

    @Transactional
    override fun cancelOrder(userId: Long, orderNumber: String) {
        val order = orderRepository.findByOrderNumberAndUser_Id(orderNumber, userId)
            .orElseThrow { BusinessException(OrderErrorCode.ORDER_NOT_FOUND) }

        if (order.status == OrderStatus.CANCELLED) {
            throw BusinessException(OrderErrorCode.ORDER_ALREADY_CANCELLED)
        }
        if (!order.isCancellable()) {
            throw BusinessException(OrderErrorCode.ORDER_NOT_CANCELLABLE)
        }

        order.cancel()

        val orderId = order.id ?: error("Order ID null")
        paymentRepository.findByOrderId(orderId)?.cancel()

        outboxEventWriter.write(
            source = OutboxEvent.SOURCE_API_USER,
            aggregateId = order.orderNumber,
            eventType = OutboxEvent.TYPE_ORDER_CANCELLED,
            event = OrderCancelledEvent(
                orderId = orderId,
                orderNumber = order.orderNumber,
                userName = order.user.nickname,
                totalAmount = order.totalPrice.toLong(),
                cancelledAt = LocalDateTime.now(),
            )
        )
    }

    @Transactional(readOnly = true)
    override fun getOrderHistory(
        userId: Long,
        keyword: String?,
        status: String?,
        fromDate: String?,
        toDate: String?,
        paymentStatus: String?,
        pageable: Pageable,
    ): OrderHistoryPageResponse {
        val orderStatus = status?.let { runCatching { OrderStatus.valueOf(it) }.getOrNull() }
        val from = fromDate?.let {
            try { LocalDate.parse(it) } catch (e: DateTimeParseException) {
                throw BusinessException(OrderErrorCode.INVALID_DATE_FORMAT)
            }
        }
        val to = toDate?.let {
            try { LocalDate.parse(it) } catch (e: DateTimeParseException) {
                throw BusinessException(OrderErrorCode.INVALID_DATE_FORMAT)
            }
        }

        val page = orderRepository.findByUserIdPaged(userId, keyword, orderStatus, from, to, paymentStatus, pageable)
        val orders = page.content
        if (orders.isEmpty()) {
            return OrderHistoryPageResponse(
                content = emptyList(),
                totalElements = page.totalElements,
                totalPages = page.totalPages,
                currentPage = pageable.pageNumber,
                pageSize = pageable.pageSize,
            )
        }

        val orderIds = orders.mapNotNull { it.id }
        val itemsByOrderId = orderItemRepository.findByOrderIdsWithProduct(orderIds).groupBy { it.order?.id }
        val paymentByOrderId = paymentRepository.findByOrderIds(orderIds).associateBy { it.order.id }

        val content = orders.map { order ->
            val items = itemsByOrderId[order.id].orEmpty().map { item ->
                OrderHistoryItemResponse(
                    productName = item.product.name,
                    quantity = item.quantity,
                    price = item.price.toLong(),
                )
            }
            val payment = paymentByOrderId[order.id]?.let { p ->
                PaymentInfoResponse(
                    amount = p.amount,
                    pgProvider = p.pgProvider,
                    paymentMethod = p.paymentMethod,
                    status = p.status.name,
                    paidAt = p.paidAt?.toString(),
                )
            }
            OrderHistoryResponse(
                orderNumber = order.orderNumber,
                totalPrice = order.totalPrice.toLong(),
                status = order.status.name,
                orderedAt = order.orderedAt.toString(),
                items = items,
                payment = payment,
                canCancel = order.isCancellable(),
            )
        }

        return OrderHistoryPageResponse(
            content = content,
            totalElements = page.totalElements,
            totalPages = page.totalPages,
            currentPage = page.number,
            pageSize = page.size,
        )
    }

    @Transactional(readOnly = true)
    override fun getOrderDetail(userId: Long, orderNumber: String): OrderHistoryResponse {
        val order = orderRepository.findByOrderNumberAndUser_Id(orderNumber, userId)
            .orElseThrow { BusinessException(OrderErrorCode.ORDER_NOT_FOUND) }

        val orderId = order.id ?: error("Order ID null")
        val items = orderItemRepository.findByOrderIdWithProduct(orderId).map { item ->
            OrderHistoryItemResponse(
                productName = item.product.name,
                quantity = item.quantity,
                price = item.price.toLong(),
            )
        }
        val payment = paymentRepository.findByOrderId(orderId)?.let { p ->
            PaymentInfoResponse(
                amount = p.amount,
                pgProvider = p.pgProvider,
                paymentMethod = p.paymentMethod,
                status = p.status.name,
                paidAt = p.paidAt?.toString(),
            )
        }
        return OrderHistoryResponse(
            orderNumber = order.orderNumber,
            totalPrice = order.totalPrice.toLong(),
            status = order.status.name,
            orderedAt = order.orderedAt.toString(),
            items = items,
            payment = payment,
            canCancel = order.isCancellable(),
        )
    }
}
