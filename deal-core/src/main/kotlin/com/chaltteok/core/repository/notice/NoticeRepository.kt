package com.chaltteok.core.repository.notice

import com.chaltteok.core.domain.Notice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate
import java.time.LocalTime

interface NoticeRepository : JpaRepository<Notice, Long> {
    fun findByNoticeUuid(uuid: String): Notice?

    @Query("""
        SELECT n FROM Notice n
        WHERE n.isVisible = true
        AND (n.startDate IS NULL OR n.startDate <= :today)
        AND (n.endDate IS NULL OR n.endDate >= :today)
        AND (n.startTime IS NULL OR n.startTime <= :now)
        AND (n.endTime IS NULL OR n.endTime >= :now)
        ORDER BY n.createdAt DESC
    """)
    fun findActiveNotices(@Param("today") today: LocalDate, @Param("now") now: LocalTime): List<Notice>
}
