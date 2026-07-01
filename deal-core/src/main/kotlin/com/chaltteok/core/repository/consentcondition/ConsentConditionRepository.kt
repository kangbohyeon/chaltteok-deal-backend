package com.chaltteok.core.repository.consentcondition

import com.chaltteok.core.domain.ConsentCondition
import com.chaltteok.core.domain.enums.ConsentType
import org.springframework.data.jpa.repository.JpaRepository

interface ConsentConditionRepository : JpaRepository<ConsentCondition, Long> {
    fun findAllByOrderByDisplayOrderAsc(): List<ConsentCondition>
    fun findByConditionUuid(conditionUuid: String): ConsentCondition?
    fun findByConsentType(consentType: ConsentType): ConsentCondition?
}
