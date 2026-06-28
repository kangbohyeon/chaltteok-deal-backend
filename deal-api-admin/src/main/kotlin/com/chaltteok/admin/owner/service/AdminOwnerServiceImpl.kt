package com.chaltteok.admin.owner.service

import com.chaltteok.core.domain.Owner
import com.chaltteok.core.repository.owner.OwnerRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AdminOwnerServiceImpl(
    private val ownerRepository: OwnerRepository,
) : AdminOwnerService {

    @Transactional(readOnly = true)
    override fun findAll(pageable: Pageable): Page<Owner> =
        ownerRepository.findAll(pageable)

    @Transactional(readOnly = true)
    override fun findById(id: Long): Owner =
        ownerRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Owner not found: $id") }
}
