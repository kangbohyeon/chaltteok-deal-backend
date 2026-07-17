package com.chaltteok.core.domain

import com.chaltteok.core.domain.enums.InquiryStatus
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "tb_inquiry", uniqueConstraints = [UniqueConstraint(name = "uk_inquiry_uuid", columnNames = ["inquiry_uuid"])])
class Inquiry(
    @Column(name = "user_id", nullable = false)
    val userId: Long,

    @Column(name = "title", nullable = false, length = 200)
    var title: String,

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    var content: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    var status: InquiryStatus = InquiryStatus.PENDING,

    @Column(name = "answer", columnDefinition = "TEXT")
    var answer: String? = null,

    @Column(name = "answered_at")
    var answeredAt: LocalDateTime? = null,
) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inquiry_id")
    var id: Long? = null

    @Column(name = "inquiry_uuid", nullable = false, length = 36)
    val inquiryUuid: String = UUID.randomUUID().toString()
}
