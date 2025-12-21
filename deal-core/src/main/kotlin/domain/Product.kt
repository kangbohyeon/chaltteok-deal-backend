package domain

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

    @Column(name = "is_active")
    var isActive: Boolean = true

    ) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    var id: Long? = null

    @Column(name = "product_uuid", nullable = false, length = 36)
    val productUuid: String = UUID.randomUUID().toString()
}