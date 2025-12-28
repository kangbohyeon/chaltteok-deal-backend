package com.chaltteok.owner.product.service


import com.chaltteok.core.repository.product.ProductRepository
import com.chaltteok.core.repository.productoption.ProductOptionRepository
import com.chaltteok.owner.product.dto.ProductRegisterRequest
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
    private val fileUploader: LocalFileUploader
) : ProductService {
    @Transactional
    override fun registerProduct(productRegisterRequest: ProductRegisterRequest, image: MultipartFile?) {
        var imageUrl: String? = null
        logger.info { "image : ${image?.isEmpty}" }
        if (image != null && !image.isEmpty) {
            imageUrl = fileUploader.uploadFile(image)
        }

        val product = productRegisterRequest.toProduct(imageUrl)
        productRepository.save(product)
        logger.info { "product insert success : ${productRegisterRequest.name}" }

        val toProductOption = productRegisterRequest.toProductOption(product)
        productOptionRepository.save(toProductOption)
        logger.info { "product option insert success : ${productRegisterRequest.name}" }

    }

}