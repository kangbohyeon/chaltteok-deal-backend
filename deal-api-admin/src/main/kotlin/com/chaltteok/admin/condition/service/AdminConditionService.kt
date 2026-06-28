package com.chaltteok.admin.condition.service

import com.chaltteok.core.domain.ConsentCondition

interface AdminConditionService {
    fun findAll(): List<ConsentCondition>
    fun toggleRequired(conditionUuid: String)
    fun toggleActive(conditionUuid: String)
}
