package repository.eventhistory

import domain.EventHistory
import org.springframework.data.jpa.repository.JpaRepository

interface EventHistoryRepository : JpaRepository<EventHistory, Long>, EventHistoryRepositoryCustom {
}