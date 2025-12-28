package com.chaltteok.owner.service

import com.chaltteok.owner.dto.ProductRegisterRequest
import org.springframework.web.multipart.MultipartFile

interface ProductService {
    fun registerProduct(productRegisterRequest: ProductRegisterRequest,image: MultipartFile?)
}