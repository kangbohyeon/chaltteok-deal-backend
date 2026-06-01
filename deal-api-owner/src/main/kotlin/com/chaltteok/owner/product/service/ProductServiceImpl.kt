package com.chaltteok.owner.product.service

import com.chaltteok.common.exception.BusinessException
import com.chaltteok.core.repository.product.ProductRepository
import com.chaltteok.core.repository.productoption.ProductOptionRepository
import com.chaltteok.owner.product.dto.ProductListResponse
import com.chaltteok.owner.product.dto.ProductRegisterRequest
import com.chaltteok.owner.product.dto.ProductUpdateRequest
import com.chaltteok.owner.product.enums.ProductErrorCode
import com.chaltteok.owner.product.util.LocalFileUploader
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

private val logger = KotlinLogging.logger {}

@Service
class ProductServiceImpl(
    private val productRepository: ProductRepository,
    private val productOptionRepository: ProductOptionRepository,
    private val fileUploader: LocalFileUploader,
) : ProductService {

    @Transactional(readOnly = true)
    override fun getProducts(): List<ProductListResponse> =
        productRepository.findAllWithOption().map { ProductListResponse.from(it) }

    @Transactional
    override fun registerProduct(request: ProductRegisterRequest, image: MultipartFile?) {
        val imageUrl = image?.takeIf { !it.isEmpty }?.let { fileUploader.uploadFile(it) }
        val product = request.toProduct(imageUrl)
        productRepository.save(product)
        productOptionRepository.save(request.toProductOption(product))
        logger.info { "product registered: ${request.name}" }
    }

    @Transactional
    override fun updateProduct(productUuid: String, request: ProductUpdateRequest, image: MultipartFile?) {
        val product = productRepository.findByProductUuid(productUuid)
            ?: throw BusinessException(ProductErrorCode.PRODUCT_NOT_FOUND)
        val newImageUrl = image?.takeIf { !it.isEmpty }?.let { newImage ->
            product.imageUrl?.let { fileUploader.deleteFile(it) }
            fileUploader.uploadFile(newImage)
        }

        product.name = request.name
        product.description = request.descp
        product.price = request.price
        product.isActive = request.isActive
        product.isSoldOut = request.isSoldOut || request.stockQuantity == 0
        product.isRecommended = request.isRecommended
        if (newImageUrl != null) product.imageUrl = newImageUrl
        if (request.stockQuantity != null && request.stockQuantity != product.stockQuantity) {
            product.stockQuantity = request.stockQuantity
            product.currentStock = request.stockQuantity
        } else if (request.stockQuantity != null) {
            product.stockQuantity = request.stockQuantity
        }
    }

    @Transactional
    override fun deleteProduct(productUuid: String) {
        val product = productRepository.findByProductUuid(productUuid)
            ?: throw BusinessException(ProductErrorCode.PRODUCT_NOT_FOUND)
        product.imageUrl?.let { fileUploader.deleteFile(it) }
        productOptionRepository.deleteAll(
            productOptionRepository.findAll().filter { it.product.id == product.id }
        )
        productRepository.delete(product)
        logger.info { "product deleted: $productUuid" }
    }

    @Transactional
    override fun toggleActive(productUuid: String) {
        val product = productRepository.findByProductUuid(productUuid)
            ?: throw BusinessException(ProductErrorCode.PRODUCT_NOT_FOUND)
        product.isActive = !product.isActive
    }

    @Transactional
    override fun toggleSoldOut(productUuid: String) {
        val product = productRepository.findByProductUuid(productUuid)
            ?: throw BusinessException(ProductErrorCode.PRODUCT_NOT_FOUND)
        product.isSoldOut = !product.isSoldOut
    }

    @Transactional
    override fun toggleRecommend(productUuid: String) {
        val product = productRepository.findByProductUuid(productUuid)
            ?: throw BusinessException(ProductErrorCode.PRODUCT_NOT_FOUND)
        product.isRecommended = !product.isRecommended
    }
}
