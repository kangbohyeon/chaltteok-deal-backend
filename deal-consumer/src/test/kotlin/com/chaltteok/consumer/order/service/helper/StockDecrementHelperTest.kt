package com.chaltteok.consumer.order.service.helper

import com.chaltteok.consumer.order.exception.OrderProcessingException
import com.chaltteok.core.domain.Product
import com.chaltteok.core.domain.TimeSaleStock
import com.chaltteok.core.domain.enums.TimeSaleStockStatus
import com.chaltteok.core.infrastructure.outbox.OutboxEventWriter
import com.chaltteok.core.repository.timesalestock.TimeSaleStockRepository
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate

class StockDecrementHelperTest {

    private val timeSaleStockRepository: TimeSaleStockRepository = mockk()
    private val outboxEventWriter: OutboxEventWriter = mockk()
    private val helper = StockDecrementHelper(timeSaleStockRepository, outboxEventWriter)

    private val stockId = 1L
    private val product = Product(name = "테스트 상품")

    private fun makeOpenStock(remainStock: Int): TimeSaleStock = TimeSaleStock(
        product = product,
        saleDate = LocalDate.now(),
        salePrice = 10_000,
        totalQty = remainStock,
        remainStock = remainStock,
        status = TimeSaleStockStatus.OPEN,
    )

    @BeforeEach
    fun setUp() {
        justRun { outboxEventWriter.write(any(), any(), any(), any()) }
    }

    @Test
    fun `tryDecrement - TimeSaleStock 미존재 시 OrderProcessingException 발생`() {
        every { timeSaleStockRepository.findByIdWithProduct(stockId) } returns null

        assertThrows<OrderProcessingException> {
            helper.tryDecrement(stockId, 1)
        }
    }

    @Test
    fun `tryDecrement - OPEN이 아닌 상태이면 false 반환`() {
        listOf(TimeSaleStockStatus.SOLD_OUT, TimeSaleStockStatus.CLOSED, TimeSaleStockStatus.SCHEDULED)
            .forEach { status ->
                val stock = makeOpenStock(5).apply {
                    this.status = status
                }
                every { timeSaleStockRepository.findByIdWithProduct(stockId) } returns stock

                val result = helper.tryDecrement(stockId, 1)

                assertFalse(result)
                verify(exactly = 0) { outboxEventWriter.write(any(), any(), any(), any()) }
            }
    }

    @Test
    fun `tryDecrement - 재고 부족 시 false 반환하고 markSoldOutIfDepleted 호출`() {
        val stock = makeOpenStock(remainStock = 0)
        every { timeSaleStockRepository.findByIdWithProduct(stockId) } returns stock

        val result = helper.tryDecrement(stockId, 1)

        assertFalse(result)
        assertEquals(TimeSaleStockStatus.SOLD_OUT, stock.status)
        verify(exactly = 0) { outboxEventWriter.write(any(), any(), any(), any()) }
    }

    @Test
    fun `tryDecrement - 정상 차감 시 true 반환`() {
        val stock = makeOpenStock(remainStock = 5)
        every { timeSaleStockRepository.findByIdWithProduct(stockId) } returns stock

        val result = helper.tryDecrement(stockId, 2)

        assertTrue(result)
        assertEquals(3, stock.remainStock)
        assertEquals(TimeSaleStockStatus.OPEN, stock.status)
        verify(exactly = 0) { outboxEventWriter.write(any(), any(), any(), any()) }
    }

    @Test
    fun `tryDecrement - 차감 후 재고가 0이 되면 OutboxEvent 기록`() {
        val stock = makeOpenStock(remainStock = 1)
        every { timeSaleStockRepository.findByIdWithProduct(stockId) } returns stock

        val result = helper.tryDecrement(stockId, 1)

        assertTrue(result)
        assertEquals(TimeSaleStockStatus.SOLD_OUT, stock.status)
        verify(exactly = 1) { outboxEventWriter.write(any(), any(), any(), any()) }
    }

    @Test
    fun `tryDecrement - 차감 후 재고가 남아있으면 OutboxEvent 기록하지 않음`() {
        val stock = makeOpenStock(remainStock = 3)
        every { timeSaleStockRepository.findByIdWithProduct(stockId) } returns stock

        val result = helper.tryDecrement(stockId, 1)

        assertTrue(result)
        assertEquals(TimeSaleStockStatus.OPEN, stock.status)
        verify(exactly = 0) { outboxEventWriter.write(any(), any(), any(), any()) }
    }
}
