package com.chaltteok.user.product.controller

import com.chaltteok.common.dto.ResponseDTO
import com.chaltteok.user.product.dto.ProductResponse
import com.chaltteok.user.product.service.ProductQueryService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/v1/user/products")
class ProductQueryController(
    private val productQueryService: ProductQueryService,
) {
    @GetMapping
    fun getProducts(): ResponseDTO<List<ProductResponse>> =
        ResponseDTO.success(productQueryService.getProducts())

    @GetMapping("/{productUuid}")
    fun getProduct(@PathVariable productUuid: String): ResponseDTO<ProductResponse> =
        ResponseDTO.success(productQueryService.getProductByUuid(productUuid))

    @GetMapping("/recommended")
    fun getRecommendedProducts(): ResponseDTO<List<ProductResponse>> =
        ResponseDTO.success(productQueryService.getRecommendedProducts())

    @GetMapping("/search")
    fun searchProducts(@RequestParam keyword: String): ResponseDTO<List<ProductResponse>> {
        if (keyword.isBlank() || keyword.length > 100) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "keyword must be between 1 and 100 characters")
        }
        return ResponseDTO.success(productQueryService.searchProducts(keyword))
    }
}
