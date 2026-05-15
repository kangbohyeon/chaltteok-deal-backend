package com.chaltteok.user.product.controller

import com.chaltteok.common.dto.ResponseDTO
import com.chaltteok.user.product.dto.ProductResponse
import com.chaltteok.user.product.service.ProductQueryService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/user/products")
class ProductQueryController(
    private val productQueryService: ProductQueryService,
) {
    @GetMapping
    fun getProducts(): ResponseDTO<List<ProductResponse>> =
        ResponseDTO.success(productQueryService.getProducts())

    @GetMapping("/recommended")
    fun getRecommendedProducts(): ResponseDTO<List<ProductResponse>> =
        ResponseDTO.success(productQueryService.getRecommendedProducts())

    @GetMapping("/search")
    fun searchProducts(@RequestParam keyword: String): ResponseDTO<List<ProductResponse>> =
        ResponseDTO.success(productQueryService.searchProducts(keyword))
}
