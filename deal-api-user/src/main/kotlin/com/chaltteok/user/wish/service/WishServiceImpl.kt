package com.chaltteok.user.wish.service

import com.chaltteok.core.domain.Product
import com.chaltteok.core.domain.Wish
import com.chaltteok.core.repository.product.ProductRepository
import com.chaltteok.core.repository.wish.WishRepository
import com.chaltteok.user.wish.dto.WishListResponse
import com.chaltteok.user.wish.dto.WishResponse
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Service
class WishServiceImpl(
    private val wishRepository: WishRepository,
    private val productRepository: ProductRepository,
) : WishService {

    @Transactional(readOnly = true)
    override fun getWishes(userId: Long): WishListResponse {
        val wishes = wishRepository.findByUserIdWithProduct(userId)
            .map { WishResponse.from(it) }
        return WishListResponse(wishes = wishes, totalCount = wishes.size)
    }

    @Transactional
    override fun addWish(userId: Long, productUuid: String) {
        val product = resolveProduct(productUuid)
        try {
            wishRepository.save(Wish(userId = userId, product = product))
        } catch (e: DataIntegrityViolationException) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "이미 찜한 상품입니다: $productUuid")
        }
    }

    @Transactional
    override fun removeWish(userId: Long, productUuid: String) {
        val product = resolveProduct(productUuid)
        val deleted = wishRepository.deleteByUserIdAndProductId(userId, product.id!!)
        if (deleted == 0) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "찜 내역을 찾을 수 없습니다: $productUuid")
        }
    }

    private fun resolveProduct(productUuid: String): Product =
        productRepository.findByProductUuid(productUuid)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다: $productUuid")
}
