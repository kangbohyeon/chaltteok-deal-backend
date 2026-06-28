package com.chaltteok.core.repository.consentcondition

import com.chaltteok.core.domain.ConsentCondition
import org.springframework.data.jpa.repository.JpaRepository

interface ConsentConditionRepository : JpaRepository<ConsentCondition, Long> {
    fun findAllByOrderByDisplayOrderAsc(): List<ConsentCondition>
    fun findByConditionUuid(conditionUuid: String): ConsentCondition?
}
