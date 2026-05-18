package com.chaltteok.user.product.service

import com.chaltteok.core.repository.comment.CommentRepository
import com.chaltteok.core.repository.product.ProductRepository
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
        val countMap = commentRepository.countRootCommentsByProductIds(products.mapNotNull { it.id })
            .associate { it.productId to it.cnt.toInt() }
        return products.map { ProductResponse.from(it, countMap[it.id!!] ?: 0) }
    }

    @Transactional(readOnly = true)
    override fun getRecommendedProducts(): List<ProductResponse> {
        val products = productRepository.findAllByIsActiveTrueAndIsRecommendedTrue()
        val countMap = commentRepository.countRootCommentsByProductIds(products.mapNotNull { it.id })
            .associate { it.productId to it.cnt.toInt() }
        return products.map { ProductResponse.from(it, countMap[it.id!!] ?: 0) }
    }

    @Transactional(readOnly = true)
    override fun searchProducts(keyword: String): List<ProductResponse> {
        val products = productRepository.searchByKeyword(keyword)
        val countMap = commentRepository.countRootCommentsByProductIds(products.mapNotNull { it.id })
            .associate { it.productId to it.cnt.toInt() }
        return products.map { ProductResponse.from(it, countMap[it.id!!] ?: 0) }
    }
}
