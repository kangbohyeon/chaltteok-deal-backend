package com.chaltteok.user.product.service

import com.chaltteok.core.domain.enums.OrderStatus
import com.chaltteok.core.repository.comment.CommentRepository
import com.chaltteok.core.repository.orderitem.OrderItemRepository
import com.chaltteok.core.repository.product.ProductRepository
import com.chaltteok.core.domain.Product
import com.chaltteok.user.product.dto.ProductResponse
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Service
class ProductQueryServiceImpl(
    private val productRepository: ProductRepository,
    private val commentRepository: CommentRepository,
    private val orderItemRepository: OrderItemRepository,
) : ProductQueryService {

    @Transactional(readOnly = true)
    override fun getProducts(): List<ProductResponse> {
        val products = productRepository.findAllActiveByDisplayOrder()
        return buildResponses(products)
    }

    @Transactional(readOnly = true)
    override fun getProductByUuid(productUuid: String): ProductResponse {
        val product = productRepository.findByProductUuid(productUuid)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found: $productUuid")
        return buildResponses(listOf(product)).first()
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
        val salesMap = orderItemRepository.sumQuantityByProductIds(ids, OrderStatus.COMPLETED)
            .associate { it.productId to it.totalQty }
        return products.mapNotNull { product ->
            val id = product.id ?: return@mapNotNull null
            ProductResponse.from(product, countMap[id] ?: 0, ratingMap[id], salesMap[id] ?: 0L)
        }
    }
}
