package com.chaltteok.core.domain

import jakarta.persistence.*
import java.util.*

@Entity
@Table(
    name = "tb_product_options",
    indexes = [
        Index(name = "idx_product_id", columnList = "product_id")
    ],
    uniqueConstraints = [
        UniqueConstraint(name = "uk_option_uuid", columnNames = ["option_uuid"])
    ]
)
class ProductOption(
    @Column(name = "option_name", nullable = false, length = 50)
    var optionName: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    var product: Product,

    @Column(name = "price", nullable = false)
    var price: Int
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "option_id")
    var id: Long? = null

    @Column(name = "option_uuid", nullable = false, unique = true, length = 36)
    val optionUuid: String = UUID.randomUUID().toString()
}