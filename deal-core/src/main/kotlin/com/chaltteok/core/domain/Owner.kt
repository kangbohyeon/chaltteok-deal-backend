package com.chaltteok.core.domain

import jakarta.persistence.*
import java.util.*

@Entity
@Table(
    name = "tb_owner",
    uniqueConstraints = [
        UniqueConstraint(name = "uk_username", columnNames = ["username"]),
        UniqueConstraint(name = "uk_owner_uuid", columnNames = ["owner_uuid"])
    ]
)
class Owner(
    @Column(name = "username", nullable = false, length = 50)
    val username: String,

    @Column(name = "password", nullable = false, length = 255)
    val password: String,

    @Column(name = "name", nullable = false, length = 50)
    val name: String,

    @Column(name = "role", length = 20)
    val role: String = "MASTER"


) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "owner_id")
    var id: Long? = null

    @Column(name = "owner_uuid", nullable = false, unique = true, length = 36)
    val ownerUuid: String = UUID.randomUUID().toString()
}