package com.chaltteok.core.domain

import com.chaltteok.core.domain.enums.ConsentType
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(
    name = "user_consent_history",
    indexes = [
        Index(name = "idx_uch_user_consent_type", columnList = "user_id, consent_type"),
        Index(name = "idx_uch_user_changed_at", columnList = "user_id, changed_at"),
    ]
)
class UserConsentHistory(
    @Column(name = "user_id", nullable = false)
    val userId: Long,

    @Enumerated(EnumType.STRING)
    @Column(name = "consent_type", nullable = false, length = 20)
    val consentType: ConsentType,

    @Column(name = "agreed", nullable = false)
    val agreed: Boolean,

    @Column(name = "changed_at", nullable = false)
    val changedAt: LocalDateTime,
) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null

    @Column(name = "history_uuid", nullable = false, length = 36)
    val historyUuid: String = UUID.randomUUID().toString()
}
