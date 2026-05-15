package com.chaltteok.user.product.service

import com.chaltteok.user.product.dto.ProductResponse

interface ProductQueryService {
    fun getProducts(): List<ProductResponse>
    fun getRecommendedProducts(): List<ProductResponse>
    fun searchProducts(keyword: String): List<ProductResponse>
}
