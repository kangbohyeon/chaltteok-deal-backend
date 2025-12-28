package com.chaltteok.owner.product.controller

import com.chaltteok.common.dto.ResponseDTO
import com.chaltteok.owner.product.dto.ProductRegisterRequest
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

    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun createProduct(
        @RequestPart("image", required = false) image: MultipartFile?,
        @Valid @RequestPart("data") productRegisterRequest: ProductRegisterRequest
    ):ResponseEntity<ResponseDTO<Any>> {
        productService.registerProduct(productRegisterRequest, image)
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseDTO.success())
    }
}