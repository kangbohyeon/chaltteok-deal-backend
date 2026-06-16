package com.chaltteok.owner.profile.service

import com.chaltteok.owner.profile.dto.ChangeOwnerPasswordRequest

interface OwnerProfileService {
    fun changePassword(ownerId: Long, request: ChangeOwnerPasswordRequest)
}
