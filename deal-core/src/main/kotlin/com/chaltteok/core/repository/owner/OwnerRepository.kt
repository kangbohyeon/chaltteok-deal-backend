package com.chaltteok.core.repository.owner

import com.chaltteok.core.domain.Owner
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface OwnerRepository : JpaRepository<Owner, Long>, OwnerRepositoryCustom {
    fun findByUsername(username: String): Optional<Owner>
}