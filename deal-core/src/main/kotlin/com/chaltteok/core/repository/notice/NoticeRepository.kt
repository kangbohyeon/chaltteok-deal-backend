package com.chaltteok.core.repository.notice

import com.chaltteok.core.domain.Notice
import org.springframework.data.jpa.repository.JpaRepository

interface NoticeRepository : JpaRepository<Notice, Long> {
    fun findByNoticeUuid(uuid: String): Notice?
    fun findAllByIsVisibleTrueOrderByCreatedAtDesc(): List<Notice>
}
