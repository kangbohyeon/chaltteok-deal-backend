package com.chaltteok.owner.profile.service

import com.chaltteok.owner.profile.dto.ChangeOwnerPasswordRequest
import com.chaltteok.owner.profile.dto.OwnerProfileResponse

interface OwnerProfileService {
    fun getProfile(ownerId: Long): OwnerProfileResponse
    fun changePassword(ownerId: Long, request: ChangeOwnerPasswordRequest)
}
