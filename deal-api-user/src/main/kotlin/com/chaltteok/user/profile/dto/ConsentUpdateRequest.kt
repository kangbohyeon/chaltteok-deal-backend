package com.chaltteok.user.profile.dto

import com.chaltteok.core.domain.enums.ConsentType
import jakarta.validation.constraints.NotNull

data class ConsentUpdateRequest(
    @field:NotNull val consentType: ConsentType,
    @field:NotNull val agreed: Boolean,
)
