package com.chaltteok.core.repository.owner

import com.chaltteok.core.domain.Owner
import org.springframework.data.jpa.repository.JpaRepository

interface OwnerRepository : JpaRepository<Owner, Long>, OwnerRepositoryCustom {
}