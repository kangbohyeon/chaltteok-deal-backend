package com.chaltteok.user.profile.service

import com.chaltteok.user.profile.dto.ChangePasswordRequest
import com.chaltteok.user.profile.dto.ConsentUpdateRequest
import com.chaltteok.user.profile.dto.UpdateNicknameRequest
import com.chaltteok.user.profile.dto.UserProfileResponse

interface UserProfileService {
    fun getProfile(userId: Long): UserProfileResponse
    fun updateNickname(userId: Long, request: UpdateNicknameRequest): UserProfileResponse
    fun changePassword(userId: Long, request: ChangePasswordRequest)
    fun updateConsent(userId: Long, request: ConsentUpdateRequest)
}
