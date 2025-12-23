package repository.dailystock

import domain.DailyStock
import org.springframework.data.jpa.repository.JpaRepository

interface DailyStockRepository : JpaRepository<DailyStock, Long>, DailStockRepositoryCustom {
}