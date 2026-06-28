package com.chaltteok.admin.condition.service

import com.chaltteok.core.domain.ConsentCondition
import com.chaltteok.core.repository.consentcondition.ConsentConditionRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Service
class AdminConditionServiceImpl(
    private val consentConditionRepository: ConsentConditionRepository,
) : AdminConditionService {

    @Transactional(readOnly = true)
    override fun findAll(): List<ConsentCondition> =
        consentConditionRepository.findAllByOrderByDisplayOrderAsc()

    @Transactional
    override fun toggleRequired(conditionUuid: String) {
        val condition = findByUuid(conditionUuid)
        condition.isRequired = !condition.isRequired
    }

    @Transactional
    override fun toggleActive(conditionUuid: String) {
        val condition = findByUuid(conditionUuid)
        condition.isActive = !condition.isActive
    }

    private fun findByUuid(conditionUuid: String): ConsentCondition =
        consentConditionRepository.findByConditionUuid(conditionUuid)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "조건을 찾을 수 없습니다: $conditionUuid")
}
