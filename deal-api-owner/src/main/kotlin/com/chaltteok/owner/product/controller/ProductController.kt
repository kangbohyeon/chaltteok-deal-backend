package com.chaltteok.owner.product.controller

import com.chaltteok.common.dto.ResponseDTO
import com.chaltteok.owner.product.dto.ProductDetailResponse
import com.chaltteok.owner.product.dto.ProductListResponse
import com.chaltteok.owner.product.dto.ProductRegisterRequest
import com.chaltteok.owner.product.dto.ProductUpdateRequest
import com.chaltteok.owner.product.service.ProductService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/v1/owner/products")
class ProductController(private val productService: ProductService) {

    @GetMapping
    fun getProducts(): ResponseEntity<ResponseDTO<List<ProductListResponse>>> =
        ResponseEntity.ok(ResponseDTO.success(productService.getProducts()))

    @GetMapping("/{productUuid}")
    fun getProduct(@PathVariable productUuid: String): ResponseEntity<ResponseDTO<ProductDetailResponse>> =
        ResponseEntity.ok(ResponseDTO.success(productService.getProduct(productUuid)))

    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun createProduct(
        @RequestPart("image", required = false) image: MultipartFile?,
        @Valid @RequestPart("data") request: ProductRegisterRequest,
    ): ResponseEntity<ResponseDTO<Any>> {
        productService.registerProduct(request, image)
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseDTO.success())
    }

    @PutMapping("/{productUuid}", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun updateProduct(
        @PathVariable productUuid: String,
        @RequestPart("image", required = false) image: MultipartFile?,
        @Valid @RequestPart("data") request: ProductUpdateRequest,
    ): ResponseEntity<ResponseDTO<Any>> {
        productService.updateProduct(productUuid, request, image)
        return ResponseEntity.ok(ResponseDTO.success())
    }

    @DeleteMapping("/{productUuid}")
    fun deleteProduct(@PathVariable productUuid: String): ResponseEntity<ResponseDTO<Any>> {
        productService.deleteProduct(productUuid)
        return ResponseEntity.ok(ResponseDTO.success())
    }

    @PatchMapping("/{productUuid}/active")
    fun toggleActive(@PathVariable productUuid: String): ResponseEntity<ResponseDTO<Any>> {
        productService.toggleActive(productUuid)
        return ResponseEntity.ok(ResponseDTO.success())
    }

    @PatchMapping("/{productUuid}/soldout")
    fun toggleSoldOut(@PathVariable productUuid: String): ResponseEntity<ResponseDTO<Any>> {
        productService.toggleSoldOut(productUuid)
        return ResponseEntity.ok(ResponseDTO.success())
    }

    @PatchMapping("/{productUuid}/recommend")
    fun toggleRecommend(@PathVariable productUuid: String): ResponseEntity<ResponseDTO<Any>> {
        productService.toggleRecommend(productUuid)
        return ResponseEntity.ok(ResponseDTO.success())
    }
}
