package com.chaltteok.core.domain

import com.chaltteok.core.domain.enums.TimeSaleStockStatus
import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(
    name = "tb_time_sale_stocks",
    indexes = [
        Index(name = "idx_date_type", columnList = "sale_date, stock_type"),
        Index(name = "idx_end_at_status", columnList = "end_at, status"),
        Index(name = "idx_status_start_at", columnList = "status, start_at"),
    ],
    uniqueConstraints = [
        UniqueConstraint(name = "uk_stock_uuid", columnNames = ["stock_uuid"]),
        UniqueConstraint(name = "uk_product_date_type", columnNames = ["product_id", "sale_date", "stock_type"])
    ]
)
class TimeSaleStock(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    val product: Product,

    @Column(name = "sale_date", nullable = false)
    val saleDate: LocalDate,

    @Column(name = "stock_type", length = 20)
    val stockType: String = "NORMAL",

    @Column(name = "sale_price", nullable = false)
    var salePrice: Int,

    @Column(name = "total_qty", nullable = false)
    var totalQty: Int,

    @Column(name = "remain_stock", nullable = false)
    var remainStock: Int,

    @Version
    val version: Long = 0L,

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    var status: TimeSaleStockStatus = TimeSaleStockStatus.OPEN,

    @Column(name = "start_at")
    var startAt: LocalDateTime? = null,

    @Column(name = "end_at")
    var endAt: LocalDateTime? = null,

    @Column(name = "max_purchase_count", nullable = true)
    var maxPurchaseCount: Int? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()

) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stock_id")
    var id: Long? = null

    @Column(name = "stock_uuid", nullable = false, unique = true, length = 36)
    val stockUuid: String = UUID.randomUUID().toString()

    fun decrease(quantity: Int = 1) {
        check(status == TimeSaleStockStatus.OPEN) { "OPEN 상태가 아닌 재고는 차감 불가: $status" }
        check(remainStock >= quantity) { "재고가 부족합니다" }
        remainStock -= quantity
        if (remainStock == 0) status = TimeSaleStockStatus.SOLD_OUT
    }

    fun markSoldOutIfDepleted() {
        if (remainStock == 0) status = TimeSaleStockStatus.SOLD_OUT
    }

    fun update(salePrice: Int, totalQty: Int, startAt: LocalDateTime?, endAt: LocalDateTime?, maxPurchaseCount: Int?) {
        val qtyDelta = totalQty - this.totalQty
        this.salePrice = salePrice
        this.totalQty = totalQty
        this.remainStock = maxOf(0, this.remainStock + qtyDelta)
        this.startAt = startAt
        this.endAt = endAt
        this.maxPurchaseCount = maxPurchaseCount
        resetStatusIfNeeded(startAt, LocalDateTime.now())
    }

    // SOLD_OUT(재고 복구 시) 또는 CLOSED(종료 시각이 아직 미래인 경우) 상태를 재오픈한다.
    // now를 파라미터로 받아 배치 처리 시 기준 시각 일관성을 보장하고 테스트 가능성을 확보한다.
    internal fun resetStatusIfNeeded(newStartAt: LocalDateTime?, now: LocalDateTime) {
        val shouldReopen = when (status) {
            TimeSaleStockStatus.SOLD_OUT -> remainStock > 0
            TimeSaleStockStatus.CLOSED -> endAt?.isAfter(now) == true
            else -> false
        }
        if (shouldReopen) {
            status = if (newStartAt != null && newStartAt.isAfter(now)) {
                TimeSaleStockStatus.SCHEDULED
            } else {
                TimeSaleStockStatus.OPEN
            }
        }
    }
}
