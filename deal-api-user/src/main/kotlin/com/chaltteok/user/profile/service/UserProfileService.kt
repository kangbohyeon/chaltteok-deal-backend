package com.chaltteok.user.profile.service

import com.chaltteok.user.profile.dto.UpdateNicknameRequest
import com.chaltteok.user.profile.dto.UserProfileResponse

interface UserProfileService {
    fun getProfile(userId: Long): UserProfileResponse
    fun updateNickname(userId: Long, request: UpdateNicknameRequest): UserProfileResponse
}
