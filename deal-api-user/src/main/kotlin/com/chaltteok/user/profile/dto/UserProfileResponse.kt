package com.chaltteok.user.profile.dto

import com.chaltteok.core.domain.User

data class UserProfileResponse(
    val email: String,
    val nickname: String,
) {
    companion object {
        fun from(user: User) = UserProfileResponse(
            email = user.email,
            nickname = user.nickname,
        )
    }
}
