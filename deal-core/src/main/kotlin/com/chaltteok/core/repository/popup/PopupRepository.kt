package com.chaltteok.core.repository.popup

import com.chaltteok.core.domain.Popup
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate
import java.time.LocalTime

interface PopupRepository : JpaRepository<Popup, Long> {
    fun findByPopupUuid(uuid: String): Popup?

    @Query("""
        SELECT p FROM Popup p
        WHERE p.isVisible = true
        AND (p.startDate IS NULL OR p.startDate <= :today)
        AND (p.endDate IS NULL OR p.endDate >= :today)
        AND (p.startTime IS NULL OR p.startTime <= :now)
        AND (p.endTime IS NULL OR p.endTime >= :now)
        ORDER BY p.createdAt DESC
    """)
    fun findActivePopups(@Param("today") today: LocalDate, @Param("now") now: LocalTime): List<Popup>
}
