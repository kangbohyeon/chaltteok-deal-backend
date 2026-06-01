package com.chaltteok.core.repository.product

import com.chaltteok.core.domain.Product
import com.chaltteok.core.domain.QProduct
import com.chaltteok.core.domain.QProductOption
import com.chaltteok.core.repository.product.dto.ProductWithOptionRow
import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class ProductRepositoryImpl(private val jpaQueryFactory: JPAQueryFactory) : ProductRepositoryCustom {

    override fun findAllWithOption(): List<ProductWithOptionRow> {
        val qProduct = QProduct.product
        val qOption = QProductOption.productOption
        return jpaQueryFactory
            .select(
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
                    qOption.optionUuid,
                    qOption.price,
                )
            )
            .from(qProduct)
            .join(qOption).on(qOption.product.id.eq(qProduct.id))
            .orderBy(qProduct.id.desc())
            .fetch()
    }

    override fun searchByKeyword(keyword: String): List<Product> {
        val qProduct = QProduct.product
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
