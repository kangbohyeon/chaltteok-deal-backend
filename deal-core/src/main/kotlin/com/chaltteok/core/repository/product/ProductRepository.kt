package com.chaltteok.core.repository.product

import com.chaltteok.core.domain.Product
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ProductRepository : JpaRepository<Product, Long>, ProductRepositoryCustom {
    fun findAllByIsActiveTrue(): List<Product>
    fun findAllByIsActiveTrueOrderByDisplayOrderAscNameAsc(): List<Product>
    fun findAllByIsActiveTrueAndIsRecommendedTrue(): List<Product>
    fun findByProductUuid(productUuid: String): Product?

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id IN :ids")
    fun findAllByIdInWithLock(@Param("ids") ids: Collection<Long>): List<Product>

    @Modifying
    @Query("UPDATE Product p SET p.currentStock = p.stockQuantity, p.isSoldOut = false WHERE p.stockQuantity IS NOT NULL AND p.stockQuantity > 0")
    fun resetDailyStockForActiveProducts(): Int

    @Modifying
    @Query("UPDATE Product p SET p.currentStock = 0, p.isSoldOut = true WHERE p.stockQuantity = 0")
    fun markZeroStockAsSoldOut(): Int
}