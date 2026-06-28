package com.chaltteok.admin.owner.service

import com.chaltteok.core.domain.Owner
import com.chaltteok.core.repository.owner.OwnerRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Service
class AdminOwnerServiceImpl(
    private val ownerRepository: OwnerRepository,
) : AdminOwnerService {

    @Transactional(readOnly = true)
    override fun findAll(pageable: Pageable): Page<Owner> =
        ownerRepository.findAll(pageable)

    @Transactional(readOnly = true)
    override fun findByOwnerUuid(ownerUuid: String): Owner =
        ownerRepository.findByOwnerUuid(ownerUuid)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "점주를 찾을 수 없습니다: $ownerUuid")
}
