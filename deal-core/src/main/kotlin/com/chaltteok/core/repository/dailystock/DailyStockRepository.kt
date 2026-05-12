package com.chaltteok.core.repository.dailystock

import com.chaltteok.core.domain.DailyStock
import com.chaltteok.core.domain.enums.DailyStockStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface DailyStockRepository : JpaRepository<DailyStock, Long>, DailStockRepositoryCustom {
    @Query("SELECT ds FROM DailyStock ds JOIN FETCH ds.product WHERE ds.status = :status")
    fun findAllByStatusWithProduct(status: DailyStockStatus): List<DailyStock>
}