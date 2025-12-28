package com.chaltteok.core.repository.dailystock

import com.chaltteok.core.domain.DailyStock
import org.springframework.data.jpa.repository.JpaRepository

interface DailyStockRepository : JpaRepository<DailyStock, Long>, DailStockRepositoryCustom {
}