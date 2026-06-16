package com.chaltteok.core.domain

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(
    name = "tb_comment",
    uniqueConstraints = [UniqueConstraint(name = "uk_comment_uuid", columnNames = ["comment_uuid"])]
)
class Comment(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    val product: Product,

    @Column(name = "user_id", nullable = false)
    val userId: Long,

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    var content: String,

    @Column(name = "rating")
    var rating: Int? = null,

    @Column(name = "is_secret", nullable = false)
    var isSecret: Boolean = false,

    @Column(name = "parent_id")
    val parentId: Long? = null,

    @Column(name = "is_owner_reply", nullable = false)
    val isOwnerReply: Boolean = false,
) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    var id: Long? = null

    @Column(name = "comment_uuid", nullable = false, length = 36)
    val commentUuid: String = UUID.randomUUID().toString()
}
