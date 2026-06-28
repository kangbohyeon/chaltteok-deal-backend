package com.chaltteok.admin.condition.service

import com.chaltteok.core.domain.ConsentCondition
import com.chaltteok.core.repository.consentcondition.ConsentConditionRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AdminConditionServiceImpl(
    private val consentConditionRepository: ConsentConditionRepository,
) : AdminConditionService {

    @Transactional(readOnly = true)
    override fun findAll(): List<ConsentCondition> =
        consentConditionRepository.findAllByOrderByDisplayOrderAsc()

    @Transactional
    override fun toggleRequired(id: Long) {
        val condition = consentConditionRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Consent condition not found: $id") }
        condition.isRequired = !condition.isRequired
    }

    @Transactional
    override fun toggleActive(id: Long) {
        val condition = consentConditionRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Consent condition not found: $id") }
        condition.isActive = !condition.isActive
    }
}
