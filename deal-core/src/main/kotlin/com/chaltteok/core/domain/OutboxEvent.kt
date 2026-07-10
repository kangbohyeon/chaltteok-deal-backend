package com.chaltteok.core.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(
    name = "tb_outbox_events",
    indexes = [
        Index(name = "idx_outbox_source_status_created", columnList = "source,status,created_at"),
    ],
)
class OutboxEvent(
    val source: String,
    val aggregateId: String,
    val eventType: String,
    @Column(columnDefinition = "TEXT")
    val payload: String,
    var status: String = STATUS_PENDING,
    var retryCount: Int = 0,
    var processedAt: LocalDateTime? = null,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
) : BaseEntity() {
    companion object {
        const val STATUS_PENDING = "PENDING"
        const val STATUS_PROCESSED = "PROCESSED"
        const val STATUS_FAILED = "FAILED"

        const val SOURCE_API_USER = "API_USER"
        const val SOURCE_API_OWNER = "API_OWNER"
        const val SOURCE_CONSUMER = "CONSUMER"

        const val TYPE_ORDER_COMPLETED = "ORDER_COMPLETED"
        const val TYPE_ORDER_CANCELLED = "ORDER_CANCELLED"
    }
}
