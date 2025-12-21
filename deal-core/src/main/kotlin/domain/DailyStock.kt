package domain

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
        UniqueConstraint(name = "uk_option_date_type", columnNames = ["option_id", "sale_date", "stock_type"])
    ]
)
class DailyStock(
    @Column(name = "option_id", nullable = false)
    val optionId: Long,

    @Column(name = "sale_date", nullable = false)
    val saleDate: LocalDate,

    @Column(name = "stock_type", length = 20)
    val stockType: String = "NORMAL",

    @Column(name = "sale_price", nullable = false)
    val salePrice: Int,

    @Column(name = "total_qty", nullable = false)
    val totalQty: Int,

    @Column(name = "current_qty", nullable = false)
    var currentQty: Int,

    @Version
    val version: Long = 0L,

    @Column(length = 20)
    var status: String = "OPEN",

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()


) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stock_id")
    var id: Long? = null

    @Column(name = "stock_uuid", nullable = false, unique = true, length = 36)
    val stockUuid: String = UUID.randomUUID().toString()
}