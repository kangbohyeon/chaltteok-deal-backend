package com.chaltteok.owner.product.service

import com.chaltteok.owner.product.dto.ProductDetailResponse
import com.chaltteok.owner.product.dto.ProductListResponse
import com.chaltteok.owner.product.dto.ProductRegisterRequest
import com.chaltteok.owner.product.dto.ProductUpdateRequest
import org.springframework.web.multipart.MultipartFile

interface ProductService {
    fun getProducts(): List<ProductListResponse>
    fun getProduct(productUuid: String): ProductDetailResponse
    fun registerProduct(request: ProductRegisterRequest, image: MultipartFile?)
    fun updateProduct(productUuid: String, request: ProductUpdateRequest, image: MultipartFile?)
    fun deleteProduct(productUuid: String)
    fun toggleActive(productUuid: String)
    fun toggleSoldOut(productUuid: String)
    fun toggleRecommend(productUuid: String)
}
