package com.chaltteok.owner.dailystock.service

import com.chaltteok.common.exception.BusinessException
import com.chaltteok.core.domain.Product
import com.chaltteok.core.domain.ProductOption
import com.chaltteok.core.repository.dailystock.DailyStockRepository
import com.chaltteok.core.repository.productoption.ProductOptionRepository
import com.chaltteok.owner.dailystock.dto.DailyStocksRegisterRequest
import com.chaltteok.owner.dailystock.enums.DailyStockErrorCode
import com.chaltteok.owner.dailystock.enums.DailyStockType
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.dao.DataIntegrityViolationException
import java.time.LocalDate
import java.util.*

@ExtendWith(MockKExtension::class)
class DailyStockServiceImplTest {
    @MockK
    lateinit var dailyStockRepository: DailyStockRepository

    @MockK
    lateinit var productOptionRepository: ProductOptionRepository

    @InjectMockKs
    lateinit var dailyStockService: DailyStockServiceImpl

    private fun createRequest(
        price: Int? = 1000,
        type: DailyStockType? = DailyStockType.NORMAL
    ): DailyStocksRegisterRequest {
        return DailyStocksRegisterRequest(
            optionId = "8c15b634-ddbf-4c06-bd9d-696c48f50692",
            saleDate = LocalDate.now(),
            stockType = type,
            salePrice = price,
            totalQty = 10,
            status = "OPEN"
        )
    }

    private fun createProductOption(price: Int = 5000): ProductOption {
        val product = Product(
            name = "꿀떡",
            imageUrl = "./uploads/products/be5ef121-2fef-43e1-9968-acd4a918769c.jpeg",
            isActive = true
        )
        val option = ProductOption(
            optionName = "기본옵션",
            product = product,
            price = price
        )

        return io.mockk.mockk(relaxed = true) {
            every { this@mockk.price } returns price
            every { this@mockk.id } returns 1L
        }
    }

    @Test
    @DisplayName("정상 등록: 판매가가 입력되면 해당 가격으로 저장된다")
    fun registerDailyStock_Success_WithInputPrice() {
        // Given
        val request = createRequest(price = 2000)
        val option = createProductOption(price = 5000)

        every { productOptionRepository.findProductOptionByOptionUuid(any()) } returns Optional.of(option)
        every { dailyStockRepository.save(any()) } returns mockk()

        // When
        dailyStockService.registerDailyStock(request)

        // Then
        val slot = slot<com.chaltteok.core.domain.DailyStock>()
        verify(exactly = 1) { dailyStockRepository.save(capture(slot)) }

        assertThat(slot.captured.salePrice).isEqualTo(2000)
    }

    @Test
    @DisplayName("정상 등록: 판매가가 null이면 옵션의 원가(price)로 저장된다")
    fun registerDailyStock_Success_WithDefaultPrice() {
        // Given
        val request = createRequest(price = null)
        val option = createProductOption(price = 5000)

        every { productOptionRepository.findProductOptionByOptionUuid(any()) } returns Optional.of(option)
        every { dailyStockRepository.save(any()) } returns mockk()

        // When
        dailyStockService.registerDailyStock(request)

        // Then
        val slot = slot<com.chaltteok.core.domain.DailyStock>()
        verify(exactly = 1) { dailyStockRepository.save(capture(slot)) }

        assertThat(slot.captured.salePrice).isEqualTo(5000)
    }

    @Test
    @DisplayName("실패: 존재하지 않는 옵션 ID로 요청 시 예외 발생")
    fun registerDailyStock_Fail_InvalidId() {
        // Given
        val request = createRequest()
        every { productOptionRepository.findProductOptionByOptionUuid(any()) } returns Optional.empty()

        // When & Then
        val exception = assertThrows<BusinessException> {
            dailyStockService.registerDailyStock(request)
        }
        assertThat(exception.errorCode).isEqualTo(DailyStockErrorCode.INVALID_ID)
    }

    @Test
    @DisplayName("실패: 이벤트(EVENT) 타입인데 가격이 0원이면 예외 발생")
    fun registerDailyStock_Fail_EventPriceZero() {
        // Given
        val request = createRequest(price = 0, type = DailyStockType.EVENT)
        val option = createProductOption()

        every { productOptionRepository.findProductOptionByOptionUuid(any()) } returns Optional.of(option)

        // When & Then
        val exception = assertThrows<BusinessException> {
            dailyStockService.registerDailyStock(request)
        }
        assertThat(exception.errorCode).isEqualTo(DailyStockErrorCode.EVENT_PRICE_REQUIRED)
    }

    @Test
    @DisplayName("실패: 이미 등록된 재고(중복)인 경우 예외 발생")
    fun registerDailyStock_Fail_Duplicate() {
        // Given
        val request = createRequest()
        val option = createProductOption()

        every { productOptionRepository.findProductOptionByOptionUuid(any()) } returns Optional.of(option)

        every { dailyStockRepository.save(any()) } throws DataIntegrityViolationException("Duplicate entry")

        // When & Then
        val exception = assertThrows<BusinessException> {
            dailyStockService.registerDailyStock(request)
        }
        assertThat(exception.errorCode).isEqualTo(DailyStockErrorCode.DUPLICATE_STOCK)

        verify(exactly = 1) { dailyStockRepository.save(any()) }
    }
}