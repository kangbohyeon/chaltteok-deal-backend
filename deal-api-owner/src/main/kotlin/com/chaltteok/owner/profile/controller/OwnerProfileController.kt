package com.chaltteok.owner.profile.controller

import com.chaltteok.common.dto.ResponseDTO
import com.chaltteok.owner.profile.dto.ChangeOwnerPasswordRequest
import com.chaltteok.owner.profile.service.OwnerProfileService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/owner/me")
class OwnerProfileController(private val ownerProfileService: OwnerProfileService) {

    @PatchMapping("/password")
    fun changePassword(
        @RequestHeader("X-Owner-Id") ownerId: Long,
        @Valid @RequestBody request: ChangeOwnerPasswordRequest,
    ): ResponseDTO<Unit> {
        ownerProfileService.changePassword(ownerId, request)
        return ResponseDTO.success(Unit)
    }
}
