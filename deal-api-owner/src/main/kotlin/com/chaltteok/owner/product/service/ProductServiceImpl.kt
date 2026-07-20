package com.chaltteok.owner.product.service

import com.chaltteok.common.exception.BusinessException
import com.chaltteok.core.cache.CacheNames
import com.chaltteok.core.repository.orderitem.OrderItemRepository
import com.chaltteok.core.repository.product.ProductRepository
import com.chaltteok.core.repository.productoption.ProductOptionRepository
import com.chaltteok.owner.product.dto.ProductDetailResponse
import com.chaltteok.owner.product.dto.ProductListResponse
import com.chaltteok.owner.product.dto.ProductRegisterRequest
import com.chaltteok.owner.product.dto.ProductUpdateRequest
import com.chaltteok.owner.product.enums.ProductErrorCode
import com.chaltteok.owner.product.util.LocalFileUploader
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.Caching
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

private val logger = KotlinLogging.logger {}

@Service
class ProductServiceImpl(
    private val productRepository: ProductRepository,
    private val productOptionRepository: ProductOptionRepository,
    private val orderItemRepository: OrderItemRepository,
    private val fileUploader: LocalFileUploader,
) : ProductService {

    @Transactional(readOnly = true)
    override fun getProducts(): List<ProductListResponse> =
        productRepository.findAllWithOption().map { ProductListResponse.from(it) }

    @Cacheable(value = [CacheNames.PRODUCT], key = "#productUuid")
    @Transactional(readOnly = true)
    override fun getProduct(productUuid: String): ProductDetailResponse {
        val row = productRepository.findByProductUuidWithOption(productUuid)
            ?: throw BusinessException(ProductErrorCode.PRODUCT_NOT_FOUND)
        return ProductDetailResponse.from(row)
    }

    @Caching(evict = [
        CacheEvict(value = [CacheNames.PRODUCT_LIST], allEntries = true),
        CacheEvict(value = [CacheNames.PRODUCT_RECOMMENDED], allEntries = true),
    ])
    @Transactional
    override fun registerProduct(request: ProductRegisterRequest, image: MultipartFile?, ownerId: Long) {
        val imageUrl = image?.takeIf { !it.isEmpty }?.let { fileUploader.uploadFile(it) }
        val product = request.toProduct(imageUrl, ownerId)
        productRepository.save(product)
        productOptionRepository.save(request.toProductOption(product))
        logger.info { "product registered: ${request.name}" }
    }

    @Caching(evict = [
        CacheEvict(value = [CacheNames.PRODUCT_LIST], allEntries = true),
        CacheEvict(value = [CacheNames.PRODUCT_RECOMMENDED], allEntries = true),
        CacheEvict(value = [CacheNames.PRODUCT], key = "#productUuid"),
    ])
    @Transactional
    override fun updateProduct(productUuid: String, request: ProductUpdateRequest, image: MultipartFile?) {
        val product = productRepository.findByProductUuid(productUuid)
            ?: throw BusinessException(ProductErrorCode.PRODUCT_NOT_FOUND)

        val newImageUrl = when {
            // 새 이미지 업로드: 기존 이미지 삭제 후 새 이미지 저장
            image != null && !image.isEmpty -> {
                product.imageUrl?.let { fileUploader.deleteFile(it) }
                fileUploader.uploadFile(image)
            }
            // deleteImage=true이고 새 이미지 없음: 기존 이미지만 삭제
            request.deleteImage && image == null -> {
                product.imageUrl?.let { fileUploader.deleteFile(it) }
                null
            }
            // 그 외: 이미지 변경 없음
            else -> product.imageUrl
        }

        product.name = request.name
        product.description = request.descp
        product.price = request.price
        product.isActive = request.isActive
        product.isRecommended = request.isRecommended
        product.imageUrl = newImageUrl
        request.displayOrder?.let { product.displayOrder = it }

        val effectiveStockQuantity = request.stockQuantity ?: product.stockQuantity
        if (request.currentStock != null && effectiveStockQuantity != null
            && request.currentStock > effectiveStockQuantity
        ) {
            throw BusinessException(ProductErrorCode.INVALID_STOCK)
        }

        if (request.stockQuantity != null && request.stockQuantity != product.stockQuantity) {
            product.stockQuantity = request.stockQuantity
            if (request.currentStock == null) {
                product.currentStock = request.stockQuantity
            }
        }

        val prevStock = product.currentStock ?: 0
        if (request.currentStock != null) {
            product.currentStock = request.currentStock
        }
        product.isSoldOut = resolveSoldOut(request, prevStock)
    }

    private fun resolveSoldOut(request: ProductUpdateRequest, prevStock: Int): Boolean = when {
        request.currentStock != null && request.currentStock > 0 && prevStock == 0 -> false
        request.currentStock != null && request.currentStock == 0 -> true
        else -> request.isSoldOut || request.stockQuantity == 0
    }

    @Caching(evict = [
        CacheEvict(value = [CacheNames.PRODUCT_LIST], allEntries = true),
        CacheEvict(value = [CacheNames.PRODUCT_RECOMMENDED], allEntries = true),
        CacheEvict(value = [CacheNames.PRODUCT], key = "#productUuid"),
    ])
    @Transactional
    override fun deleteProduct(productUuid: String) {
        val product = productRepository.findByProductUuid(productUuid)
            ?: throw BusinessException(ProductErrorCode.PRODUCT_NOT_FOUND)
        val productId = product.id ?: throw BusinessException(ProductErrorCode.PRODUCT_NOT_FOUND)
        if (orderItemRepository.existsByProductId(productId)) {
            throw BusinessException(ProductErrorCode.PRODUCT_HAS_ORDERS)
        }
        product.imageUrl?.let { fileUploader.deleteFile(it) }
        productOptionRepository.deleteAllByProductId(productId)
        productRepository.delete(product)
        logger.info { "product deleted: $productUuid" }
    }

    @Caching(evict = [
        CacheEvict(value = [CacheNames.PRODUCT_LIST], allEntries = true),
        CacheEvict(value = [CacheNames.PRODUCT_RECOMMENDED], allEntries = true),
        CacheEvict(value = [CacheNames.PRODUCT], key = "#productUuid"),
    ])
    @Transactional
    override fun toggleActive(productUuid: String) {
        val product = productRepository.findByProductUuid(productUuid)
            ?: throw BusinessException(ProductErrorCode.PRODUCT_NOT_FOUND)
        product.isActive = !product.isActive
    }

    @Caching(evict = [
        CacheEvict(value = [CacheNames.PRODUCT_LIST], allEntries = true),
        CacheEvict(value = [CacheNames.PRODUCT], key = "#productUuid"),
    ])
    @Transactional
    override fun toggleSoldOut(productUuid: String) {
        val product = productRepository.findByProductUuid(productUuid)
            ?: throw BusinessException(ProductErrorCode.PRODUCT_NOT_FOUND)
        product.isSoldOut = !product.isSoldOut
    }

    @Caching(evict = [
        CacheEvict(value = [CacheNames.PRODUCT_LIST], allEntries = true),
        CacheEvict(value = [CacheNames.PRODUCT_RECOMMENDED], allEntries = true),
        CacheEvict(value = [CacheNames.PRODUCT], key = "#productUuid"),
    ])
    @Transactional
    override fun toggleRecommend(productUuid: String) {
        val product = productRepository.findByProductUuid(productUuid)
            ?: throw BusinessException(ProductErrorCode.PRODUCT_NOT_FOUND)
        product.isRecommended = !product.isRecommended
    }
}
