package com.chaltteok.core.repository.product

import com.chaltteok.core.domain.Product
import com.chaltteok.core.repository.product.dto.ProductWithOptionRow

interface ProductRepositoryCustom {
    fun findAllWithOption(): List<ProductWithOptionRow>
    fun searchByKeyword(keyword: String): List<Product>
}