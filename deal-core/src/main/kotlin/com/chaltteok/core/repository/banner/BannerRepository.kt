package com.chaltteok.core.repository.banner

import com.chaltteok.core.domain.Banner
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate

interface BannerRepository : JpaRepository<Banner, Long> {

    fun findByBannerUuid(bannerUuid: String): Banner?

    @Query("""
        SELECT b FROM Banner b
        WHERE b.isVisible = true
        AND (b.startDate IS NULL OR b.startDate <= :today)
        AND (b.endDate IS NULL OR b.endDate >= :today)
        ORDER BY b.sortOrder ASC, b.createdAt DESC
    """)
    fun findActiveBanners(@Param("today") today: LocalDate): List<Banner>
}
