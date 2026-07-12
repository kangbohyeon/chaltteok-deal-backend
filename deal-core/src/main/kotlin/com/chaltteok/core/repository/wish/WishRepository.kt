package com.chaltteok.core.repository.wish

import com.chaltteok.core.domain.Wish
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface WishRepository : JpaRepository<Wish, Long> {
    @Query("SELECT w FROM Wish w JOIN FETCH w.product WHERE w.userId = :userId ORDER BY w.createdAt DESC")
    fun findByUserIdWithProduct(userId: Long): List<Wish>

    @Modifying
    @Query("DELETE FROM Wish w WHERE w.userId = :userId AND w.product.id = :productId")
    fun deleteByUserIdAndProductId(userId: Long, productId: Long): Int
}
