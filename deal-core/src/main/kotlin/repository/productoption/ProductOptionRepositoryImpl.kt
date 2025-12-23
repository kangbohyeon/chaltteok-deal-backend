package repository.productoption

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class ProductOptionRepositoryImpl(private val jpaQueryFactory: JPAQueryFactory):ProductOptionRepositoryCustom {
}