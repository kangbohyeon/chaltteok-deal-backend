package com.chaltteok.core.repository.eventhistory

import com.chaltteok.core.domain.EventHistory
import org.springframework.data.jpa.repository.JpaRepository

interface EventHistoryRepository : JpaRepository<EventHistory, Long>, EventHistoryRepositoryCustom {
}