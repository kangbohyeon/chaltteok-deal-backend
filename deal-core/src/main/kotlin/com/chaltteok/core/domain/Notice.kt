package com.chaltteok.core.domain

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "tb_notice", uniqueConstraints = [UniqueConstraint(name = "uk_notice_uuid", columnNames = ["notice_uuid"])])
class Notice(
    @Column(name = "title", nullable = false, length = 200)
    var title: String,

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    var content: String,

    @Column(name = "is_visible", nullable = false)
    var isVisible: Boolean = true,
) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_id")
    var id: Long? = null

    @Column(name = "notice_uuid", nullable = false, length = 36)
    val noticeUuid: String = UUID.randomUUID().toString()
}
