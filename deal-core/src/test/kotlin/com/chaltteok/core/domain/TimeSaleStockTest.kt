package com.chaltteok.core.domain

import com.chaltteok.core.domain.enums.TimeSaleStockStatus
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate

class TimeSaleStockTest {

    private fun makeStock(
        remainStock: Int,
        status: TimeSaleStockStatus = TimeSaleStockStatus.OPEN,
    ): TimeSaleStock = TimeSaleStock(
        product = Product(name = "테스트 상품"),
        saleDate = LocalDate.now(),
        salePrice = 10_000,
        totalQty = remainStock,
        remainStock = remainStock,
        status = status,
    )

    // ── decrease() ──────────────────────────────────────────────────────────

    @Test
    fun `decrease - OPEN 상태에서 정상 차감`() {
        val stock = makeStock(remainStock = 5)
        stock.decrease(2)
        assertEquals(3, stock.remainStock)
        assertEquals(TimeSaleStockStatus.OPEN, stock.status)
    }

    @Test
    fun `decrease - 재고가 0이 되면 SOLD_OUT으로 전이`() {
        val stock = makeStock(remainStock = 1)
        stock.decrease(1)
        assertEquals(0, stock.remainStock)
        assertEquals(TimeSaleStockStatus.SOLD_OUT, stock.status)
    }

    @Test
    fun `decrease - OPEN이 아닌 상태에서 차감 시 예외 발생`() {
        listOf(TimeSaleStockStatus.SOLD_OUT, TimeSaleStockStatus.CLOSED, TimeSaleStockStatus.SCHEDULED)
            .forEach { status ->
                val stock = makeStock(remainStock = 5, status = status)
                assertThrows<IllegalStateException> { stock.decrease(1) }
            }
    }

    @Test
    fun `decrease - 재고보다 많은 수량 차감 시 예외 발생`() {
        val stock = makeStock(remainStock = 2)
        assertThrows<IllegalStateException> { stock.decrease(3) }
    }

    @Test
    fun `decrease - 기본값(1) 적용 확인`() {
        val stock = makeStock(remainStock = 3)
        stock.decrease()
        assertEquals(2, stock.remainStock)
    }

    // ── markSoldOutIfDepleted() ─────────────────────────────────────────────

    @Test
    fun `markSoldOutIfDepleted - remainStock가 0이면 SOLD_OUT으로 전이`() {
        val stock = makeStock(remainStock = 0)
        stock.markSoldOutIfDepleted()
        assertEquals(TimeSaleStockStatus.SOLD_OUT, stock.status)
    }

    @Test
    fun `markSoldOutIfDepleted - remainStock가 0보다 크면 상태 변화 없음`() {
        val stock = makeStock(remainStock = 3)
        stock.markSoldOutIfDepleted()
        assertEquals(TimeSaleStockStatus.OPEN, stock.status)
    }
}
