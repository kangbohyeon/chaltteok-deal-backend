package com.chaltteok.core.repository.productoption

import com.chaltteok.core.domain.ProductOption
import org.springframework.data.jpa.repository.JpaRepository


interface ProductOptionRepository : JpaRepository<ProductOption, Long>,ProductOptionRepositoryCustom {
}