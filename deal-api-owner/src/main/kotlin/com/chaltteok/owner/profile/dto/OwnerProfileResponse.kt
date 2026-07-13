package com.chaltteok.owner.profile.dto

import com.chaltteok.core.domain.Owner
import java.time.LocalDateTime

data class OwnerProfileResponse(
    val ownerUuid: String,
    val username: String,
    val name: String,
    val role: String,
    val passwordChangedAt: LocalDateTime?,
) {
    companion object {
        fun from(owner: Owner) = OwnerProfileResponse(
            ownerUuid = owner.ownerUuid,
            username = owner.username,
            name = owner.name,
            role = owner.role,
            passwordChangedAt = owner.passwordChangedAt,
        )
    }
}
