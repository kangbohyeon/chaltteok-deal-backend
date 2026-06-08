package com.chaltteok.core.repository.productoption

import com.chaltteok.core.domain.Product
import com.chaltteok.core.domain.ProductOption
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.transaction.annotation.Transactional
import java.util.*


interface ProductOptionRepository : JpaRepository<ProductOption, Long>, ProductOptionRepositoryCustom {
    fun findProductOptionByOptionUuid(uuid: String): Optional<ProductOption>
    fun findFirstByProduct(product: Product): Optional<ProductOption>
    fun findAllByProductIn(products: List<Product>): List<ProductOption>

    @Modifying
    @Transactional
    @Query("DELETE FROM ProductOption po WHERE po.product.id = :productId")
    fun deleteAllByProductId(@Param("productId") productId: Long)
}