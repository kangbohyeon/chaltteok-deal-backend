package domain

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "tb_system_audit_logs",
    indexes = [
        Index(name = "idx_created_at", columnList = "created_at"),
    ]
)
class SystemAuditLog(
    @Column(name = "user_id")
    val userId: Long? = null,

    @Column(name = "api_path", length = 255)
    val apiPath: String? = null,

    @Column(name = "method", length = 10)
    val method: String,

    @Column(name = "request_params", columnDefinition = "TEXT")
    val requestParams: String? = null,

    @Column(name = "execution_time")
    val executionTime: Int? = 0,

    @Column(name = "status", length = 20)
    val status: String = "SUCCESS",

    @Column(name = "error_message", columnDefinition = "TEXT")
    val errorMessage: String? = null,

    @Column(name = "client_ip", length = 50)
    val clientIp: String,


    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    var id: Long? = null

}