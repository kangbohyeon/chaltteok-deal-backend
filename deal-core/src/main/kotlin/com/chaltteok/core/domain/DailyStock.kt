package com.chaltteok.core.domain

import com.chaltteok.core.domain.enums.DailyStockStatus
import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(
    name = "tb_daily_stocks",
    indexes = [
        Index(name = "idx_date_type", columnList = "sale_date, stock_type"),
    ],
    uniqueConstraints = [
        UniqueConstraint(name = "uk_stock_uuid", columnNames = ["stock_uuid"]),
        UniqueConstraint(name = "uk_product_date_type", columnNames = ["product_id", "sale_date", "stock_type"])
    ]
)
class DailyStock(
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
    var status: DailyStockStatus = DailyStockStatus.OPEN,

    @Column(name = "start_at")
    var startAt: LocalDateTime? = null,

    @Column(name = "end_at")
    var endAt: LocalDateTime? = null,

    @Column(name = "max_purchase_count", nullable = false)
    var maxPurchaseCount: Int = 1,

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
        check(remainStock >= quantity) { "재고가 부족합니다" }
        remainStock -= quantity
        if (remainStock == 0) status = DailyStockStatus.SOLD_OUT
    }

    fun update(salePrice: Int, totalQty: Int, startAt: LocalDateTime?, endAt: LocalDateTime?, maxPurchaseCount: Int) {
        val qtyDelta = totalQty - this.totalQty
        this.salePrice = salePrice
        this.totalQty = totalQty
        this.remainStock = maxOf(0, this.remainStock + qtyDelta)
        this.startAt = startAt
        this.endAt = endAt
        this.maxPurchaseCount = maxPurchaseCount
    }
}
