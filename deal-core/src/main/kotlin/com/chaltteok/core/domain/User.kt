package com.chaltteok.core.domain

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(
    name = "tb_users",
    uniqueConstraints = [
        UniqueConstraint(name = "uk_provider", columnNames = ["provider", "provider_id"]),
        UniqueConstraint(name = "uk_user_uuid", columnNames = ["user_uuid"]),
        UniqueConstraint(name = "uk_email", columnNames = ["email"])
    ]
)
class User(
    @Column(name = "email", nullable = false, length = 100)
    val email: String,

    @Column(name = "nickname", nullable = false, length = 50)
    var nickname: String,

    @Column(name = "provider", nullable = false, length = 20)
    val provider: String,

    @Column(name = "provider_id", nullable = false, length = 255)
    val providerId: String
) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    var id: Long? = null

    @Column(name = "user_uuid", nullable = false, length = 36)
    val userUuid: String = UUID.randomUUID().toString()

    @Column(name = "password", nullable = true, length = 255)
    var password: String? = null

    @Column(name = "password_changed_at")
    var passwordChangedAt: LocalDateTime? = null

    @Column(name = "phone", nullable = true, length = 20)
    var phone: String? = null

    @Column(name = "login_failed_count", nullable = false)
    var loginFailedCount: Int = 0

    @Column(name = "locked_at")
    var lockedAt: LocalDateTime? = null

    @Column(name = "require_password_change", nullable = false)
    var requirePasswordChange: Boolean = false

    @Column(name = "terms_agreed", nullable = false)
    var termsAgreed: Boolean = false

    @Column(name = "privacy_agreed", nullable = false)
    var privacyAgreed: Boolean = false

    @Column(name = "age_agreed", nullable = false)
    var ageAgreed: Boolean = false

    @Column(name = "marketing_agreed", nullable = false)
    var marketingAgreed: Boolean = false

    @Column(name = "push_agreed", nullable = false)
    var pushAgreed: Boolean = false
}