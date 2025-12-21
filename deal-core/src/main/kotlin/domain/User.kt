package domain

import jakarta.persistence.*
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
}