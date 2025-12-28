package com.chaltteok.core.repository.productoption

import com.chaltteok.core.domain.ProductOption
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*


interface ProductOptionRepository : JpaRepository<ProductOption, Long>, ProductOptionRepositoryCustom {
    fun findProductOptionByOptionUuid(uuid: String): Optional<ProductOption>
}