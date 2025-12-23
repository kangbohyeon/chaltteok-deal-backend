package repository.owner

import domain.Owner
import org.springframework.data.jpa.repository.JpaRepository

interface OwnerRepository : JpaRepository<Owner, Long>, OwnerRepositoryCustom {
}