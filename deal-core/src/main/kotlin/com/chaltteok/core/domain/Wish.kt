package com.chaltteok.core.domain

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(
    name = "tb_wish",
    uniqueConstraints = [
        UniqueConstraint(name = "uk_wish_uuid", columnNames = ["wish_uuid"]),
        UniqueConstraint(name = "uk_wish_user_product", columnNames = ["user_id", "product_id"])
    ]
)
class Wish(
    @Column(name = "user_id", nullable = false)
    val userId: Long,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    val product: Product,
) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wish_id")
    var id: Long? = null

    @Column(name = "wish_uuid", nullable = false, length = 36)
    val wishUuid: String = UUID.randomUUID().toString()
}
