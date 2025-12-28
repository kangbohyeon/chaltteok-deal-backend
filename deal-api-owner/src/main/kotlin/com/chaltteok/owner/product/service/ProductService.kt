package com.chaltteok.owner.product.service

import com.chaltteok.owner.product.dto.ProductRegisterRequest
import org.springframework.web.multipart.MultipartFile

interface ProductService {
    fun registerProduct(productRegisterRequest: ProductRegisterRequest, image: MultipartFile?)
}