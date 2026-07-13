package com.chaltteok.user.profile.dto

import com.chaltteok.core.domain.ConsentCondition
import com.chaltteok.core.domain.UserConsent
import com.chaltteok.core.domain.enums.ConsentType
import java.time.LocalDateTime

data class UserConsentResponse(
    val consentType: ConsentType,
    val displayName: String,
    val agreed: Boolean,
    val agreedAt: LocalDateTime,
    val isRequired: Boolean,
) {
    companion object {
        fun from(consent: UserConsent, condition: ConsentCondition?) = UserConsentResponse(
            consentType = consent.consentType,
            displayName = condition?.displayName ?: consent.consentType.name,
            agreed = consent.agreed,
            agreedAt = consent.agreedAt,
            isRequired = condition?.isRequired ?: false,
        )
    }
}
