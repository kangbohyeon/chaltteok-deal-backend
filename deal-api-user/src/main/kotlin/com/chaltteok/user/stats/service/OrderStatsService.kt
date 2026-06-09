package com.chaltteok.user.stats.service

import java.time.LocalDate

interface OrderStatsService {
    fun incrementOrderStats(date: LocalDate, revenue: Long)
    fun incrementCancelStats(date: LocalDate)
}
