package com.chaltteok.core.domain

import com.chaltteok.core.domain.enums.ConsentType
import jakarta.persistence.*

@Entity
@Table(
    name = "tb_consent_conditions",
    uniqueConstraints = [UniqueConstraint(name = "uk_consent_condition_type", columnNames = ["consent_type"])]
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
}
