package com.chaltteok.core.domain

import jakarta.persistence.*
import java.time.LocalDate
import java.util.UUID

@Entity
@Table(name = "tb_banner", uniqueConstraints = [UniqueConstraint(name = "uk_banner_uuid", columnNames = ["banner_uuid"])])
class Banner(
    @Column(name = "title", length = 200)
    var title: String? = null,

    @Column(name = "subtitle", length = 400)
    var subtitle: String? = null,

    @Column(name = "image_url", columnDefinition = "TEXT")
    var imageUrl: String? = null,

    @Column(name = "link_url", columnDefinition = "TEXT")
    var linkUrl: String? = null,

    @Column(name = "background_color", length = 20)
    var backgroundColor: String? = null,

    @Column(name = "sort_order", nullable = false)
    var sortOrder: Int = 0,

    @Column(name = "is_visible", nullable = false)
    var isVisible: Boolean = true,

    @Column(name = "start_date")
    var startDate: LocalDate? = null,

    @Column(name = "end_date")
    var endDate: LocalDate? = null,
) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "banner_id")
    var id: Long? = null

    @Column(name = "banner_uuid", nullable = false, length = 36)
    val bannerUuid: String = UUID.randomUUID().toString()
}
