package com.chaltteok.core.repository.consent

import com.chaltteok.core.domain.UserConsent
import com.chaltteok.core.domain.enums.ConsentType
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface UserConsentRepository : JpaRepository<UserConsent, Long> {
    fun findByUserIdAndConsentType(userId: Long, consentType: ConsentType): Optional<UserConsent>
    fun findAllByUserId(userId: Long): List<UserConsent>
}
