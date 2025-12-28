package com.chaltteok.core.repository.product

import com.chaltteok.core.domain.Product
import org.springframework.data.jpa.repository.JpaRepository

interface ProductRepository : JpaRepository<Product, Long>,ProductRepositoryCustom{

}