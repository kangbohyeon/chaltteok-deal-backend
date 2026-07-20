package com.chaltteok.core.repository.consent

import com.chaltteok.core.domain.UserConsentHistory
import com.chaltteok.core.domain.enums.ConsentType
import org.springframework.data.jpa.repository.JpaRepository

interface UserConsentHistoryRepository : JpaRepository<UserConsentHistory, Long> {
    fun findAllByUserIdOrderByChangedAtDesc(userId: Long): List<UserConsentHistory>
    fun findAllByUserIdAndConsentTypeOrderByChangedAtDesc(userId: Long, consentType: ConsentType): List<UserConsentHistory>
}
