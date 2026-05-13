package com.chaltteok.user.product.service

import com.chaltteok.core.repository.product.ProductRepository
import com.chaltteok.user.product.dto.ProductResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProductQueryServiceImpl(
    private val productRepository: ProductRepository,
) : ProductQueryService {

    @Transactional(readOnly = true)
    override fun getProducts(): List<ProductResponse> =
        productRepository.findAllByIsActiveTrue()
            .map { ProductResponse.from(it) }

    @Transactional(readOnly = true)
    override fun getRecommendedProducts(): List<ProductResponse> =
        productRepository.findAllByIsActiveTrueAndIsRecommendedTrue()
            .map { ProductResponse.from(it) }
}
