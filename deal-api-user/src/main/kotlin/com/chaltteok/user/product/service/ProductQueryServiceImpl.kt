package com.chaltteok.user.product.service

import com.chaltteok.core.repository.comment.CommentRepository
import com.chaltteok.core.repository.product.ProductRepository
import com.chaltteok.core.domain.Product
import com.chaltteok.user.product.dto.ProductResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProductQueryServiceImpl(
    private val productRepository: ProductRepository,
    private val commentRepository: CommentRepository,
) : ProductQueryService {

    @Transactional(readOnly = true)
    override fun getProducts(): List<ProductResponse> {
        val products = productRepository.findAllByIsActiveTrue()
        return buildResponses(products)
    }

    @Transactional(readOnly = true)
    override fun getRecommendedProducts(): List<ProductResponse> {
        val products = productRepository.findAllByIsActiveTrueAndIsRecommendedTrue()
        return buildResponses(products)
    }

    @Transactional(readOnly = true)
    override fun searchProducts(keyword: String): List<ProductResponse> {
        val products = productRepository.searchByKeyword(keyword)
        return buildResponses(products)
    }

    private fun buildResponses(products: List<Product>): List<ProductResponse> {
        if (products.isEmpty()) return emptyList()
        val ids = products.mapNotNull { it.id }
        val countMap = commentRepository.countRootCommentsByProductIds(ids)
            .associate { it.productId to it.cnt.toInt() }
        val ratingMap = commentRepository.avgRatingByProductIds(ids)
            .associate { it.productId to it.avg }
        return products.map {
            ProductResponse.from(it, countMap[it.id!!] ?: 0, ratingMap[it.id!!])
        }
    }
}
