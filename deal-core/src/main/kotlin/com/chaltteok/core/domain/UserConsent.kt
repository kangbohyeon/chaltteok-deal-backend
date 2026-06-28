package com.chaltteok.core.domain

import com.chaltteok.core.domain.enums.ConsentType
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(
    name = "tb_user_consents",
    uniqueConstraints = [UniqueConstraint(name = "uk_user_consent_type", columnNames = ["user_id", "consent_type"])]
)
class UserConsent(
    @Column(name = "user_id", nullable = false)
    val userId: Long,

    @Enumerated(EnumType.STRING)
    @Column(name = "consent_type", nullable = false, length = 20)
    val consentType: ConsentType,

    @Column(name = "agreed", nullable = false)
    var agreed: Boolean,

    @Column(name = "agreed_at", nullable = false)
    var agreedAt: LocalDateTime,
) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null

    @Column(name = "consent_uuid", nullable = false, length = 36)
    val consentUuid: String = UUID.randomUUID().toString()
}
