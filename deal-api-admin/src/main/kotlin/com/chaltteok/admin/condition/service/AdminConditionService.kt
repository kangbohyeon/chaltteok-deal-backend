package com.chaltteok.admin.condition.service

import com.chaltteok.core.domain.ConsentCondition

interface AdminConditionService {
    fun findAll(): List<ConsentCondition>
    fun toggleRequired(id: Long)
    fun toggleActive(id: Long)
}
