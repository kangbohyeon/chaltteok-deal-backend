package domain

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "tb_event_history",
    uniqueConstraints = [UniqueConstraint(name = "uk_one_event_per_user", columnNames = ["user_id", "stock_id"])]
)
class EventHistory(
    @Column(name = "user_id", nullable = false)
    val userId: Long,

    @Column(name = "stock_id", nullable = false)
    val stockId: Long,

    @Column(name = "order_id", nullable = false)
    val orderId: Long,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()

) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    var id: Long? = null
}