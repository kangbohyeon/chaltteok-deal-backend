package com.chaltteok.core.domain

import com.chaltteok.core.domain.enums.ConsentType
import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(
    name = "tb_consent_conditions",
    uniqueConstraints = [
        UniqueConstraint(name = "uk_consent_condition_type", columnNames = ["consent_type"]),
        UniqueConstraint(name = "uk_condition_uuid", columnNames = ["condition_uuid"]),
    ]
)
class ConsentCondition(
    @Enumerated(EnumType.STRING)
    @Column(name = "consent_type", nullable = false, length = 20)
    val consentType: ConsentType,

    @Column(name = "display_name", nullable = false, length = 100)
    var displayName: String,

    @Column(name = "is_required", nullable = false)
    var isRequired: Boolean,

    @Column(name = "display_order", nullable = false)
    var displayOrder: Int,

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true,
) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null

    @Column(name = "condition_uuid", nullable = false, length = 36)
    val conditionUuid: String = UUID.randomUUID().toString()
}
