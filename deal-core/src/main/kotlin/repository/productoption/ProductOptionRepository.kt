package repository.productoption

import domain.ProductOption
import org.springframework.data.jpa.repository.JpaRepository


interface ProductOptionRepository : JpaRepository<ProductOption, Long>,ProductOptionRepositoryCustom {
}