package com.chaltteok.core.repository.product

import com.chaltteok.core.domain.Product
import com.chaltteok.core.domain.QProduct
import com.chaltteok.core.domain.QProductOption
import com.chaltteok.core.repository.product.dto.ProductWithOptionRow
import com.querydsl.core.types.Expression
import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class ProductRepositoryImpl(private val jpaQueryFactory: JPAQueryFactory) : ProductRepositoryCustom {

    private val qProduct = QProduct.product
    private val qOption = QProductOption.productOption

    private fun productWithOptionProjection(): Expression<ProductWithOptionRow> =
        Projections.constructor(
            ProductWithOptionRow::class.java,
            qProduct.id,
            qProduct.productUuid,
            qProduct.name,
            qProduct.description,
            qProduct.imageUrl,
            qProduct.price,
            qProduct.isActive,
            qProduct.isSoldOut,
            qProduct.isRecommended,
            qProduct.stockQuantity,
            qProduct.currentStock,
            qProduct.displayOrder,
            qOption.optionUuid,
            qOption.price,
        )

    override fun findAllWithOption(): List<ProductWithOptionRow> =
        jpaQueryFactory
            .select(productWithOptionProjection())
            .from(qProduct)
            .join(qOption).on(qOption.product.id.eq(qProduct.id))
            .orderBy(qProduct.displayOrder.asc(), qProduct.id.asc())
            .fetch()

    // 상품당 옵션이 1개인 비즈니스 규칙에 따라 fetchFirst()로 대표 옵션 조회
    override fun findByProductUuidWithOption(productUuid: String): ProductWithOptionRow? =
        jpaQueryFactory
            .select(productWithOptionProjection())
            .from(qProduct)
            .join(qOption).on(qOption.product.id.eq(qProduct.id))
            .where(qProduct.productUuid.eq(productUuid))
            .fetchFirst()

    override fun searchByKeyword(keyword: String): List<Product> {
        return jpaQueryFactory
            .selectFrom(qProduct)
            .where(
                qProduct.isActive.isTrue,
                qProduct.name.containsIgnoreCase(keyword),
            )
            .orderBy(qProduct.name.asc())
            .limit(200)
            .fetch()
    }
}
