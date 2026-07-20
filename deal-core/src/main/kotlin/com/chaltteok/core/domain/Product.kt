package com.chaltteok.core.domain

import jakarta.persistence.*
import java.util.*

@Entity
@Table(
    name = "tb_products",
    uniqueConstraints = [
        UniqueConstraint(name = "uk_product_uuid", columnNames = ["product_uuid"])
    ]
)
class Product(
    @Column(name = "name", nullable = false, length = 100)
    var name: String,

    @Column(name = "description", columnDefinition = "TEXT")
    var description: String? = null,

    @Column(name = "image_url", length = 255)
    var imageUrl: String? = null,

    @Column(name = "price", nullable = false)
    var price: Int = 0,

    @Column(name = "is_active")
    var isActive: Boolean = true,

    @Column(name = "is_sold_out")
    var isSoldOut: Boolean = false,

    @Column(name = "is_recommended")
    var isRecommended: Boolean = false,

    @Column(name = "stock_quantity")
    var stockQuantity: Int? = null,

    @Column(name = "current_stock")
    var currentStock: Int? = null,

    @Column(name = "display_order", nullable = false)
    var displayOrder: Int = 0,

    @Column(name = "owner_id", nullable = false)
    val ownerId: Long,

) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    var id: Long? = null

    @Column(name = "product_uuid", nullable = false, length = 36)
    val productUuid: String = UUID.randomUUID().toString()

    @OneToMany(mappedBy = "product", cascade = [CascadeType.REMOVE], orphanRemoval = true)
    val comments: MutableList<Comment> = mutableListOf()

    fun deductStock(quantity: Int): Boolean {
        val stock = currentStock ?: return true
        if (stock < quantity) return false
        currentStock = stock - quantity
        if (currentStock == 0) isSoldOut = true
        return true
    }
}
